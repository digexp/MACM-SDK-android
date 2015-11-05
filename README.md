# MACM Android SDK

The Android SDK is an Android API to retrieve content from a MACM server.

### Table Of Contents

1. [Installation](#installation)
    1. [SDK](#sdk)
    2. [Sample app](#sample-app)
2. [Getting started](#getting-started)
    1. [Authentication](#authentication)
        1. [Authentication with the credentials of the application](#authentication-with-the-credentials-of-the-application)
        2. [Authentication with the credentials of the end user](#authentication-with-the-credentials-of-the-end-user)
    2. [Retrieving Content](#retrieving-content)
        1. [List of content items by path](#querying-a-list-of-content-items-by-path)
        2. [List of content items by id](#querying-a-list-of-content-items-by-id)
        3. [Single content item by id](#querying-a-single-content-item-by-id)
        4. [Single content item by path](#querying-a-single-content-item-by-path)
    3. [Querying an asset (image) by its url](#querying-an-asset-image-by-its-url)
3. [Miscellaneous](#miscellaneous)
    1. [Allowing untrusted certificates for HTTPS connections](#allowing-untrusted-certificates-with-https-connections)
    2. [Enabling geo-location retrieval](#enabling-geo-location-retrieval)
4. [Javadoc](#javadoc)

## Installation
### SDK

The SDK may be installed by referencing the **MACM-SDK-android-xxx.aar** android archive found in this repository's *dist/* folder.
For instance, this can be done as follows in your build.gradle script, assuming the .aar file was copied in the app's 'libs/' folder:

```groovy
repositories {
  flatDir {
    dirs 'libs'
  }
}

dependencies {
  compile(name: 'MACM-SDK-android-release', ext: 'aar')
}
```

### Sample app

To install and run the sample app 'AndroidSDKTestApp':
* clone this Git repostiory
* import it in Android Studio
* the app can now be built and run from Android Studio as any other porject


## Getting started
### Authentication

Authentication with the remote server can be done in two ways:
#### Authentication with the credentials of the application

The username and password are hardcoded in the application and the following constructor should be used:

```java
// create a service instance with application credentials
CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "username", "password");
```

#### Authentication with the credentials of the end user

* The users must sign in against a MACM server with their own credentials, the following constructor must be used:

```java
// create a service instance without credentials
CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant");
```

* When the users provide their credentials, the following API checks these credentials against the MACM server:

```java
// handle the sign-in result asynchronously
CAASDataCallback<Void> callback = new CAASDataCallback<Void>() {
  @Override
  public void onSuccess(Void result) {
    // sign-in successful
  }

  @Override
  public void onError(CAASErrorResult error) {
    // handle the error
  }
};
// perform the explicit sign-in with user credentials
CAASRequestResult<Void> result = service.signIn("username", "password", callback);
```

### Retrieving content
#### Querying a list of content items by path

```java
// create the service that connects to the MACM instance
CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "username", "password");
// handle the request result asynchronously
CAASDataCallback<CAASContentItemsList> callback = new CAASDataCallback<CAASContentItemsList>() {
 @Override
 public void onSuccess(CAASContentItemsList itemsList) {
   List<CAASContentItem> items = itemsList.getContentItems();
   // do something with the list of items
 }

 @Override
 public void onError(CAASErrorResult error) {
   // handle the error
 }
};
// create the request
CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
request.setPath("MACM/Content Types/Book"); // path to "Book" content items
// include the specified properties in the response
request.addProperties("id", "contenttype", "title", "lastmodifieddate", "categories", "keywords");
// request the url to the cover image
request.addElements("cover");
// request the first 10 items
request.setPageSize(10);
request.setPageNumber(1);
// execute the request
CAASRequestResult<CAASContentItemsList> result = service.executeRequest(request);
```

#### Querying a list of content items by id

```java
// creates the service that connects to the MACM instance
CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "username", "password");
// handle the request result asynchronously
CAASDataCallback<CAASContentItemsList> callback = ...;
// create the request
CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
// id of the content items list to retrieve
request.setOid("5f2c3fee-3994-45d4-9570-a6aa67ff2250");
// sort by descending status then ascending title
request.addSortDescriptor("status", false);
request.addSortDescriptor("title", true);
// only include the published items that have any of the specified keywords
request.setWorkflowStatus(CAASContentItemsRequest.WorkflowStatus.Published);
request.addAnyKeywords("bestseller", "top10");
// execute the request
CAASRequestResult<CAASContentItemsList> result = service.executeRequest(request);
```

#### Querying a single content item by id

```java
// creates the service that connects to the MACM instance
CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "username", "password");
// handle the request result asynchronously
CAASDataCallback<CAASContentItem> callback = new CAASDataCallback<CAASContentItem>() {
  @Override
  public void onSuccess(CAASContentItem item) {
    // do something with the item
  }

  @Override
  public void onError(CAASErrorResult error) {
    // handle the error
  }
};

// create the request
CAASContentItemRequest request = new CAASContentItemRequest(callback);
// id of the content item to retrieve
request.setOid("5f2c3fee-3994-45d4-9570-a6aa67ff2250");
// execute the request
CAASRequestResult<CAASContentItem> result = service.executeRequest(request);
```

#### Querying a single content item by path

```java
// create the service that connects to the MACM instance
CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "username", "password");
CAASDataCallback<CAASContentItem> callback = ...;
// create the request
CAASContentItemRequest request = new CAASContentItemRequest(callback);
request.setPath("MACM/some/content item path");
// execute the request
CAASRequestResult<CAASContentItem> result = service.executeRequest(request);
```

### Querying an asset (image) by its url

```java
// create the service that connects to the MACM instance
CAASService service = ...;
final Context context = ...;
final ImageView imageView = ...;
// url retrieved from a previous request
// this is a partial URL, the CAASService instance will infer the full URL
final String url = "/wps/wcm/myconnect/80686c98-9264-4391-bc00-3a039f4dc0b3/cover.jpg?MOD=AJPERES"
// callback that retrieves the image and sets it on a view
CAASDataCallback<byte[]> callback = new CAASDataCallback<byte[]>() {
  @Override
  public void onSuccess(byte[] bytes) {
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
    imageView.setImageDrawable(drawable);
  }

  @Override
  public void onError(CAASErrorResult error) {
    Log.e(LOG_TAG, "failed to load image at " + url + ", with error: " + error);
  }
};
CAASAssetRequest request = new CAASAssetRequest(url, callback);
service.executeRequest(request);
```

## Miscellaneous
### Allowing untrusted certificates with HTTPS connections

By default, Android only allows trusted certificates whose certificate authority is in its trusted list.
To disable this behavior, for instance to test with seff-signed certificates, the `allowUntrustedCertificates`
flag must be set to `true', as follows:

```java
// create the service that connects to the MACM instance
CAASService service = new CAASService("https://www.myhost.com:10042", "MyContextRoot", "MyTenant", "username", "password");
service.setAllowUntrustedCertificates(true);
...
```

### Enabling geo-location retrieval

For the Android SDK to access the available location providers, the application must provide an Android application [Context](http://developer.android.com/reference/android/content/Context.html).
This can be done as in the following example:

```java
public class MyActivity extends Activity {
  ...

  public void doLogin() {
    CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "user", "password");
    service.setAndroidContext(getApplicationContext());
    ...;
  }
}
```

## Javadoc

The Javadoc is available on the [MACM Android SDK GitHub pages](https://digexp.github.io/MACM-SDK-android)
