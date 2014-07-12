package cloud.project.gethelp;

import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import cloud.project.gethelp.util.SystemUiHider;

/**
 * 
 * @see SystemUiHider
 */
public class SplashScreen extends Activity {
	private static int SPLASH_TIME_OUT = 1000;
	private boolean result = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		final Class nextActivityClass = getNextActivityClass();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(SplashScreen.this, nextActivityClass);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

	private Class getNextActivityClass() {
		RegistrationDataSource registrationDataSource = new RegistrationDataSource(this);
		RegistrationBean registrationBean = new RegistrationBean();
		List<RegistrationBean> allRegistrations = registrationDataSource.getAllRegistrations(); 
		CheckRegistrationAsync checkReg = new CheckRegistrationAsync();
		if(allRegistrations != null && !allRegistrations.isEmpty()){
			checkReg.execute(allRegistrations.get(0));
			try {
				result = checkReg.get();
				Log.i("GETHELP", "the result value after get is "+result);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(result){
				Log.i("GETHELP", "the user was banned!");
				return BannedUserActivity.class;
			}else{
				Log.i("GETHELP", "going to the Mainactivity page because result value is :"+result);
				return MainActivity.class;
			}
		}else{
			Log.i("GETHELP", "Going to registration page as there is nothing in the database");
			return Registration.class;
		}
	}

	public void setResult(Boolean result){
		this.result = result;
	}
}
