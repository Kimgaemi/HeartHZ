package com.heart.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

import com.example.heart.R;
import com.heart.util.DownloadGetter;
import com.heart.util.JSONParser;

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
	private String strFileNo;   // 받을거

	// JSON
	private JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_popup);

		Intent i = getIntent();
		String strFromName = i.getStringExtra("FromName");
		String strFromPhone = i.getStringExtra("FromPhone");
		String strFileTitle = i.getStringExtra("FileTitle");
		strFileNo = i.getStringExtra("FileNo");

		Log.i("TAG!", strFromName + " / " +strFromPhone + " / " + strFileNo);

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
		if(!dialogBounds.contains((int) ev.getX(), (int)ev.getY())) {
			return false;
		}
		return super.dispatchTouchEvent(ev);
	}   


	public void popupClick(View v) {
		switch(v.getId()){
		case R.id.iv_popup_ok :
			new DownloadFileFromURL().execute(strFileUrl + strFileNo + ".wav");
			// DownloadGetter.isShow = false;
			Log.d("Tag", "popup ok");
			DownloadGetter.Download_Finish_Flag = true;

			finish();
			// Popup_Activity_Flag 는 true 로 유지!!
			break;

		case R.id.iv_popup_cancel :
			//DownloadGetter.isShow = false;
			Log.d("Tag", "popup no");
			DownloadGetter.Popup_Activity_Flag = false;
			finish();
			break;
		}
	}


	class DownloadFileFromURL extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {         
				// IF SDCARD EXISTS
				strPath = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME;
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
				is.close();
				fos.close();
				con.disconnect();
			} catch (MalformedURLException e) {
				Log.e(CURRENT_ACTIVITY + "_ERROR1", e.getMessage());
			} catch (IOException e) {
				Log.e(CURRENT_ACTIVITY + "_ERROR2", e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		protected void onProgressUpdate(String... progress) {
			// pDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String file_url) {
			Intent i = new Intent(PopupActivity.this, MessagePlayerActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(i);
		}

	}

	@Override
	public void onBackPressed() {}
}