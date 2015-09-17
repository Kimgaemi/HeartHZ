package com.heart.activity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.heart.R;
import com.heart.util.Config;
import com.heart.util.Converter;
import com.heart.util.SlidingMenu;

public class SendActivity extends AppCompatActivity {

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private static final String TAG_CONTEXT = "contenxt";

	// VARIABLE
	private int iUserId;
	private String strTag;
	private String strTitle;

	private int[] arrFileTag;
	private String strDate;
	private String strFriendId;
	private String strFriendName;

	// VIEW
	private EditText editTitle;

	protected Converter convert;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);

		iUserId = SignInActivity.iUserId;

		Intent i = getIntent();
		strDate = i.getStringExtra(Config.TAG_DATE);
		arrFileTag = i.getIntArrayExtra(Config.TAG_CONTEXT_ARR);
		strFriendId = i.getStringExtra(Config.TAG_FRIEND_ID);
		strFriendName = i.getStringExtra(Config.TAG_FRIEND_NAME);

		editTitle = (EditText) findViewById(R.id.send_edit_title);
		strTag = Arrays.toString(arrFileTag);

		convert = new Converter(RecordActivity.frequency,
				AudioRecord.getMinBufferSize(RecordActivity.frequency,
						RecordActivity.channelConfiguration,
						RecordActivity.EncodingBitRate));

		// MENU
		menuInit();
		View menu = (View) findViewById(R.id.send_menu);
		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		logo.setImageResource(R.drawable.logo2_icon_01);
	}

	// Button Event
	public void mOnClick(View v) {
		int what = v.getId();
		switch (what) {
		case R.id.send_message_btn:
			new UploadFileToServer().execute();
			break;
		}
	}

	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... params) {
			strTitle = "Message Title";
			strTitle += editTitle.getText().toString();
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String strResponse = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Config.URL_FILE_UPLOAD);

			// SETTING HEADER
			httppost.setHeader("Accept-Charset", "UTF-8");
			httppost.setHeader("Connection", "Keep-Alive");
			httppost.setHeader("ENCTYPE", "multipart/form-data");

			try {
				MultipartEntity entity = new MultipartEntity();

				File sourceFile = new File(
						convert.getFilename(Converter.AUDIO_RECORDER_FILE));

				// ADDING FILE DATA TO HTTP BODY
				entity.addPart("sound", new FileBody(sourceFile));

				// EXTRA PARAMETERS IF YOU WANT TO PASS TO SERVER
				entity.addPart("From",
						new StringBody(Integer.toString(iUserId)));
				entity.addPart("To", new StringBody(strFriendId));
				entity.addPart("Title",
						new StringBody(strTitle, Charset.forName("UTF-8")));
				entity.addPart("TAG_Emotion",
						new StringBody(Integer.toString(arrFileTag[0])));
				entity.addPart("TAG_Weather",
						new StringBody(Integer.toString(arrFileTag[1])));
				entity.addPart("TAG_Time",
						new StringBody(Integer.toString(arrFileTag[2])));
				entity.addPart("TAG_Date", new StringBody(strDate));

				httppost.setEntity(entity);

				// MAKING SERVER CALL
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// SERVER RESPONSE
					strResponse = EntityUtils.toString(r_entity);

					JSONObject json = new JSONObject(strResponse);
					int success = json.getInt(Config.TAG_SUCCESS);

					if (success == 0) {
						String title = json.getString("File_name");
						Intent i = new Intent(SendActivity.this,
								SendActivity_pre.class);
						startActivity(i);
					}

				} else {
					strResponse = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				strResponse = e.toString();
			} catch (IOException e) {
				strResponse = e.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			convert.deleteFile("/test.wav");
			return strResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e(CURRENT_ACTIVITY, "RESPONSE FROM SERVER: " + result);

			// SHOWING THE SERVER RESPONSE IN AN ALERT DIALOG
			showAlert(result);
		}
	}

	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("RESPONSE FROM SERVERS")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static String getCurrentTime() {
		String time = "";
		Calendar cal = Calendar.getInstance();

		time = String.format("%02d%02d%02d%02d%02d", cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		return time + "@";
	}

	public void menuInit() {
		SlidingMenu sm = new SlidingMenu(this);
		toolbar = sm.getToolbar();
		dlDrawer = sm.getDlDrwaer();
		dtToggle = sm.getToggle();

		setSupportActionBar(toolbar);
		getWindow().getDecorView()
				.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

		dtToggle.setDrawerIndicatorEnabled(false);
		ActionBar ab = getSupportActionBar();
		if (null != ab)
			ab.setDisplayHomeAsUpEnabled(false);
		dlDrawer.setDrawerListener(dtToggle);

	}

	public void toolbarClick(View v) {
		switch (v.getId()) {
		case R.id.iv_toolbar_back:
			finish();
			break;

		case R.id.iv_toolbar_btn:
			dlDrawer.openDrawer(Gravity.RIGHT);
			break;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		dtToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		dtToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (dtToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}