import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class SendNotification {

	AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(
			"AKIAJ6IKE3AV5RM6RLGA", // access key
			"rvJKCTJqKfJNeuQG6uM91RTvtlLDwnyF7nvk+nvI")); // secret key
	private final String bucket_name = "get-help";
	private final String GOOGLE_SERVER_KEY = "AIzaSyAWoSzRASaLbOsFfqY_RpcXlTdzAmTNNpc";
	private final String MESSAGE_KEY = "Emergency";
	
	public SendNotification() {

	}

	public void notify(String security_id, Double latitude, Double longitude,
			String key,String phoneNo) throws FileNotFoundException, IOException {
		//S3Object obj = s3.getObject(new GetObjectRequest(bucket_name, key));
		//File file = new File(key);
		//IOUtils.copy(obj.getObjectContent(), new FileOutputStream(file));
		String address = getAddress(latitude, longitude);
		String userMessage = latitude + " " + longitude + " " + key + " " + phoneNo +" "+ address;
		//String regId = String.valueOf(security_id);
		Sender sender = new Sender(GOOGLE_SERVER_KEY);
		Message message = new Message.Builder()
	            .addData(MESSAGE_KEY, userMessage).build();
		Result result = sender.send(message, security_id, 1);
		
		//System.out.println("Error: " + result.getCanonicalRegistrationId());
		//System.out.println("Result: " + result.getMessageId());
		System.out.println("Security id: " + security_id);
		System.out.println("Latitude: " + latitude);
		System.out.println("Longitude: " + longitude);
		System.out.println("Key: " + key);
		System.out.println("Address: " + address);

	}

	private String getAddress(Double lat, Double lng) throws IOException {
		//HttpClient client = new DefaultHttpClient();
		URL url = new URL(
				"https://maps.googleapis.com/maps/api/geocode/json?latlng="
						+ lat + "," + lng + "&sensor=true");
		String address = "";
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();		 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		address = JSONParse(response.toString());
		in.close();
		return address;
	}

	private String JSONParse(String resp) {
		JSONParser parser = new JSONParser();	
		String address = "";
		try {
			Object object = parser.parse(resp);
			JSONObject jsonObject = (JSONObject) object;
			JSONArray resultArray = (JSONArray) jsonObject.get("results");
			JSONObject resultObject = (JSONObject) resultArray.get(0);
			address = (String) resultObject.get("formatted_address");
			
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		return address;
	}

}
