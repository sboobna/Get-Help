package com.example.givehelp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class MyThread extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... params) {
		Log.i("lat",params[0]);
		Log.i("lng",params[1]);
		Log.i("id",params[2]);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://gethelp-env.elasticbeanstalk.com/UpdateLocation?latitude="
						+ params[0] + "&longitude=" + params[1]
						+ "&id=" + params[2]);
		try {
			client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
