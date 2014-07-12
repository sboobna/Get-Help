package cloud.project.gethelp;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class CheckRegistrationAsync extends
		AsyncTask<RegistrationBean, Void, Boolean> {
	private Boolean returnValue;
	@Override
	protected Boolean doInBackground(RegistrationBean... registrationBean) {
		returnValue = false;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://gethelp-env.elasticbeanstalk.com/checkIfBanned?phoneNo="
						+ registrationBean[0].getPhnoneNo());
		try {
			HttpResponse response = client.execute(post);
			if ("true".equals(response.getHeaders("isBanned")[0].getValue())) {
				returnValue = true;
			} else {
				returnValue = false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return returnValue;
	}
	
	
	
}
