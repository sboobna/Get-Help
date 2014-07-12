package com.example.givehelp;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {
	//TextView loc;
	//String id = "1";
	private String latitude = "", longitude = "";
	GoogleCloudMessaging gcm;
	Context context;
	String regId;
	
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	static final String TAG = "Register Activity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//loc = (TextView) findViewById(R.id.loc);
		Log.i("update","app started");
		context = getApplicationContext();
		if(TextUtils.isEmpty(regId)) {
	          regId = registerGCM();
	          Log.d("RegisterActivity", "GCM RegId: " + regId);
	    }
		
	}
	
	public void sendLocation() {
		GPSTracker mGPS = new GPSTracker(this, regId);
  		if(mGPS.canGetLocation) {
  			latitude = String.valueOf(mGPS.getLatitude());
  			longitude = String.valueOf(mGPS.getLongitude());
  			Log.i("latitude",latitude);
  			Log.i("longitude",longitude);
  			MyThread mythread = new MyThread();
  			mythread.execute(latitude, longitude, regId);
  		}
	}
	
	public String registerGCM() {
		 
	    gcm = GoogleCloudMessaging.getInstance(this);
	    regId = getRegistrationId(context);
	    if (TextUtils.isEmpty(regId)) {
	    	 
	        registerInBackground();
	    }
	    else {
	    	sendLocation();
	    	showToast();
	    	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    onBackPressed();
	    }
	    
	    return regId;
	}
	
	public void showToast() {
		Toast registered = Toast.makeText(this,
				"Application registered. Please keep it running to send your location updates!",
				Toast.LENGTH_LONG);
	    registered.show();
	    
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getSharedPreferences(
	        MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	    String registrationId = prefs.getString(REG_ID, "");
	    if (registrationId.isEmpty()) {
	      Log.i(TAG, "Registration not found.");
	      return "";
	    }
	    int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	      Log.i(TAG, "App version changed.");
	      return "";
	    }
	    return registrationId;
	  }
	
	private static int getAppVersion(Context context) {
	    try {
	      PackageInfo packageInfo = context.getPackageManager()
	          .getPackageInfo(context.getPackageName(), 0);
	      return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	      Log.d("RegisterActivity",
	          "I never expected this! Going down, going down!" + e);
	      throw new RuntimeException(e);
	    }
	  }
	
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	      @Override
	      protected String doInBackground(Void... params) {
	        String msg = "";
	        try {
	          if (gcm == null) {
	            gcm = GoogleCloudMessaging.getInstance(context);
	          }
	          regId = gcm.register(Config.GOOGLE_PROJECT_ID);
	          Log.d("RegisterActivity", "registerInBackground - regId: "
	              + regId);
	          msg = "Device registered, registration ID=" + regId;
	 
	          storeRegistrationId(context, regId);
	        } catch (IOException ex) {
	          msg = "Error :" + ex.getMessage();
	          Log.d("RegisterActivity", "Error: " + msg);
	        }
	        Log.d("RegisterActivity", "AsyncTask completed: " + msg);
	        return msg;
	      }
	 
	      @Override
	      protected void onPostExecute(String msg) {
	    	  sendLocation();
	    	  showToast();
	    	  try {
	  			Thread.sleep(2000);
	  		} catch (InterruptedException e) {
	  			e.printStackTrace();
	  		}
	  	    onBackPressed();
	      }
	    }.execute(null, null, null);
	  }
	
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getSharedPreferences(
	        MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(REG_ID, regId);
	    editor.putInt(APP_VERSION, appVersion);
	    editor.commit();
	  }
}

