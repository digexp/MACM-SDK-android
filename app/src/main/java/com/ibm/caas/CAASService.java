/*
 ********************************************************************
 * Licensed Materials - Property of IBM                             *
 *                                                                  *
 * Copyright IBM Corp. 2015 All rights reserved.                    *
 *                                                                  *
 * US Government Users Restricted Rights - Use, duplication or      *
 * disclosure restricted by GSA ADP Schedule Contract with          *
 * IBM Corp.                                                        *
 *                                                                  *
 * DISCLAIMER OF WARRANTIES. The following [enclosed] code is       *
 * sample code created by IBM Corporation. This sample code is      *
 * not part of any standard or IBM product and is provided to you   *
 * solely for the purpose of assisting you in the development of    *
 * your applications. The code is provided "AS IS", without         *
 * warranty of any kind. IBM shall not be liable for any damages    *
 * arising out of your use of the sample code, even if they have    *
 * been advised of the possibility of such damages.                 *
 ********************************************************************
 */

package com.ibm.caas;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.ibm.caas.sdk.BuildConfig;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Implementation of the service which communicates with the server to authenticate and retrieve content.
 */
public class CAASService {
  /**
   * Log tag for this class.
   */
  private final static String LOG_TAG = CAASService.class.getSimpleName();
  /**
   * This flag is set to <code>true</code> when the Android version name is NOT JellyBean.
   * <p>This addresses a problem in Android Jelly Bean, which causes the password authentication data in {@link Authenticator} to never be used (sigh).
   * <p>For more details, see this <a href="http://stackoverflow.com/questions/14550131/http-basic-authentication-issue-on-android-jelly-bean-4-1-using-httpurlconnectio">StackOverflow thread</a>
   */
  private final static boolean USE_AUTHENTICATOR = (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN);
  /**
   * Name of the header sent by the server in a response to indicate a basic authentication challenge.
   */
  private final static String AUTHENTICATION_CHALLENGE_HEADER = "WWW-Authenticate";
  private CookieManager manager = new CookieManager();
  /**
   * The base server URL, for instance <code>http[s]://hostname[:port]</code>.
   */
  private String serverURL;
  /**
   * The context root, generally "wps".
   */
  private String contextRoot;
  /**
   * MACM instance name, may be null, in which case it not used.
   */
  private String instance;
  /**
   * The user name for HTTP authentication.
   */
  private String userName;
  /**
   * The user password for HTTP authentication.
   */
  private String password;
  /**
   * Contextual information about the mobile device using the SDK.
   */
  private MobileContext mobileContext;
  /**
   * Whether to send mobile contextual information with each requests.
   */
  private boolean sendMobileInformation = true;
  /**
   * Android application <code>Context</code> required to access location services.
   */
  private Context androidContext = null;
  /**
   * Whether to allow untrusted certificates for HTTPS connections.
   */
  private boolean allowUntrustedCertificates = false;
  /**
   * Validates any host name, used only when {@link #allowUntrustedCertificates} is <code>true</code>.
   */
  private static HostnameVerifier untrustedHostNameVerifier = new HostnameVerifier() {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  };
  /**
   * Validates any certificate chain, used only when {@link #allowUntrustedCertificates} is <code>true</code>.
   */
  private static TrustManager[] untrustedTrustManagers = new TrustManager[] { new X509TrustManager() {
    @Override
    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
  }};
  /**
   * SSL socket factory that validates any certificate chain, used only when {@link #allowUntrustedCertificates} is <code>true</code>.
   */
  private static SSLSocketFactory untrustedSocketFactory = null;
  static {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, untrustedTrustManagers, new SecureRandom());
      untrustedSocketFactory = sslContext.getSocketFactory();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Measures the time taken by one or more http requests.
   */
  private PerformanceMeasurement performanceHook = new PerformanceMeasurement();

  /**
   * Initialize this service with the specified server URL, context root, tenant name and credentials.
   * @param serverURL the base server URL in the form <code>http[s]://hostname[:port]</code>.
   * @param contextRoot the web context root name, for instance "wps".
   * @param instance instance or tenant name, which may be null in which case it not used.
   * @param userName the user name for HTTP authentication.
   * @param password the user password for HTTP authentication.
   */
  public CAASService(String serverURL, String contextRoot, String instance, String userName, String password) {
    if ((userName == null) || (password == null)) {
      throw new IllegalArgumentException(Utils.localize("com.ibm.caas.nullCredentials"));
    }
    this.userName = userName;
    this.password = password;
    initialize(serverURL, contextRoot, instance);
  }

  /**
   * Initialize this service with the specified server URL, context root and tenant.
   * @param serverURL the base server URL in the form <code>http[s]://hostname[:port]</code>.
   * @param contextRoot the web context root name, for instance "wps".
   * @param instance instance or tenant name, which may be null in which case it not used.
   */
  public CAASService(String serverURL, String contextRoot, String instance) {
    initialize(serverURL, contextRoot, instance);
  }

  private void initialize(String serverURL, String contextRoot, String instance) {
    this.serverURL = serverURL;
    this.contextRoot = contextRoot;
    this.instance = instance;
    if (USE_AUTHENTICATOR) {
      // handles basic auth automatically
      Authenticator auth = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(getUserName(), getPassword().toCharArray());
        }
      };
      Authenticator.setDefault(auth);
    }
    Log.v(LOG_TAG, "BuildConfig.DEBUG = " + BuildConfig.DEBUG);
    mobileContext = new MobileContext();
  }

  /**
   * Get the base server URL.
   * @return a string representing the base server URL in the form <code>http[s]://hostname[:port]</code>.
   */
  public String getServerURL() {
    return serverURL;
  }

  /**
   * Get the context root for the MACM server. For instance: "wps".
   * @return the context root name.
   */
  public String getContextRoot() {
    return contextRoot;
  }

  /**
   * Return the MACM instance name.
   */
  public String getInstance() {
    return instance;
  }

  /**
   * Return the user name for HTTP authentication.
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * Return the password for HTTP authentication.
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * Get the Android application <code>Context</code> required to access location services.
   */
  public Context getAndroidContext() {
    return androidContext;
  }

  /**
   * Get the Android application <code>Context</code> required to access location services.
   */
  public void setAndroidContext(Context androidContext) {
    mobileContext.setContext(androidContext);
  }

  /**
   * Perform server basic authentication using the server URL, context root and tenant provided in the constructor.
   * @param userName the user name for HTTP authentication.
   * @param password the user password for HTTP authentication.
   * @param callback callback for asynchronous propagation of the authentication result.
   * @return a {@link CAASRequestResult} object encapsulating the result of the authentication request.
   */
  public CAASRequestResult<Void> signIn(final String userName, final String password, final CAASDataCallback<Void> callback) {
    Log.d(LOG_TAG, "attempting authentication ...");
    this.userName = userName;
    this.password = password;
    manager = new CookieManager();
    //Log.d(LOG_TAG, "BuildConfig.DEBUG = " + BuildConfig.DEBUG);
    final CAASRequestResult<Void> requestResult = new CAASRequestResult<Void>(null);
    AsyncTask<Void, Void, CAASRequestResult<Void>> task = new AsyncTask<Void, Void, CAASRequestResult<Void>>() {
      @Override
      protected CAASRequestResult<Void> doInBackground(Void... params) {
        int statusCode = -1;
        try {
          Log.d(LOG_TAG, "attempting with USE_AUTHENTICATOR = " + USE_AUTHENTICATOR);
          statusCode = authenticateSync();
          if ((statusCode >= 400) && (statusCode != 404)) {
            CAASErrorResult error = new CAASErrorResult(statusCode, null, Utils.localize("com.ibm.caas.authFailure", statusCode));
            requestResult.setError(error);
            callback.onError(error);
          } else {
            callback.onSuccess(null);
          }
        } catch (Exception e) {
          Log.e(LOG_TAG, "error authenticating: " + e.getClass().getName() + " " + Log.getStackTraceString(e));
          CAASErrorResult error = new CAASErrorResult(statusCode, e, Utils.localize("com.ibm.caas.authFailure", statusCode));
          requestResult.setError(error);
          callback.onError(error);
        }
        return requestResult;
      }
    };
    requestResult.setAsyncTask(task);
    task.execute();
    return requestResult;
  }

  /**
   * Perform basic auth synchronously.
   * This method should always be called from an <code>AsyncTask</code>.
   * @return the HTTP response code for the authentication request.
   * @throws Exception if any error occurs.
   */
  private int authenticateSync() throws Exception {
    int statusCode = -1;
    CookieHandler tmpManager = CookieHandler.getDefault();
    try {
      URL url = new URL(getBasicAuthenticationURI().toString());
      manager = new CookieManager();
      manager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
      CookieHandler.setDefault(manager);
      HttpURLConnection connection = handleConnection((HttpURLConnection) url.openConnection());
      Log.d(LOG_TAG, "authenticateSync() url = " + connection.getURL());
      connection.setInstanceFollowRedirects(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      if (!USE_AUTHENTICATOR) {
        setBasicAuthHeader(connection);
      }
      setUserAgentHeader(connection);
      try {
        statusCode = connection.getResponseCode();
      } catch(IOException e) {
        statusCode = connection.getResponseCode();
      }
      logResponseHeaders(connection);
    } finally {
      CookieHandler.setDefault(tmpManager);
    }
    return statusCode;
  }

  /**
   * Execute the specified request against the server.
   * @param <T> the type of result returned from the server.
   * @param request the request to execute.
   * @return an object encapsulating the result of the request.
   */
  public <T> CAASRequestResult<T> executeRequest(final CAASRequest<T> request) {
    Log.d(LOG_TAG, "executeRequest()");
    CAASRequestResult<T> requestResult = new CAASRequestResult<T>(request);
    RequestAsyncTask<T> task = new RequestAsyncTask<T>(request, requestResult);
    requestResult.setAsyncTask(task);
    task.execute();
    return requestResult;
  }

  /**
   * Determine whether to send mobile contextual information with each requests.
   * @return <code>true</code> if this service is configured to send the mobile information, <code>false</code> otherwise.
   */
  public boolean isSendMobileInformation() {
    return sendMobileInformation;
  }

  /**
   * Specify whether to send mobile contextual information with each requests.
   * @param sendMobileInformation <code>true</code> to configure this service to send the mobile information, <code>false</code> otherwise.
   */
  public void setSendMobileInformation(boolean sendMobileInformation) {
    this.sendMobileInformation = sendMobileInformation;
  }

  /**
   * Determine whether untrusted certificates, such as self-signed certificates or certificates
   * whose CA is not in the Android's trusted list, are allowed for HTTPS connections.
   * @return <code>true</code> if untrusted certificates are alloowed, <code>false</code> otherwise.
   */
  public boolean isAllowUntrustedCertificates() {
    return allowUntrustedCertificates;
  }

  /**
   * Specify whether untrusted certificates, such as self-signed certificates or certificates
   * whose CA is not in the Android's trusted list, are allowed for HTTPS connections.
   * @param allowUntrustedCertificates <code>true</code> to allow untrusted certificates are alloowed, <code>false</code> to deny them.
   */
  public void setAllowUntrustedCertificates(boolean allowUntrustedCertificates) {
    this.allowUntrustedCertificates = allowUntrustedCertificates;
  }

  /**
   * Get the object which holds session identifiers.
   * This object can be used to perform furtther requests to the server with the same HTTP session.
   * @return A {@link CookieManager} instance.
   */
  public CookieManager getCookieManager() {
    return manager;
  }

  /**
   * Set the header for basic authentication onto the specified connection.
   * @param connection the connection used for autheitcation.
   * @throws Exception if any error occurs.
   */
  private void setBasicAuthHeader(HttpURLConnection connection) throws Exception {
    String credentials = getUserName() + ":" + getPassword();
    String encoded = Base64.encodeToString(credentials.getBytes(Utils.UTF_8), Base64.NO_WRAP);
    connection.setRequestProperty("Authorization", "Basic " + encoded);
  }

  /**
   * Set the header for basic authentication onto the specified connection.
   * @param connection the connection used for autheitcation.
   * @throws Exception if any error occurs.
   */
  private void setUserAgentHeader(HttpURLConnection connection) throws Exception {
    connection.setRequestProperty(Utils.HTTP_HEADER_USER_AGENT, System.getProperty(Utils.PROPERTY_HTTP_AGENT));
  }

  /**
   * Extract the body of an HTTP response as a string.
   * @param connection the connection to read from.
   * @return the string extracted from the connection's input stream.
   * @throws Exception if any error occurs.
   */
  private String readResponseBody(HttpURLConnection connection) throws Exception {
    return readString(new BufferedInputStream(connection.getInputStream()));
  }

  /**
   * Determine whether the response contains text data, based on the "Content-Type" header.
   * @param connection the connection from which the response is extracted.
   * @return <code>true</code> if the response contains text data, <code>false</code> otherwise.
   * @throws Exception if any eror occurs.
   */
  private boolean isTextResponseBody(HttpURLConnection connection) throws Exception {
    Map<String, List<String>> headers = connection.getHeaderFields();
    List<String> types = headers.get("Content-Type");
    if (types == null) {
      types = new ArrayList<String>();
    }
    String type = types.isEmpty() ? null : types.get(0);
    Log.d(LOG_TAG, "response content type = [" + type + "]");
    return !((type == null) || (!type.contains("application/json") && !type.contains("text/html")));
    //return !((type == null) || (!"application/json".equals(type) && !"text/html".equals(type)));
  }

  /**
   * Extract the error of an HTTP response as a string.
   * @param connection the connection to read from.
   * @return the string extracted from the connection's error stream.
   * @throws Exception if any error occurs.
   */
  private String readErrorBody(HttpURLConnection connection) throws Exception {
    return readString(new BufferedInputStream(connection.getErrorStream()));
  }

  /**
   * Read a string content from a stream.
   * @param in the stream to read from.
   * @return the string extracted frm the stream.
   * @throws Exception if any error occurs.
   */
  private String readString(InputStream in) throws Exception {
    return new String(readBytes(in), Utils.UTF_8);
  }

  /**
   * Read bytes from an HTTP connection.
   * @param connection the connection to read from.
   * @return an array of the bytes read.
   * @throws Exception if any error occurs.
   */
  private byte[] readBytes(HttpURLConnection connection) throws Exception {
    return readBytes(new BufferedInputStream(connection.getInputStream()));
  }

  /**
   * Read a string content from a stream.
   * @param in the stream to read from.
   * @return the raw bytes extracted from the stream.
   * @throws Exception if any error occurs.
   */
  private byte[] readBytes(InputStream in) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[2048];
    int n;
    while ((n = in.read(buffer)) > 0) {
      out.write(buffer, 0, n);
    }
    out.close();
    return out.toByteArray();
  }

  /**
   * Return the base URL for querying content.
   * @return the URL as a {@link Uri} object.
   * @throws Exception if any error occurs.
   */
  Uri getBaseQueryURI() throws Exception {
    URL url = new URL(getServerURL());
    int port = url.getPort();
    String portStr = (port < 0) ? "" : ":" + port;
    Uri.Builder builder = new Uri.Builder().scheme(url.getProtocol()).encodedAuthority(url.getHost() + portStr)
      .appendEncodedPath(getContextRoot())
      .appendEncodedPath("myportal");
    String instance = getInstance();
    if ((instance != null) && !"".equals(instance.trim())) {
      builder.appendEncodedPath(getInstance());
    }
    return builder.build();
  }

  /**
   * Return the base URL for basic authentication.
   * @return the URL as a {@link Uri} object.
   * @throws Exception if any error occurs.
   */
  private Uri getBasicAuthenticationURI() throws Exception {
    URL url = new URL(getServerURL());
    int port = url.getPort();
    String portStr = (port < 0) ? "" : ":" + port;
    Uri.Builder builder = new Uri.Builder().scheme(url.getProtocol()).encodedAuthority(url.getHost() + portStr)
      .appendEncodedPath(getContextRoot())
      .appendEncodedPath("mycontenthandler");
    String instance = getInstance();
    if ((instance != null) && !"".equals(instance.trim())) {
      builder.appendEncodedPath(getInstance());
    }
    builder.appendQueryParameter("uri", "login:basicauth");
    return builder.build();
  }

  /**
   * Print the HTTP request headers to Logcat. This method is for tracing and debugging purposes only.
   * @param connection the HTTP connection from which to extract the headers.
   * @throws Exception if any error occurs.
   */
  private void logRequestHeaders(HttpURLConnection connection) throws Exception {
    StringBuilder sb = new StringBuilder();
    Map<String, List<String>> headers = connection.getRequestProperties();
    for (Map.Entry<String, List<String>> entry: headers.entrySet()) {
      sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
    }
    Log.d(LOG_TAG, "request header:\n" + sb.toString());
  }

  /**
   * Print the HTTP reponse headers to Logcat. This method is for tracing and debugging purposes only.
   * @param connection the HTTP connection from which to extract the headers.
   * @throws Exception if any error occurs.
   */
  private void logResponseHeaders(HttpURLConnection connection) throws Exception {
    try {
      StringBuilder sb = new StringBuilder();
      Map<String, List<String>> headers = connection.getHeaderFields();
      for (Map.Entry<String, List<String>> entry: headers.entrySet()) {
        sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
      }
      Log.d(LOG_TAG, "response header:\n" + sb.toString());
    } catch(Exception e) {
      Log.d(LOG_TAG, "error logging response headers: ", e);
    }
  }

  private void setRequestCookies(HttpURLConnection connection) throws Exception {
    List<HttpCookie> list = new ArrayList<HttpCookie>(manager.getCookieStore().getCookies());
    boolean hasLoginType = false;
    for (HttpCookie cookie: list) {
      if ("ibm.login.type".equals(cookie.getName())) {
        hasLoginType = true;
        break;
      }
    }
    if (!hasLoginType) {
      HttpCookie cookie = new HttpCookie("ibm.login.type", "basicauth");
      manager.getCookieStore().add(null, cookie);
      list.add(cookie);
      //cookie.setDomain(url.getHost());
    }
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<list.size(); i++) {
      if (i > 0) sb.append(',');
      sb.append(list.get(i).toString());
    }
    connection.setRequestProperty("Cookie", sb.toString());
  }

  /**
   * Determine whether the response contains a basic authentication challenge.
   * @param connection the connection from which the response is extracted.
   * @return <code>true</code> if the response contains an authentication challenge, <code>false</code> otherwise.
   */
  private boolean hasAuthenticationChallenge(HttpURLConnection connection) {
    Map<String, List<String>> headers = connection.getHeaderFields();
    return headers.get(AUTHENTICATION_CHALLENGE_HEADER) != null;
  }

  /**
   * Log the error response of an HTTP request.
   * @param connection the connection encapsulating the request and response.
   */
  private void logErrorBody(HttpURLConnection connection) {
    try {
      String err = readErrorBody(connection);
      Log.d(LOG_TAG, "error body = " + err);
    } catch(Exception ignore) {
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('[');
    sb.append("serverURL=").append(serverURL);
    sb.append(", contextRoot=").append(contextRoot);
    sb.append(", instance=").append(instance);
    sb.append(", userName=").append(userName);
    sb.append(']');
    return sb.toString();
  }

  /**
   * Executes a request to fetch content asynchronously.
   * @param <T> the type of result returned by the request.
   */
  private class RequestAsyncTask<T> extends AsyncTask<Void, Void, CAASRequestResult<T>> {
    private final CAASRequest<T> request;
    private final CAASRequestResult<T> requestResult;

    private RequestAsyncTask(CAASRequest<T> request, CAASRequestResult<T> requestResult) {
      this.request = request;
      this.requestResult = requestResult;
    }

    @Override
    protected CAASRequestResult<T> doInBackground(Void... params) {
      int statusCode = -1;
      boolean reauthenticationRequired = false;
      boolean done = false;
      CookieHandler tmpManager = CookieHandler.getDefault();
      try {
        CookieHandler.setDefault(manager);
        StringBuilder sb = new StringBuilder();
        String query = request.buildQuery(CAASService.this);
        sb.append(query);
        if (isSendMobileInformation()) {
          for (Map.Entry<String, Object> entry: mobileContext.getDeviceInfo()) {
            sb.append('&').append(request.encodeParam(entry.getKey(), entry.getValue()));
          }
        }
        URL url = new URL(sb.toString());
        while (!done) {
          HttpURLConnection connection = handleConnection((HttpURLConnection) url.openConnection());
          connection.setRequestProperty(Utils.HTTP_HEADER_ACCEPT_LANGUAGE, Locale.getDefault().toString());
          setUserAgentHeader(connection);
          Log.d(LOG_TAG, "request url = " + connection.getURL());
          connection.setInstanceFollowRedirects(true);
          setRequestCookies(connection);
          if (reauthenticationRequired) {
            setBasicAuthHeader(connection);
          }
          logRequestHeaders(connection);
          // workaround for issue described at http://stackoverflow.com/q/17121213
          long time = System.nanoTime();
          try {
            statusCode = connection.getResponseCode();
            time = System.nanoTime() - time;
          } catch(IOException e) {
            time = System.nanoTime() - time;
            statusCode = connection.getResponseCode();
            if (statusCode != 401) {
              Log.e(LOG_TAG, "code " + statusCode + ", exception: ", e);
            }
          } finally {
            performanceHook.newTime(time / 1000000d);
            logResponseHeaders(connection);
          }
          requestResult.setResponseHeaders(connection.getHeaderFields());
          if (statusCode != 200) {
            CAASErrorResult error;
            logErrorBody(connection);
            // if version is >= JellyBean and an authentication challenge is issued, then handle re-authentication and resend the request
            if ((statusCode == 401) && !reauthenticationRequired && !USE_AUTHENTICATOR && hasAuthenticationChallenge(connection)) {
              Log.d(LOG_TAG, "re-authenticating due to auth challenge...");
              reauthenticationRequired = true;
              continue;
            } else {
              error = new CAASErrorResult(statusCode, null, Utils.localize("com.ibm.caas.queryFailure", url));
            }
            requestResult.setError(error);
            return requestResult;
          }
          done = true;
          requestResult.setContentType(connection.getContentType());
          byte[] body = readBytes(connection);
          if (isTextResponseBody(connection)) {
            String bodyStr = new String(body, Utils.UTF_8);
            Log.d(LOG_TAG, "doInBackground() response body = " + bodyStr);
          }
          T result = request.resultFromResponse(body);
          Log.d(LOG_TAG, String.format("status code for GET request = %d, url = %s", statusCode, url));
          requestResult.setResult(result);
        }
      } catch(Exception e) {
        CAASErrorResult error = new CAASErrorResult(statusCode, e, e.getMessage());
        requestResult.setError(error);
      } finally {
        CookieHandler.setDefault(tmpManager);
      }
      return requestResult;
    }

    @Override
    protected void onPostExecute(CAASRequestResult<T> result) {
      CAASDataCallback<T> callback = request.getCallback();
      if (result.getError() != null) {
        callback.onError(result.getError());
      } else {
        callback.onSuccess(result);
      }
    }
  }

  private HttpURLConnection handleConnection(HttpURLConnection connection) throws Exception {
    if (allowUntrustedCertificates && "https".equalsIgnoreCase(connection.getURL().getProtocol())) {
      ((HttpsURLConnection) connection).setHostnameVerifier(untrustedHostNameVerifier);
      ((HttpsURLConnection) connection).setSSLSocketFactory(untrustedSocketFactory);
    }
    return connection;
  }

  /**
   * Get the object that measures the time taken by one or more http requests.
   * @return a {@link PerformanceMeasurement} instance.
   */
  public String getPerformanceStats() {
    return performanceHook.toString();
  }
}
