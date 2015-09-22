package com.heart.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.heart.R;
import com.heart.util.Config;
import com.heart.util.DownloadService;
import com.heart.util.JSONParser;
import com.heart.util.SharedPreferenceUtil;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	private ImageView ivPw;
	private EditText editPhone;
	private EditText editPassword;
	private ProgressDialog pDialog;

	// VARIABLE
	public static int iUserId;
	public static String strName;
	public static String strPhone;
	public static String strModel;
	public static String strPw;
	public static String strPic;
	public static String strCPic;
	public static String strEmail;
	
	public static SharedPreferenceUtil pref;
	public Boolean isNotFirst = false;	// true: 재방문

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		
		editPhone = (EditText) findViewById(R.id.edit_signin_phone);
		pref = new SharedPreferenceUtil(this);
		
		isNotFirst = pref.getValue("first", false);
		
		if(isNotFirst) {
			iUserId =  pref.getValue(Config.TAG_USER_ID, 0);
			strPhone = pref.getValue(Config.TAG_PHONE, null);
			strName =  pref.getValue(Config.TAG_NAME, null);
			strPic = pref.getValue(Config.TAG_PIC_PATH, null);
			strPw =  pref.getValue(Config.TAG_PW, null);
			strModel = pref.getValue(Config.TAG_MODEL, null);
			editPhone.setText(strPhone);
			new GetUserDetails().execute();
		}
		
		btnLogin = (Button) findViewById(R.id.login_btn);
		editPassword = (EditText) findViewById(R.id.edit_signin_pw);
		tvForgotAccount = (TextView) findViewById(R.id.tv_forgot_account);
		tvCreateAccount = (TextView) findViewById(R.id.tv_create_account);
		bgContainer = (RelativeLayout) findViewById(R.id.page1_container);

		ivPw = (ImageView) findViewById(R.id.pwImageView);
		ivUser = (ImageView) findViewById(R.id.userImageView);
		ivLogo = (ImageView) findViewById(R.id.imageView1);

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
				strPw = editPassword.getText().toString();
				if(strPhone.equals("")) Toast.makeText(SignInActivity.this, "Enter your ID!", Toast.LENGTH_SHORT).show();
				else if (strPw.equals("")) Toast.makeText(SignInActivity.this, "Enter your Password!", Toast.LENGTH_SHORT).show();
				else
					new GetUserDetails().execute();				
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
				params.add(new BasicNameValuePair(Config.TAG_PW, strPw));

				// GETTING USER DETAILS BY MAKING HTTP REQUEST (POST)
				JSONObject json = jsonParser.makeHttpRequest(Config.URL_SIGN_IN, "POST", params);

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

						if(!isNotFirst) {
							iUserId = user.getInt(Config.TAG_USER_ID);
							strName = user.getString(Config.TAG_NAME);
							strPhone = user.getString(Config.TAG_PHONE);
							strPw = user.getString(Config.TAG_PW);
							strModel = user.getString(Config.TAG_MODEL);
							strPic = user.getString(Config.TAG_PIC_PATH);
							// strCPic = user.getString(Config.TAG_CPIC_PATH);
	
							pref.put("first", true);
							pref.put(Config.TAG_USER_ID, iUserId);
							pref.put(Config.TAG_PW, strPw);
							pref.put(Config.TAG_MODEL, strModel);
							pref.put(Config.TAG_NAME, strName);
							pref.put(Config.TAG_PHONE, strPhone);
							pref.put(Config.TAG_PIC_PATH, strPic);
						}
						
						// TO MAINACTIVITY HAVING USER ID AND USER PHONE
						Intent i = new Intent(SignInActivity.this, MainActivity.class);
						
						/*iUserId = userId;
						strName = userName;
						strPhone = userPhone;
						strPic = userPic;
						strCPic = userCPic;*/
						Log.d("tag", iUserId + ";");
						startActivity(i);
						finish();

					} else if (success == 1) {
						// NO USER FOUND
						publishProgress(TAG_NO_USER);
						Log.e(CURRENT_ACTIVITY, "NO USER FOUND");
						
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
						"The username or password you entered is incorrect.", Toast.LENGTH_SHORT)
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
		ivPw.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.password_icon_01)));
	}

	@Override
	public void onPause() {
		super.onPause();
		recycleBgBitmap(bgContainer);
		recycleBgBitmap(ivLogo);
		recycleBgBitmap(ivUser);
		recycleBgBitmap(ivPw);
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
