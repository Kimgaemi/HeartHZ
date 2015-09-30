package com.heart.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.heart.R;
import com.heart.service.RealService;
import com.heart.util.JSONParser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class PopupActivity extends Activity {

	private TextView fileName = null;
	private TextView senderName = null;
	private TextView ment1 = null;
	private TextView ment2 = null;

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private final String FOLDER_NAME = "/HeartHZ";

	// VARIABLE
	private String strPath;
	private String strFileUrl = "http://210.125.96.96/Heart_php/uploads/";
	private String strFileNo; // 받을거
	private boolean isDownloadFinish = false;

	// JSON
	private JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_popup);

		RealService.Duplicated_Popup_Flag = true; // Popup창

		Intent i = getIntent();
		String strFromName = i.getStringExtra("FromName");
		String strFromPhone = i.getStringExtra("FromPhone");
		String strFileTitle = i.getStringExtra("FileTitle");
		strFileNo = i.getStringExtra("FileNo");

		Log.i("TAG!", strFromName + " / " + strFromPhone + " / " + strFileNo);

		fileName = (TextView) findViewById(R.id.tv_popup_file_name);
		senderName = (TextView) findViewById(R.id.tv_popup_sender_name);
		ment1 = (TextView) findViewById(R.id.tv_popup_ment1);
		ment2 = (TextView) findViewById(R.id.tv_popup_ment2);

		senderName.setText(strFromName);
		fileName.setText(strFileTitle);

		// FONTS
		fileName.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		senderName.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		ment1.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		ment2.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));

	}

	// 배경터치 막기
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);
		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
			return false;
		}
		return super.dispatchTouchEvent(ev);
	}

	public void popupClick(View v) {

		RealService.Check_Change_Flag = true;
		RealService.Duplicated_Popup_Flag = false;
		// RealService.CheckFileDownload.class.notifyAll();

		Log.d("Tag", "깨우자!");

		switch (v.getId()) {
		case R.id.iv_popup_ok:
			new DownloadFileFromURL().execute(strFileUrl + strFileNo + ".wav");
			Log.d("Tag", "popup ok");
			finish();
			break;

		case R.id.iv_popup_cancel:
			Log.d("Tag", "popup no");
			RealService.Check_Downloading_Flag = false;
			new ChangeFileFlag().execute();
			finish();
			break;
		}
	}

	class DownloadFileFromURL extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// IF SDCARD EXISTS
				strPath = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + FOLDER_NAME;
				File file = new File(strPath);

				if (!file.exists()) {
					// IF DIRECTORY DOESN'T EXIST
					Log.d(CURRENT_ACTIVITY + "_PATH", "만들어");
					file.mkdirs();
				}

				Log.d(CURRENT_ACTIVITY + "_PATH", strPath);
			} else {
				// WHAT SHOULD I DO
			}

		}

		@Override
		protected String doInBackground(String... f_url) {
			int count = 0;
			try {
				URL url = new URL(f_url[0]);
				Log.d(CURRENT_ACTIVITY + "_URL", url.toString());

				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setRequestMethod("POST");
				con.connect();

				int len = con.getContentLength();
				Log.d(CURRENT_ACTIVITY + "_RESPONSE CODE",
						con.getResponseCode() + "!");
				byte[] tmpByte = new byte[len];

				File file = new File(strPath, strFileNo + ".wav");
				if (!file.exists()) {
					file.createNewFile();
				}
				Log.d(CURRENT_ACTIVITY + "_PATH", file.getAbsolutePath());

				InputStream is = con.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);

				for (;;) {
					count = is.read(tmpByte);
					if (count <= 0) {
						break;
					}
					fos.write(tmpByte, 0, count);
				}
				isDownloadFinish = true;
				is.close();
				fos.close();
				con.disconnect();
			} catch (MalformedURLException e) {
				isDownloadFinish = false;
				Log.e(CURRENT_ACTIVITY + "_ERROR1", e.getMessage());
			} catch (IOException e) {
				isDownloadFinish = false;
				Log.e(CURRENT_ACTIVITY + "_ERROR2", e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			Log.d("Service", "MessageList move");
			if (isDownloadFinish) {
				Intent i = new Intent(PopupActivity.this,
						MessagePlayerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);

				// if잘받아졌다면
				new ChangeFileFlag().execute();
			} else
				Toast.makeText(getApplicationContext(), "Fail to download",
						Toast.LENGTH_SHORT).show();
			RealService.Check_Downloading_Flag = false;
		}

	}

	public static String URL_CHANGE_FLAG = "http://210.125.96.96/heart_php/change_flag.php";

	class ChangeFileFlag extends AsyncTask<String, String, String> {
		protected String doInBackground(String... args) {

			while (true) {
				if (RealService.Check_Change_Flag) {
					Log.d("Tag", "change flag in");

					// Flag Change
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("file_no", strFileNo));
					params.add(new BasicNameValuePair("tag_flag", Integer
							.toString(2)));

					JSONObject json = jsonParser.makeHttpRequest(
							URL_CHANGE_FLAG, "GET", params);

					if (json != null) {
						try {
							Log.d("tag", json.toString());
							int success = json.getInt("success");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					RealService.Check_Change_Flag = false;
					Log.d("Tag", "돌는중");
					break;
				}
			}
			return null;
		}
	}

	@Override
	public void onBackPressed() {
	}
}