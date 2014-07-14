package cloud.project.gethelp;

import java.io.File;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class S3BucketManager extends AsyncTask<String, Void, Void> {

	AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
			<ACCESS-KEY>, // access key
			<SECRET-KEY>)); // secret key
	String bucket_name = "get-help";
	Activity activity;
	public void setOuterActivity(Activity activity){
		this.activity = activity;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		Log.i("record", "starting background process");
		String filePath = params[0];
		File file = new File(filePath);
		String key = filePath.substring(filePath.lastIndexOf("/") + 1);
		try {
			s3.putObject(new PutObjectRequest(bucket_name, key, file)
					.withCannedAcl(CannedAccessControlList.PublicRead));
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://gethelp-env.elasticbeanstalk.com/sendRequest?latitude="
							+ params[1] + "&longitude=" + params[2]
							+ "&key=" + key + "&phoneNo="+params[3]);
			client.execute(post);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.i("record", "Put on s3");
		Toast success = Toast.makeText(activity,
				"Message sent! Someone will come to help you soon",
				Toast.LENGTH_LONG);
		success.show();
	}

}
