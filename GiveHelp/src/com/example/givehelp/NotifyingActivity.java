package com.example.givehelp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NotifyingActivity extends FragmentActivity {
	TextView phone;
	Button ban;
	String msg = "";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifying);
		// tv1 = (TextView) findViewById(R.id.textView1);
		// tv2 = (TextView) findViewById(R.id.textView2);
		// address = (TextView) findViewById(R.id.textView3);
		phone = (TextView) findViewById(R.id.textView1);
		ban = (Button) findViewById(R.id.button1);

		Bundle data = getIntent().getExtras();
		if (data != null) {
			msg = data.getString("data");
			Log.i("message", msg);
			String[] splitMsg = msg.split(" ", 5);

			// tv1.setText("Latitude: " + splitMsg[0]);
			// tv2.setText("Longitude: " + splitMsg[1]);
			// address.setText(splitMsg[4]);
			final String phoneNo = splitMsg[3];
			phone.setText("Phone #: " + phoneNo);

			ban.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					BanUser bu = new BanUser();
					bu.execute(phoneNo);

				}
			});

			GoogleMap googleMap;
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			final LatLng usersLoc = new LatLng(Double.parseDouble(splitMsg[0]),
					Double.parseDouble(splitMsg[1]));

			final SharedPreferences prefs = getSharedPreferences(
					MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
			String registrationId = prefs.getString("regId", "");
			GPSTracker getMyLoc = new GPSTracker(this, registrationId);
			final LatLng myLatLng = new LatLng(getMyLoc.getLatitude(),
					getMyLoc.getLongitude());

			Marker dest = googleMap.addMarker(new MarkerOptions().position(
					usersLoc).title(splitMsg[4]));
			Marker myLoc = googleMap
					.addMarker(new MarkerOptions()
							.position(myLatLng)
							.title("My Location")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			dest.showInfoWindow();
			// myLoc.showInfoWindow();
			// ArrayList<Marker> markerList = new ArrayList<Marker>();
			// markerList.add(dest);
			// markerList.add(myLoc);

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			// for(Marker m : markerList) {
			// Log.i("loc",m.getPosition().toString());
			builder.include(usersLoc);
			builder.include(myLatLng);
			// }
			// LatLngBounds bounds = builder.build();
			// int padding = 0;
			// CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
			// padding);
			// googleMap.animateCamera(cu);
			googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					builder.build(), 25, 25, 0));

			VideoView videoView = (VideoView) findViewById(R.id.playAudio);
			MediaController mc = new MediaController(getApplicationContext());
			mc.setAnchorView(videoView);
			mc.setMediaPlayer(videoView);
			Uri myUri = Uri.parse("https://s3.amazonaws.com/get-help/"
					+ splitMsg[2]);

			videoView.setMediaController(mc);
			videoView.setVideoURI(myUri);
			videoView.requestFocus();
			videoView.start();

		}

	}

	
	
	
	

	public void banUserToast() {
		Toast banUser = Toast.makeText(this, "User Banned!", Toast.LENGTH_LONG);
		banUser.show();
	}

	class BanUser extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://gethelp-env.elasticbeanstalk.com/BanUser?phoneNo="
							+ params[0]);
			try {
				client.execute(post);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			banUserToast();
		}

	}

}
