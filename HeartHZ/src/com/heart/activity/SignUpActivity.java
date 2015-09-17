package com.heart.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.heart.R;
import com.heart.util.Config;
import com.heart.util.JSONParser;

public class SignUpActivity extends Activity {

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private final String TAG_DUPLICATE_USER = "duplicated";

	// JSON
	private JSONParser jsonParser = new JSONParser();

	// VIEW
	private Button btnSubmit = null;
	private Button btnCancel = null;
	private EditText editName = null;
	private EditText editPhone = null;
	private ProgressDialog pDialog = null;

	// VARIABLE
	private String strName = null;
	private String strPhone = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		btnSubmit = (Button) findViewById(R.id.btn_signup_submit);
		btnCancel = (Button) findViewById(R.id.btn_signup_cancel);
		editName = (EditText) findViewById(R.id.edit_signup_name);
		editPhone = (EditText) findViewById(R.id.edit_signup_phone);

		btnSubmit.setOnClickListener(signUpListen);
		btnCancel.setOnClickListener(signUpListen);
	}

	private OnClickListener signUpListen = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.btn_signup_submit:
				// CALL CREATENEWUSER() CLASS
				strName = editName.getText().toString();
				strPhone = editPhone.getText().toString();

				if (!(strName.equals("")) && !(strPhone.equals("")))
					new CreateNewUser().execute();
				else
					Toast.makeText(SignUpActivity.this,
							"You have to write down all!", Toast.LENGTH_SHORT)
							.show();
				break;

			case R.id.btn_signup_cancel:
				// CANCEL EVERYTHING AND GO TO PREVIOUS ACTIVITY
				editName.setText("");
				editPhone.setText("");

				Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
				startActivity(i);
				finish();
				break;
			}
		}
	};

	class CreateNewUser extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUpActivity.this);
			pDialog.setMessage("CREATING USER..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(Config.TAG_NAME, strName));
			params.add(new BasicNameValuePair(Config.TAG_PHONE, strPhone));
			Log.d(CURRENT_ACTIVITY + "doInBackground", "name: " + strName
					+ " phone: " + strPhone);

			JSONObject json = jsonParser.makeHttpRequest(Config.URL_SIGN_UP,
					"POST", params);

			if (json != null) {
				Log.d(CURRENT_ACTIVITY + "Create Response", json.toString());

				try {
					// CHECK FOR SUCCESS TAG
					int success = json.getInt(Config.TAG_SUCCESS);

					if (success == 0) {
						// SUCCESSFULLY CREATED USER
						// TO SIGNIN ACTIVITY
						Intent i = new Intent(SignUpActivity.this,
								SignInActivity.class);
						startActivity(i);

						// CLOSING THIS SCREEN
						finish();
					} else if (success == 1) {
						// FAILED TO CREATE USER
						publishProgress(TAG_DUPLICATE_USER);
						Log.e(CURRENT_ACTIVITY, "FAILE TO INSERT USER");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			if (values[0].equals(TAG_DUPLICATE_USER)) {
				editPhone.setText("");
				editName.setText("");
				Toast.makeText(SignUpActivity.this,
						"THIS PHONE IS ALREADY JOINED.", Toast.LENGTH_SHORT)
						.show();
			}
		}

		protected void onPostExecute(String file_url) {
			// DISMISS THE DIALOG ONCE DONE
			pDialog.dismiss();
		}
	}
}
