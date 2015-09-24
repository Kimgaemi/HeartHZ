package com.heart.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.heart.activity.SignInActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;



public class DownloadGetter extends Thread {

	public static DownloadGetter mInstance;
	private Context mContext;
	private JSONParser jsonParser = new JSONParser();

	boolean running = false;
	boolean Download_Check_Flag    = false;
	public static boolean Popup_Activity_Flag    = false;
	public static boolean Download_Finish_Flag = false;

	private String strFromName;
	private String strFromPhone;
	private String strFromPic;
	private String strFileTitle;
	private String strFileNo;
	private String strFlag;

	public DownloadGetter(Context context) {
		this.mContext = context;
		mInstance = this;
		running = true;
	}

	public void setRunning() {
		running = false;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(5000);
				Log.d("Tag", "돌고잇니 " + Popup_Activity_Flag +" / " + Download_Check_Flag);
				if( !Popup_Activity_Flag && !Download_Check_Flag ) {
					Download_Check_Flag = true;
					new CheckFileDownload().execute();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String URL_CHECK_FILE_DOWNLOAD = "http://210.125.96.96/heart_php/check_flag_from_android.php";   

	class CheckFileDownload extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {

			Log.d("Tag", "checkfile inbefore" + Download_Check_Flag);
			if( Download_Check_Flag ){
				Log.d("Tag", "checkfile in");
				// Download List Check

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(Config.TAG_USER_ID, Integer.toString(SignInActivity.iUserId)));
				JSONObject json = jsonParser.makeHttpRequest(URL_CHECK_FILE_DOWNLOAD, "GET", params);

				try {
					if (json != null) {
						Log.d("tag", json.toString());
						int success = json.getInt("success");
						if (success == 0) {
							JSONArray userObj = json.getJSONArray("flag_file");

							if(userObj.length() > 0) {
								JSONObject user = userObj.getJSONObject(0);

								Log.d("tag", user.toString());
								for (int i = 0; i < userObj.length(); i++) {
									JSONObject c = userObj.getJSONObject(i);

									strFromName = user.getString("from_name");
									strFromPhone = user.getString("from_phone");
									strFromPic = user.getString("from_pic");
									strFileNo = user.getString("file_no");
									strFileTitle = user.getString("file_title");
									strFlag = user.getString("tag_flag");
								}            
								//////////////////////////////
								// Exist File
								Popup_Activity_Flag = true;
								Download_Check_Flag = false;
								// Popup_Activity Call!!!!
								Intent popupIntent = new Intent(mContext, com.heart.activity.PopupActivity.class);
								popupIntent.putExtra("FromName", strFromName);
								popupIntent.putExtra("FromPhone", strFromPhone);
								popupIntent.putExtra("FromPic", strFromPic);
								popupIntent.putExtra("FileTitle", strFileTitle);
								popupIntent.putExtra("FileNo", strFileNo);

								// PendingIntent pie= PendingIntent.getActivity(mContext, 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
								popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
								mContext.startActivity(popupIntent);
								Log.d("Tag", "check file ok");

								new ChangeFileFlag().execute();
								//////////////////////////////
							}
							else {
								//////////////////////////////
								// No File
								Log.d("Tag", "check file no");
								Download_Check_Flag = false;
								//////////////////////////////
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} // if( Download_Check_Flag )

			return null;
		}
	}

	class ChangeFileFlag extends AsyncTask<String, String, String> {
		protected String doInBackground(String... args) {

			while(true) {
				if( Download_Finish_Flag ){
					Log.d("Tag", "change flag in");

					// Flag Change
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("file_no", strFileNo));
					params.add(new BasicNameValuePair("tag_flag",Integer.toString(2)));

					JSONObject json = jsonParser.makeHttpRequest(Config.URL_CHANGE_FLAG, "GET", params);

					if (json != null) {
						try {
							Log.d("tag", json.toString());
							int success = json.getInt(Config.TAG_SUCCESS);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					Download_Finish_Flag = false;
					Popup_Activity_Flag = false;
					Log.d("Tag", "돌는중");
					break;
				}
			}
			return null;
		}   
	}
}