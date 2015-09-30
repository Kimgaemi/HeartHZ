package com.heart.friend;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.heart.R;
import com.heart.util.Config;
import com.heart.util.JSONParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AcquAdapter extends ArrayAdapter<Friend>{

	// TAG
	private static final String TAG_NO_NEW_FRIEND = "no friend";
	private static final String NEW_FRIEND_ID = "relation_id";
	
	private ArrayList<Friend> items;		 
	private Context context;
	private Friend friend;
	private JSONParser jsonParser = new JSONParser();
	
	private ImageView ivPic;
	private TextView tvID;
	private TextView tvName;
	private Button btnAdd;
	
	private int iUserId;
	

	public AcquAdapter(Context context, int textViewResourceId, ArrayList<Friend> items, int id) {
		super(context, textViewResourceId, items);
		this.items = items;		
		this.context = context;
		this.iUserId = id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_friend, null);
			
			ivPic = (ImageView) v.findViewById(R.id.list_iv_pic);
			tvID = (TextView) v.findViewById(R.id.list_tv_uid);
			tvName  = (TextView)v.findViewById(R.id.list_tv_name);
			btnAdd = (Button)v.findViewById(R.id.list_btn_add);
			
		}

		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory()
				.displayer(new RoundedBitmapDisplayer(3000))
				.cacheOnDisc().resetViewBeforeLoading()
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile).build();

		
		friend = items.get(position);
		btnAdd.setTag(friend);
		
		if (friend != null) {
			btnAdd.setOnClickListener(new OnClickListener()  {

				@Override
				public void onClick(View v) {
					Friend friend = (Friend)v.getTag();
					Log.d("tag", "button click"+ friend.getId()+"!");
					new AddNewFriend().execute(friend.getId());
				}
			});
			
			if(ivPic != null) imageLoader.displayImage(friend.getPicPath(), ivPic, options);
			if(tvID != null) tvID.setText(friend.getId());
			if(tvName!= null) tvName.setText(friend.getName());
		}
		return v;
	}

	class AddNewFriend extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			if (args == null) // NO FRIEND ID
				return null;

			String iNewFirendId = args[0];

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(Config.TAG_USER_ID, Integer.toString(iUserId)));
			params.add(new BasicNameValuePair(NEW_FRIEND_ID, iNewFirendId));

			JSONObject json = jsonParser.makeHttpRequest(
					Config.URL_SET_RELATION, "POST", params);

			try {
				int success = json.getInt(Config.TAG_SUCCESS);
				if (success == 0) {
					publishProgress(TAG_NO_NEW_FRIEND);
				} /*
				 * else if (success == 1) { publishProgress(TAG_ALREADY_FRIEND);
				 * Log.d(CURRENT_ACTIVITY, "RELATION ALREADY CREATED"); }
				 */
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			if (values[0].equals(TAG_NO_NEW_FRIEND)) {
				Toast.makeText(getContext(), "COMPLETE!", Toast.LENGTH_SHORT).show();
				((Activity)context).finish();
			}
		}
	}
}

