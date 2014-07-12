package cloud.project.gethelp;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class Registration extends Activity {


	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserRegistrationTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mFirstName;
	private String mLastName;
	private String mPhoneNo;

	// UI references.
	private EditText mFirstNameView;
	private EditText mLastNameView;
	private EditText mPhoneNoView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_registration);

		// Set up the login form.
		mFirstNameView = (EditText) findViewById(R.id.firstName);
		mFirstNameView.setText(mFirstName);
		mLastNameView = (EditText)findViewById(R.id.lastName);
		mPhoneNoView = (EditText) findViewById(R.id.phoneNo);
		mPhoneNoView.setText(getPhoneNo());
		mPhoneNoView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	/**
	 * Returns the users telephone number or blank string
	 * @return
	 */
	private String getPhoneNo() {
		TelephonyManager tMgr = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		return ((mPhoneNumber==null)? "": mPhoneNumber);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mFirstNameView.setError(null);
		mPhoneNoView.setError(null);

		// Store values at the time of the login attempt.
		mFirstName = mFirstNameView.getText().toString();
		mLastName = mLastNameView.getText().toString();
		mPhoneNo = mPhoneNoView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		
		if (TextUtils.isEmpty(mFirstName)) {
			mLastNameView.setError(getString(R.string.error_field_required));
			focusView = mLastNameView;
			cancel = true;
		}
		if (TextUtils.isEmpty(mLastName)) {
			mLastNameView.setError(getString(R.string.error_field_required));
			focusView = mLastNameView;
			cancel = true;
		}
		if (TextUtils.isEmpty(mPhoneNo)) {
			mPhoneNoView.setError(getString(R.string.error_field_required));
			focusView = mPhoneNoView;
			cancel = true;
		}

		

		if (cancel) {
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.registering_progress);
			showProgress(true);
			mAuthTask = new UserRegistrationTask();
			mAuthTask.execute(mFirstName,mLastName,mPhoneNo);
			try {
				mAuthTask.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegistrationTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
			try {
				enterDetailsOnLocalDb(params);
				return enterDetailsOnHost(params);
				// Simulate network access.
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}

		private boolean enterDetailsOnHost(String[] params) throws ClientProtocolException, IOException {
			RegistrationBean registrationBean = constructRegistrationBean(params);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://gethelp-env.elasticbeanstalk.com/AddNewUser?phoneNo="
							+ registrationBean.getPhnoneNo() + "&firstName="+registrationBean.getFirstName() + "&lastName="+registrationBean.getLastName() );
			HttpResponse response = client.execute(post);
			if ("true".equals(response.getHeaders("isAdded")[0].getValue())) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * This method will add the details in the local sql lite db
		 * @param params
		 */
		private void enterDetailsOnLocalDb(String[] params) {
			RegistrationBean registrationBean = constructRegistrationBean(params);
			new RegistrationDataSource(Registration.this).addRegistrationDetails(registrationBean);
		}

		/**
		 * constructs bean from params
		 * @param params
		 * @return
		 */
		private RegistrationBean constructRegistrationBean(String[] params) {
			RegistrationBean registrationBean =  new RegistrationBean();
			registrationBean.setFirstName(params[0]);
			registrationBean.setLastName(params[1]);
			registrationBean.setPhnoneNo(params[2]);
			return registrationBean;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPhoneNoView
						.setError(getString(R.string.error_incorrect_password));
				mPhoneNoView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
