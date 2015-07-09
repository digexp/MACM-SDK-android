package com.ibm.caas.sdktest.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

  public static String user;
  public static String password;
	public static String server;
	public static String port;
	public static boolean ssl;
	public static String macmLib;
	public static String macmContext;
	public static String macmTenant;

	public static void load(Context c) {
		SharedPreferences settings = c.getSharedPreferences("login_data", Context.MODE_PRIVATE);
		user = settings.getString("user", null);
		server = settings.getString("server", null);
		port = settings.getString("port", null);
		ssl = settings.getBoolean("ssl", false);
		macmLib = settings.getString("macmLib", null);
		macmContext = settings.getString("macmContext", null);
		macmTenant = settings.getString("macmTenant", null);
	}

	public static void store(Context c) {
		SharedPreferences settings = c.getSharedPreferences("login_data", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("user", user);
		editor.putString("server", server);
		editor.putString("port", port);
		editor.putBoolean("ssl", ssl);
		editor.putString("macmLib", macmLib);
		editor.putString("macmContext", macmContext);
		editor.putString("macmTenant", macmTenant);
		// Commit the edits!
		editor.commit();
	}
}