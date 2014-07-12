package com.example.givehelp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				/*
				 * for (int i = 0; i < 3; i++) { Log.i(TAG, "Working... " + (i +
				 * 1) + "/5 @ " + SystemClock.elapsedRealtime()); try {
				 * Thread.sleep(5000); } catch (InterruptedException e) { }
				 * 
				 * }
				 */
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				// Log.i("message",extras.get(Config.MESSAGE_KEY).toString());
				sendNotification(String.valueOf(extras.get(Config.MESSAGE_KEY)));
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this,NotifyingActivity.class);
		intent.putExtra("data", msg);
	
		PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//final Uri ringtone = Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString("ringtone", getString(R.string.settings_default_ringtone)));
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.help_wanted)
				//.setLargeIcon(R.drawable.help_wanted)
				.setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg)
				//.setDefaults(Notification.DEFAULT_VIBRATE)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}
}
