package com.ibm.caas.sdktest.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

  public static String user;
  public static String password;
	public static String server;
	public static String port;
	public static String contextRoot;
	public static boolean ssl;
	public static String macmLib;

	public static void load(Context c) {
		SharedPreferences settings = c.getSharedPreferences("login_data", Context.MODE_PRIVATE);
		user = settings.getString("user", null);
		server = settings.getString("server", null);
		port = settings.getString("port", null);
		contextRoot = settings.getString("contextroot", null);
		ssl = settings.getBoolean("ssl", false);
		macmLib = settings.getString("macmLib", null);
	}

	public static void store(Context c) {
		SharedPreferences settings = c.getSharedPreferences("login_data", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("user", user);
		editor.putString("server", server);
		editor.putString("port", port);
		editor.putString("contextroot", contextRoot);
		editor.putBoolean("ssl", ssl);
		editor.putString("macmLib", macmLib);
		// Commit the edits!
		editor.commit();
	}
}