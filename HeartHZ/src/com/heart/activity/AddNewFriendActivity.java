package com.heart.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heart.R;
import com.heart.friend.AcquAdapter;
import com.heart.friend.Friend;
import com.heart.util.Config;
import com.heart.util.JSONParser;

public class AddNewFriendActivity extends AppCompatActivity {

	// TAG
	private static final String CURRENT_ACTIVITY = "MainActivity";
	private static final String TAG_NO_NEW_FRIEND = "no friend";
	private static final String TAG_ALREADY_FRIEND = "already friend";
	private static final String NEW_FRIEND_ID = "relation_id";
	private static final String TAG_REGISTER = "register";
	private static final String ADD_FRIEND = "add";

	// JSON
	private JSONParser jsonParser = new JSONParser();

	// VARIABLE
	private String 	strName;
	private String 	strPic;
	private String 	strPhone;
	private int 	iUserId;

	private AcquAdapter adapter;
	private ArrayList<Friend> item = new ArrayList<Friend>();

	public ListView listView;
	Uri mBaseUri; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup_listview);

		listView = (ListView) findViewById(R.id.acqu_list);
		new LoadAllAcqu().execute();

	}

	class LoadAllAcqu extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(Config.TAG_USER_ID, Integer.toString(iUserId)));

			JSONObject json = jsonParser.makeHttpRequest(Config.URL_GET_USER_LIST, "POST", params);

			if(json != null) {
				Log.d(CURRENT_ACTIVITY+"_all_friends", json.toString());

				try {
					int success = json.getInt(Config.TAG_SUCCESS);

					if (success == 0) {

						JSONArray userObj = json.getJSONArray(Config.TAG_FRIENDS); 	
						Log.d(CURRENT_ACTIVITY+"_num_of_Frineds", userObj.length()+"");

						for (int i = 0; i < userObj.length(); i++) {
							JSONObject c = userObj.getJSONObject(i);

							String strFriendPhone = c.getString(Config.TAG_PHONE);
							if(getContactName(strFriendPhone) != null) {	// 전화번호 부 일치하는지
								String strFriendId	 	= c.getString(Config.TAG_USER_ID);
								String strFriendName	= c.getString(Config.TAG_NAME);
								String strFriendPicPath = c.getString(Config.TAG_PIC_PATH);

								Log.d("temp", strFriendName + ": "+ strFriendPicPath);
								Friend friend = new Friend(strFriendId, strFriendName, strFriendPhone, strFriendPicPath, null, null);
								item.add(friend);
							}
						}
					} else {
						// no friends found
						// Launch Add New friends Activity
						// MainActivity.Intent i = new Intent(getApplicationContext(), NewfriendActivity.class);

						// Closing all previous activities
						// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// startActivity(i);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		protected void onPostExecute(String file_url) {

			//	runOnUiThread(new Runnable() {
			//	public void run() {

			adapter = new AcquAdapter(getBaseContext(), R.layout.list_friend, item);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(onClickListner);
			//		}
			//	});
		}
	}

	private OnItemClickListener onClickListner = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent, View view, int pos, long id) {
			Button btnAdd = (Button) view.findViewById(R.id.list_btn_add);
			btnAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					TextView tvId = (TextView) v.findViewById(R.id.list_tv_uid);
					new AddNewFriend().execute(tvId.getText().toString());
				}
			});

		};
	};

	class AddNewFriend extends AsyncTask<String, String, String> {
		protected String doInBackground(String... args) {
			if (args == null) // NO FRIEND ID
				return null;

			String iNewFirendId = args[0];
			// BEFORE ADD THE RELATION
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(Config.TAG_USER_ID, Integer.toString(iUserId)));
			params.add(new BasicNameValuePair(NEW_FRIEND_ID, iNewFirendId));

			JSONObject json = jsonParser.makeHttpRequest(Config.URL_SET_RELATION, "POST", params);
			Log.d(CURRENT_ACTIVITY + "_relations", json.toString());

			try {
				int success = json.getInt(Config.TAG_SUCCESS);
				if (success == 0) {
					publishProgress(TAG_NO_NEW_FRIEND);
					Log.d(CURRENT_ACTIVITY,
							"RELATION SUCCESSFULLY CREATED");
				} /*else if (success == 1) {
					publishProgress(TAG_ALREADY_FRIEND);
					Log.d(CURRENT_ACTIVITY,
							"RELATION ALREADY CREATED");
				}*/
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			if (values[0].equals(TAG_NO_NEW_FRIEND)) {
				Toast.makeText(AddNewFriendActivity.this, "COMPLETE!",
						Toast.LENGTH_SHORT).show();

				Intent intent = getIntent();
				finish();
				startActivity(intent);
			} 
		}
	}


	public String getContactName(final String phoneNumber) {  
		Uri uri;
		String[] projection;
		projection = new String[] { android.provider.Contacts.People.NAME }; 
		mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
		
		try {
			Class<?> c =Class.forName("android.provider.ContactsContract$PhoneLookup");
			mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
			projection = new String[] { "display_name" };
		} catch (Exception e) {
			e.printStackTrace();
		}


		uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber)); 
		Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null); 

		String contactName = "";

		if (cursor.moveToFirst()) { 
			contactName = cursor.getString(0);
		} 

		cursor.close();
		cursor = null;

		return contactName; 
	}
}
