package cloud.project.gethelp;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView lat, lng;
	Button speakButton;
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private MediaRecorder recorder = null;
	private String fileName = "",
					filePath = "",
					latitude = "",
					longitude = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lat = (TextView) findViewById(R.id.latitude);
		lng = (TextView) findViewById(R.id.longitude);
		speakButton = (Button) findViewById(R.id.recordButton);
		GPSTracker mGPS = new GPSTracker(this);
		if (mGPS.canGetLocation) {
			// mGPS.getLocation();
			latitude = String.valueOf(mGPS.getLatitude());
			longitude = String.valueOf(mGPS.getLongitude());
			lat.setText("Latitude: " + latitude);
			lng.setText("Longitude: " + longitude);

		} else {
			lat.setText("Latitude: Not found");
			lng.setText("Longitude: Not found");
			System.out.println("Unable");
		}
		speakButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startRecording();
					break;
				case MotionEvent.ACTION_UP:
					stopRecording();
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onBackPressed()
	{
	    finish();  
	}
	
	private String getFilename() {

		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}
		fileName = String.valueOf(System.currentTimeMillis()) + ".mp4";
		return (file.getAbsolutePath() + "/" + fileName);
	}

	private void startRecording() {
		Log.i("record", "Started Recording");

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		filePath = getFilename();
		recorder.setOutputFile(filePath);
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);
		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Log.i("Error", what + ", " + extra);
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Log.i("Warning", what + ", " + extra);
		}
	};

	private void stopRecording() {
		Log.i("record", "Stopped Recording");
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;

			Log.i("record", "uploading to s3");
			S3BucketManager s3BucketManager = new S3BucketManager();
			s3BucketManager.setOuterActivity(this);
			s3BucketManager.execute(filePath, latitude, longitude,new RegistrationDataSource(this).getAllRegistrations().get(0).getPhnoneNo());

		}
	}

	

	

}
