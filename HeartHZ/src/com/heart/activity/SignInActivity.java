package com.heart.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heart.R;
import com.heart.util.Config;
import com.heart.util.DownloadService;
import com.heart.util.JSONParser;

public class SignInActivity extends Activity {

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private final String TAG_NO_USER = "No user";
	private final String TAG_NO_PARAMETER = "No parameter";

	// JSONPARSOR
	private JSONParser jsonParser = new JSONParser();

	// VIEW
	private Button btnLogin;
	private TextView tvCreateAccount;
	private TextView tvForgotAccount;
	private RelativeLayout bgContainer;
	private ImageView ivLogo;
	private ImageView ivUser;
	private ImageView iv_pw;
	private EditText editPhone;
	private EditText editPassword;
	private ProgressDialog pDialog;

	// VARIABLE
	public static int iUserId;
	public static String strName;
	public static String strPhone;
	public static String strPic;
	public static String strCPic;
	public static String strEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		btnLogin = (Button) findViewById(R.id.login_btn);
		editPhone = (EditText) findViewById(R.id.edit_signin_phone);
		editPassword = (EditText) findViewById(R.id.edit_signin_pw);

		tvCreateAccount = (TextView) findViewById(R.id.tv_create_account);
		tvForgotAccount = (TextView) findViewById(R.id.tv_forgot_account);

		bgContainer = (RelativeLayout) findViewById(R.id.page1_container);

		ivLogo = (ImageView) findViewById(R.id.imageView1);
		ivUser = (ImageView) findViewById(R.id.userImageView);
		iv_pw = (ImageView) findViewById(R.id.pwImageView);

		btnLogin.setOnClickListener(signInListen);
		tvCreateAccount.setOnClickListener(signInListen);

		// FONT
		btnLogin.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));
		editPhone.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		editPassword.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		tvCreateAccount.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		tvForgotAccount.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));

	}

	private OnClickListener signInListen = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.login_btn:
				// CALL GetUserDetails() CLASS
				strPhone = editPhone.getText().toString();
				if (!(strPhone.equals("")))
					new GetUserDetails().execute();
				else
					Toast.makeText(SignInActivity.this,
							"WRITE DOWN YOUR PHONE NUMBER", Toast.LENGTH_SHORT)
							.show();
				break;

			case R.id.tv_create_account:
				// TO SIGNUP ACTIVITY
				Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
				startActivity(i);
				finish();
				break;
			}
		}
	};

	class GetUserDetails extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignInActivity.this);
			pDialog.setMessage("LOADING.... PLEASE WAIT...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			try {
				// BUILDING PARAMETERS
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(Config.TAG_PHONE, strPhone));

				// GETTING USER DETAILS BY MAKING HTTP REQUEST (GET)
				JSONObject json = jsonParser.makeHttpRequest(
						Config.URL_SIGN_IN, "GET", params);

				if (json != null) {
					// CHECK YOUR LOG FOR JSON RESPONSE
					Log.d(CURRENT_ACTIVITY + "_JSON", json.toString());

					int success = json.getInt(Config.TAG_SUCCESS);
					if (success == 0) {
						// SUCCESSFULLY RECEIVED USER DETAILS
						JSONArray userObj = json.getJSONArray(Config.TAG_USER);

						// GET FIRST USER OBJECT FROM JSON ARRAY
						JSONObject user = userObj.getJSONObject(0);
						Log.d(CURRENT_ACTIVITY + "_USER", user.toString());

						int userId = user.getInt(Config.TAG_USER_ID);
						String userName = user.getString(Config.TAG_NAME);
						String userPhone = user.getString(Config.TAG_PHONE);
						String userPic = user.getString(Config.TAG_PIC_PATH);
						String userCPic = user.getString(Config.TAG_CPIC_PATH);
						String userEmail = user
								.getString(Config.TAG_USER_EMAIL);

						// TO MAINACTIVITY HAVING USER ID AND USER PHONE
						Intent i = new Intent(SignInActivity.this,
								MainActivity.class);
						iUserId = userId;
						strName = userName;
						strPhone = userPhone;
						strPic = userPic;
						strCPic = userCPic;
						strEmail = userEmail;
						Log.d("tag", iUserId + ";");
						startActivity(i);
						finish();

					} else if (success == 1) {
						// NO USER FOUND
						publishProgress(TAG_NO_USER);
						Log.e(CURRENT_ACTIVITY, "NO USER FOUND");
					} else {
						// NO PARAMETER
						publishProgress(TAG_NO_PARAMETER);
						Log.e(CURRENT_ACTIVITY, "NO PARAMETER");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			if (values[0].equals(TAG_NO_USER)) {
				editPhone.setText("");
				Toast.makeText(SignInActivity.this,
						"NO USER FOUND. SIGN UP, PLEASE.", Toast.LENGTH_SHORT)
						.show();
			}
		}

		protected void onPostExecute(String file_url) {
			// DISMISS THE DIALOG ONCE GOT ALL DETAILS
			pDialog.dismiss();
			if (!DownloadService.IsPeriSer) {
				Log.d("Tag", "서비스 업당!");
				startService(new Intent(getApplicationContext(),
						DownloadService.class));
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		bgContainer.setBackground(new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.login_bg_01)));
		ivLogo.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.loginlogo_icon_01)));
		ivUser.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.id_icon_01)));
		iv_pw.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.password_icon_01)));
	}

	@Override
	public void onPause() {
		super.onPause();
		recycleBgBitmap(bgContainer);
		recycleBgBitmap(ivLogo);
		recycleBgBitmap(ivUser);
		recycleBgBitmap(iv_pw);
	}

	public static void recycleBgBitmap(View v) {
		Drawable d = v.getBackground();
		if (d instanceof BitmapDrawable) {
			Bitmap b = ((BitmapDrawable) d).getBitmap();
			b.recycle();
		}
		d.setCallback(null);
	}

	public static void recycleIvBitmap(ImageView iv) {
		Drawable d = iv.getDrawable();
		if (d instanceof BitmapDrawable) {
			Bitmap b = ((BitmapDrawable) d).getBitmap();
			b.recycle();
		}
		d.setCallback(null);
		iv.setImageDrawable(null);
		iv.setImageResource(0);

	}

}
