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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Provides a global application context.
 */
class MobileContext {
  /**
   * Log tag for this class.
   */
  private final static String LOG_TAG = MobileContext.class.getSimpleName();
  /**
   * Contains the information available and gathered on the device.
   */
  private final GenericMap deviceInfo = new GenericMap();

  /**
   * The application context required to retrieve screen and location information.
   */
  Context context;

  MobileContext() {
    initDeviceInfo();
  }

  GenericMap getDeviceInfo() {
    return deviceInfo;
  }

  private void initDeviceInfo() {
    try {
      if (context != null) {
        computeLocation();
        //computeScale();
      }
      synchronized(deviceInfo) {
        /*
        deviceInfo.set("ibm.mobile.os", "Android");
        deviceInfo.set("ibm.mobile.systemVersion", Build.VERSION.RELEASE);
        deviceInfo.set("ibm.mobile.machineName", Build.BRAND);
        deviceInfo.set("ibm.mobile.locale", Locale.getDefault());
        */
      }
    } catch(Exception e) {
      Log.d(LOG_TAG, "error initializing device info: ", e);
    }
    Log.d(LOG_TAG, "initDeviceInfo() deviceInfo=" + deviceInfo);
  }

  void setContext(Context context) {
    Context prev = this.context;
    this.context = context;
    if ((prev == null) && (context != null)) {
      try {
        computeLocation();
      } catch(Exception e) {
        Log.d(LOG_TAG, "error initializing geolocation info: ", e);
      }
    }
  }

  /**
   * Compute the location data (latitude / longitude)
   * @throws Exception if any error occurs.
   */
  private void computeLocation() throws Exception {
    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    String[] providerNames = { LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER };
    LocationProvider provider = null;
    for (String name: providerNames) {
      try {
        if (lm.isProviderEnabled(name)) {
          provider = lm.getProvider(name);
          break;
        }
      } catch(SecurityException ignore) {
      }
    }
    Log.d(LOG_TAG, "location provider = " + (provider == null ? "null" : provider.getName()));
    if (provider != null) {
      updateLocation(lm.getLastKnownLocation(provider.getName()));
      lm.requestLocationUpdates(provider.getName(), 0, 0, new ContextLocationListener());
    }
  }

  /**
   * Update the device info witht he specified location.
   * @param location the location to update with.
   */
  private void updateLocation(Location location) {
    if (location != null) {
      synchronized(deviceInfo) {
        deviceInfo.set("wp_ct_latitude", location.getLatitude());
        deviceInfo.set("wp_ct_longitude", location.getLongitude());
      }
    }
  }

  /**
   * Compute the screen scale.
   * @throws Exception if any error occurs.
   */
  private void computeScale() throws Exception {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);
    float scale = metrics.scaledDensity / metrics.density;
    synchronized(deviceInfo) {
      deviceInfo.set("ibm.mobile.scale", scale);
    }
  }

  /**
   *
   */
  private class ContextLocationListener implements LocationListener {
    /**
     *
     */
    private Location lastLocation = null;

    @Override
    public synchronized void onLocationChanged(Location location) {
      // if distance to previous location > 10 meters
      if ((lastLocation == null) || (lastLocation.distanceTo(location) > 10f)) {
        lastLocation = location;
        updateLocation(location);
        Log.d(LOG_TAG, "new location: " + location);
      }
    }

    @Override
    public synchronized void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public synchronized void onProviderEnabled(String provider) {
    }

    @Override
    public synchronized void onProviderDisabled(String provider) {
    }
  }
}
