package com.ibm.caas.sdktest.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASRequestResult;
import com.ibm.caas.CAASService;
import com.ibm.caas.sdktest.R;
import com.ibm.caas.sdktest.util.Constants;
import com.ibm.caas.sdktest.util.GenericCache;
import com.ibm.caas.sdktest.util.Settings;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends Activity {
  // UI references.
  AutoCompleteTextView serverView;
  AutoCompleteTextView macmContextView;
  AutoCompleteTextView macmTenantView;
  AutoCompleteTextView macmLibView;
  AutoCompleteTextView userView;
  EditText passwordView;
  View progressView;
  View loginFormView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // Set up the login form.
    serverView = (AutoCompleteTextView) findViewById(R.id.server);
    macmContextView = (AutoCompleteTextView) findViewById(R.id.macm_context);
    macmTenantView = (AutoCompleteTextView) findViewById(R.id.macm_tenant);
    macmLibView = (AutoCompleteTextView) findViewById(R.id.macm_lib);
    userView = (AutoCompleteTextView) findViewById(R.id.user);
    passwordView = (EditText) findViewById(R.id.password);
    passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        return true;
      }
    });

    Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
    mSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });
    loginFormView = findViewById(R.id.login_form);
    progressView = findViewById(R.id.login_progress);
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  public void attemptLogin() {
    // Reset errors.
    userView.setError(null);
    passwordView.setError(null);
    // Store values at the time of the login attempt.
    Settings.server = serverView.getText().toString();
    Settings.macmContext = macmContextView.getText().toString();
    Settings.macmTenant = macmTenantView.getText().toString();
    Settings.macmLib = macmLibView.getText().toString();
    Settings.user = userView.getText().toString();
    Settings.password = passwordView.getText().toString();

    // Show a progress spinner, and kick off a background task to perform the user login attempt.
    CAASService service = new CAASService(Settings.server, Settings.macmContext, Settings.macmTenant, Settings.user, Settings.password);
    service.setAndroidContext(getApplicationContext());
    service.setAllowUntrustedCertificates(true);
    GenericCache.getInstance().put(Constants.SERVER, service);
    CAASDataCallback<Void> callback = new CAASDataCallback<Void>() {
      @Override
      public void onSuccess(CAASRequestResult<Void> requestResult) {
        LoginActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            showProgress(false);
          }
        });
        startActivity(new Intent(getApplicationContext(), ContentTypesActivity.class));
      }

      @Override
      public void onError(final CAASErrorResult error) {
        LoginActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            showProgress(false);
            String msg = error.getMessage();
            int code = error.getStatusCode();
            userView.setError("connection error" + (msg != null ? ": " + msg : (code > 0 ? ": status code " + code : "")) + " - please try again");
          }
        });
      }
    };
    showProgress(true);
    service.signIn(Settings.user, Settings.password, callback);
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  public void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow for very easy animations. If available, use these APIs to fade-in the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
      loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });
      progressView.setVisibility(show ? View.VISIBLE : View.GONE);
      progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show and hide the relevant UI components.
      progressView.setVisibility(show ? View.VISIBLE : View.GONE);
      loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }
}
