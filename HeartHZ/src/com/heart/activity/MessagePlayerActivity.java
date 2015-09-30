package com.heart.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.heart.R;
import com.heart.service.ServicePage;
import com.heart.util.AnimatedExpandableListView;
import com.heart.util.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.heart.util.Config;
import com.heart.util.JSONParser;
import com.heart.util.RecycleUtils;
import com.heart.util.SharedPreferenceUtil;
import com.heart.util.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class MessagePlayerActivity extends AppCompatActivity {

	// function
	private AnimatedExpandableListView listView;
	private MessageListAdapter adapter;
	protected List<GroupItem> items = new ArrayList<GroupItem>();
	protected static int mode = 0;

	// View
	private Window window = null;
	private TextView tvMessageList;
	private Button btnDelete;

	private MediaPlayer mp = new MediaPlayer();
	private JSONParser jsonParser = new JSONParser();
	private SeekBar seekbar;

	Thread thread = new Thread();
	ChildItem child;

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	String folderPath = Environment.getExternalStorageDirectory().getPath()
			+ "/HeartHZ/";
	String playPath;
	// VARIABLE
	private int numbering = 1;
	private double timeElapsed = 0;
	private double totalTime = 0;
	private String path;
	private boolean isPlaying = false;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;
	private BitmapDrawable logoBitmap;
	private BitmapDrawable toolbarBtnBitmap;
	private BitmapDrawable toolbarBackBitmap;

	private String[] fileNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messagelist);

		new LoadAllMessage().execute();
		// GET LIST FROM FOLDER
		getFileList();

		tvMessageList = (TextView) findViewById(R.id.tv_messagelist);
		btnDelete = (Button) findViewById(R.id.messagelist_deletebtn);

		// MENU
		menuInit();

		// STATUSBAR COLOR
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window = this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_base));
		}

		// FONT
		tvMessageList.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		btnDelete.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));

		// GROUP LISTENER
		listView = (AnimatedExpandableListView) findViewById(R.id.expandlistview);
		listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Log.i("WHAT POSITION", String.valueOf(groupPosition));
				playPath = fileNames[groupPosition]; // /////////////////////////////
				if (mp.isPlaying())
					mp.pause();
				// thread.interrupt();

				if (seekbar != null)
					seekbar.setProgress(0);
				mp.release();
				mp = new MediaPlayer();
				mp.seekTo(0);
				timeElapsed = 0;

				if (listView.isGroupExpanded(groupPosition)) {
					listView.collapseGroupWithAnimation(groupPosition);
					TextView num = (TextView) v
							.findViewById(R.id.tv_messagelist_num);
					num.setTextColor(getResources().getColor(
							R.color.messagelist_grpitem_num_color));
				} else {
					listView.expandGroupWithAnimation(groupPosition);
					TextView num = (TextView) v
							.findViewById(R.id.tv_messagelist_num);
					num.setTextColor(getResources().getColor(
							R.color.messagelist_grpitem_num_focus_color));
					ListView conlistView = (ListView) parent;
					for (int i = 0; i < items.size(); i++) {
						if (i != groupPosition) {
							if (listView.isGroupExpanded(i)) {
								listView.collapseGroupWithAnimation(i);
								View notFocusChild = conlistView.getChildAt(i);
								TextView nfocus_num = (TextView) notFocusChild
										.findViewById(R.id.tv_messagelist_num);
								nfocus_num
										.setTextColor(getResources()
												.getColor(
														R.color.messagelist_grpitem_num_color));
							}
						}
					}
				}
				return true;
			}
		});

		// CHILD LISTENER
		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				Log.d("Tag", child.strFileNo + "/");

				final int whatPosition = groupPosition;
				Log.d("FILE", whatPosition + "/");
				ImageView btnPlay = (ImageView) findViewById(R.id.iv_messagechild_play);
				ImageView btnPause = (ImageView) findViewById(R.id.iv_messagechild_pause);
				seekbar = (SeekBar) v.findViewById(R.id.child_seekbar);

				btnPlay.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							// TextView tvPath = (TextView)
							// v.findViewById(R.id.tv_secret_path);
							// String realPath = tvPath.getText().toString();
							// Log.d("Tag" , realPath);
							mp.reset();
							mp.setDataSource(playPath);
							mp.prepare();
							mp.start();
							seekbar.setOnSeekBarChangeListener(seekbarListener);

							totalTime = mp.getDuration();
							timeElapsed = mp.getCurrentPosition();
							seekbar.setMax((int) totalTime);
							thread();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

				btnPause.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mp.isPlaying()) {
							mp.pause();
							timeElapsed = mp.getCurrentPosition();
						}
					}
				});
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// MENU
		View menu = (View) findViewById(R.id.message_menu);
		logoBitmap = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.logo1_icon));
		toolbarBtnBitmap = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.menu_btn));
		toolbarBackBitmap = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.back_btn));

		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		ImageView menuBtn = (ImageView) menu.findViewById(R.id.iv_toolbar_btn);
		ImageView backBtn = (ImageView) menu.findViewById(R.id.iv_toolbar_back);

		logo.setBackground(logoBitmap);
		menuBtn.setBackground(toolbarBtnBitmap);
		backBtn.setBackground(toolbarBackBitmap);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mp.isPlaying())
			mp.pause();
		if (mp != null)
			mp.release();

		logoBitmap.getBitmap().recycle();
		toolbarBtnBitmap.getBitmap().recycle();
		toolbarBackBitmap.getBitmap().recycle();
	}

	public void thread() {
		Runnable task = new Runnable() {
			public void run() {
				try {
					while (mp.isPlaying()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (mp != null) {
							seekbar.setProgress(mp.getCurrentPosition());
							if (!mp.isPlaying())
								isPlaying = false;
						}
					}
				} catch (NullPointerException e) {
				} catch (IllegalStateException e) {
				}
			}
		};
		thread = new Thread(task);
		thread.start();
	}

	public void onDestroy() {
		super.onDestroy();
		RecycleUtils.recursiveRecycle(getWindow().getDecorView());
		System.gc();
		listView = null;
		adapter = null;
		items = null;
		tvMessageList = null;
		btnDelete = null;
		jsonParser = null;
		seekbar = null;
		thread = null;
		child = null;
		toolbar = null;
		dlDrawer = null;
		dtToggle = null;
		logoBitmap = null;
		toolbarBtnBitmap = null;
		toolbarBackBitmap = null;
		System.gc();
	}

	protected void GetSelectedPositions() { // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<GroupItem> grouparray = adapter.getcheckedposition();
		for (int i = 0; i < grouparray.size(); i++) {
			Log.d("REMOVE_PATH", grouparray.get(i).strFilePath);
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/HeartHZ/";
			path += grouparray.get(i).strFilePath;
			new File(path).delete();
		}
		for (int i = grouparray.size() - 1; i >= 0; i--) {
			items.remove(grouparray.get(i).iPosition);
		}
		adapter.notifyDataSetChanged();
	}

	protected class GroupItem {
		String strNum;
		String strTitle;
		String strSender;
		String strTime;
		String strFilePath;
		int iPosition;
		boolean isCheckedFlag;

		List<ChildItem> childItems = new ArrayList<ChildItem>();
	}

	protected class GroupHolder {
		TextView tvNum;
		TextView tvTitle;
		TextView tvSender;
		TextView tvTime;
		TextView tvPath;
		ToggleButton tvCb;
	}

	protected class ChildItem {
		int[] iTag;
		String strPic;
		String strFileNo;
		String strDate;
	}

	protected class ChildHolder {
		ImageView ivAlbumArt;
		TextView tvSender;
		TextView tvTag;
		TextView tvFinish;
	}

	private class MessageListAdapter extends AnimatedExpandableListAdapter {
		private LayoutInflater inflater;
		private List<GroupItem> items;

		public MessageListAdapter(Context context, List<GroupItem> items) {
			this.items = items;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public GroupItem getGroup(int groupPosition) {
			return items.get(groupPosition);
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder holder;
			GroupItem item = getGroup(groupPosition);
			if (convertView == null) {
				holder = new GroupHolder();
				convertView = inflater.inflate(R.layout.list_message1, parent,
						false);

				holder.tvNum = (TextView) convertView
						.findViewById(R.id.tv_messagelist_num);
				holder.tvPath = (TextView) convertView
						.findViewById(R.id.tv_secret_path);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_messagelist_title);
				holder.tvSender = (TextView) convertView
						.findViewById(R.id.tv_messagelist_sender);
				holder.tvTime = (TextView) convertView
						.findViewById(R.id.tv_messagelist_maxtime);
				holder.tvCb = (ToggleButton) convertView
						.findViewById(R.id.tv_messagelist_cb);

				// FONT
				holder.tvNum.setTypeface(Typeface.createFromAsset(getAssets(),
						"fonts/DINPRO-MEDIUM.ttf"));
				holder.tvTitle.setTypeface(Typeface.createFromAsset(
						getAssets(), "fonts/DINPRO-MEDIUM.ttf"));
				holder.tvSender.setTypeface(Typeface.createFromAsset(
						getAssets(), "fonts/DINPRO-MEDIUM.ttf"));
				holder.tvTime.setTypeface(Typeface.createFromAsset(getAssets(),
						"fonts/AppleSDGothicNeo-Regular.otf"));
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}

			path = getFilePath(groupPosition, 0);
			fileNames[groupPosition] = path;

			String duration = getDuration(path);
			Log.d("FILE", groupPosition + " " + path + " " + duration);

			holder.tvNum.setText(item.strNum);
			holder.tvSender.setText(item.strSender);
			holder.tvTitle.setText(item.strTitle);
			holder.tvTime.setText(duration);

			if (mode == 1) {
				holder.tvTime.setVisibility(View.INVISIBLE);
				holder.tvCb.setVisibility(View.VISIBLE);
				holder.tvCb.setOnClickListener(myCheckChangList);
				holder.tvCb.setTag(groupPosition);
			}

			return convertView;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			return items.size();
		}

		@Override
		public View getRealChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			ImageLoader imageLoader = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory()
					.displayer(new RoundedBitmapDisplayer(3000)).cacheOnDisc()
					.resetViewBeforeLoading()
					.showImageForEmptyUri(R.drawable.default_profile)
					.showImageOnFail(R.drawable.default_profile).build();

			ChildHolder holder;
			ChildItem item = getChild(groupPosition, childPosition);
			if (convertView == null) {
				holder = new ChildHolder();
				convertView = inflater.inflate(R.layout.list_child, parent,
						false);
				holder.ivAlbumArt = (ImageView) convertView
						.findViewById(R.id.iv_messagechild_sender);
				holder.tvFinish = (TextView) convertView
						.findViewById(R.id.tv_messagechild_finish);
				holder.tvTag = (TextView) convertView
						.findViewById(R.id.tv_messagechild_tag);
				convertView.setTag(holder);
			} else {
				holder = (ChildHolder) convertView.getTag();
			}

			String e = getEmotion(groupPosition, childPosition);
			String t = getTime(groupPosition, childPosition);
			String w = getWeather(groupPosition, childPosition);
			String d = getDate(groupPosition, childPosition);

			String s = "";

			if (e != null)
				s += e + " / ";
			if (t != null)
				s += t + " / ";
			if (w != null)
				s += w + " / ";
			if (!(d.equals("0000-00-00")))
				s += d + " /";

			s = replaceLast(s, "/", "");
			holder.tvTag.setText(s);
			holder.tvFinish.setText(getDuration(getFilePath(groupPosition, 0)));
			imageLoader.displayImage(item.strPic, holder.ivAlbumArt, options);

			return convertView;
		}

		@Override
		public ChildItem getChild(int groupPosition, int childPosition) {
			return items.get(groupPosition).childItems.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getRealChildrenCount(int groupPosition) {
			return items.get(groupPosition).childItems.size();
		}

		public String getFileNo(int groupPosition, int childPosition) {
			return getChild(groupPosition, childPosition).strFileNo;
		}

		public String getFilePath(int groupPosition, int childPosition) {
			Log.i("FILE", " " + groupPosition + " " + childPosition + " ");
			String path = Environment.getExternalStorageDirectory().getPath();
			path = path + "/HeartHZ/" + getFileNo(groupPosition, childPosition)
					+ ".wav";
			return path;
		}

		public int getTag(int groupPosition, int childPosition, int index) {
			int[] fileTag = getChild(groupPosition, childPosition).iTag;
			return fileTag[index];
		}

		public String getEmotion(int groupPosition, int childPosition) {
			String emotion = "";
			int i = getTag(groupPosition, childPosition, 0);

			switch (i) {
			case 1:
				emotion = "Joy";
				break;
			case 2:
				emotion = "Gloomy";
				break;
			case 3:
				emotion = "Relaxed";
				break;
			case 4:
				emotion = "Tired";
				break;
			default:
				emotion = null;
				break;
			}

			return emotion;
		}

		public String getWeather(int groupPosition, int childPosition) {
			String weather = "";
			int i = getTag(groupPosition, childPosition, 0);

			switch (i) {
			case 1:
				weather = "Sunny";
				break;
			case 2:
				weather = "Rainy";
				break;
			case 3:
				weather = "Snowy";
				break;
			case 4:
				weather = "Lighting";
				break;
			case 5:
				weather = "Cloudy";
				break;
			default:
				weather = null;
				break;
			}

			return weather;
		}

		public String getTime(int groupPosition, int childPosition) {
			String time = "";
			int i = getTag(groupPosition, childPosition, 0);

			switch (1) {
			case 1:
				time = "Morning";
				break;
			case 2:
				time = "DayTime";
				break;
			case 3:
				time = "Sunset";
				break;
			case 4:
				time = "Night";
				break;
			case 5:
				time = "Break of Day";
				break;
			default:
				time = null;
				break;
			}

			return time;
		}

		public String getDate(int groupPosition, int childPosition) {
			return getChild(groupPosition, childPosition).strDate;
		}

		// 체크 이벤트
		GroupItem getselectedposition(int position) {
			return ((GroupItem) getGroup(position));
		}

		ArrayList<GroupItem> getcheckedposition() {
			ArrayList<GroupItem> checkedposition = new ArrayList<GroupItem>();
			for (GroupItem p : items) {
				if (p.isCheckedFlag)
					checkedposition.add(p);
			}
			return checkedposition;
		}

		private OnClickListener myCheckChangList = new OnClickListener() {
			public void onClick(View v) {
				if (((ToggleButton) v).isChecked()) {
					getselectedposition((Integer) v.getTag()).isCheckedFlag = true;
					Log.i("SELECT BUTTON",
							"SELECT TURE : "
									+ String.valueOf((Integer) v.getTag()));
				} else {
					getselectedposition((Integer) v.getTag()).isCheckedFlag = false;
					Log.i("SELECT BUTTON",
							"SELECT FALSE : "
									+ String.valueOf((Integer) v.getTag()));
				}
			}
		};

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}

	}

	// DELETE BUTTON
	public void mOnClick(View v) {
		if (mode == 0) {
			mode = 1;
			adapter = new MessageListAdapter(this, items);
			listView = (AnimatedExpandableListView) findViewById(R.id.expandlistview);
			listView.setAdapter(adapter);
		} else {
			mode = 0;
			GetSelectedPositions();
			for (int i = 0; i < items.size(); i++) {
				items.get(i).strNum = "0" + String.valueOf(i + 1);
				items.get(i).iPosition = i;
			}
			adapter = new MessageListAdapter(this, items);
			listView = (AnimatedExpandableListView) findViewById(R.id.expandlistview);
			listView.setAdapter(adapter);

		}
	}

	OnSeekBarChangeListener seekbarListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser)
				mp.seekTo(progress);
		}
	};

	public void getFileList() {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/HeartHZ";
		File tempfile = new File(path);

		File[] files = tempfile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				File file = new File(dir, filename);
				if (filename.startsWith("heart") && filename.endsWith(".wav")) {
					return true;
				} else
					return false;
			}
		});

		fileNames = new String[files.length + 1];
		for (int i = 0; i < files.length; i++) {
			// fileNames[i] = files[i].getName().replace(".wav", "");
			// Log.d("FILE_NAME 2", fileNames[i]);
		}

	}

	class LoadAllMessage extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(Config.TAG_USER_ID, Integer
					.toString(SignInActivity.iUserId)));
			// params.add(new BasicNameValuePair("files", fileNames));
			JSONObject json = jsonParser.makeHttpRequest(
					Config.URL_GET_MESSAGE_LIST, "GET", params);

			if (json != null) {
				Log.d(CURRENT_ACTIVITY + "_ALL_FILES", json.toString());

				try {
					int success = json.getInt(Config.TAG_SUCCESS);

					if (success == 0) {
						JSONArray fileObj = json.getJSONArray(Config.TAG_FILES);

						for (int i = 0; i < fileObj.length(); i++) {
							JSONObject c = fileObj.getJSONObject(i);

							String strFileNo = c.getString(Config.TAG_FILE_NO);
							File file = new File(folderPath + strFileNo
									+ ".wav");

							Log.d("Tag", file.getName());
							if (file.exists()) {

								String strDate = c.getString(Config.TAG_DATE);
								String strFileTitle = c
										.getString(Config.TAG_FILE_TITLE);
								String strFriendPic = c.getString("friend_pic");
								String strFriendName = c
										.getString(Config.TAG_FRIEND_NAME);
								String strFilePath = c
										.getString(Config.TAG_FILE_PATH);

								int arrTag[] = new int[3];
								arrTag[0] = c.getInt(Config.TAG_EMOTION);
								arrTag[1] = c.getInt(Config.TAG_WEATHER);
								arrTag[2] = c.getInt(Config.TAG_TIME);

								GroupItem item = new GroupItem();
								item.iPosition = numbering - 1;

								if (numbering / 10 < 10)
									item.strNum = "0" + numbering;
								else
									item.strNum = Integer.toString(numbering);
								numbering++;

								item.strTitle = strFileTitle;
								item.strSender = strFriendName;
								item.strTime = "00:00";
								item.strFilePath = strFileNo + ".wav";

								Log.d("TEST", "" + item.iPosition);

								child = new ChildItem();
								child.iTag = arrTag;
								child.strPic = strFriendPic;
								child.strFileNo = strFileNo;
								child.strDate = strDate;

								item.childItems.add(child);
								items.add(item);
								// break;
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			adapter = new MessageListAdapter(getBaseContext(), items);
			listView.setAdapter(adapter);
		}
	}

	private String replaceLast(String string, String toReplace,
			String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length(),
							string.length());
		} else {
			return string;
		}
	}

	public String getDuration(String source) {
		Log.d("tag", source);

		String time = "0";
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(source);
			time = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long timeInmillisec = Long.parseLong(time);
		long duration = timeInmillisec / 1000;
		long hour = duration / 3600;
		long minutes = (duration - hour * 3600) / 60;
		long seconds = duration - (hour * 3600 + minutes * 60);

		String strMin = String.format("%02d", minutes);
		String strSec = String.format("%02d", seconds);

		return strMin + ":" + strSec;
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

	public void menuClick(View v) {
		Intent i;

		switch (v.getId()) {
		case R.id.ll_menu_home:
			i = new Intent(MessagePlayerActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_message:
			dlDrawer.closeDrawers();
			break;

		case R.id.ll_menu_setting:
			dlDrawer.closeDrawers();
			i = new Intent(MessagePlayerActivity.this, SettingActivity.class);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_logout:
			dlDrawer.closeDrawers();

			SharedPreferenceUtil pref = SignInActivity.pref;

			pref.put("first", false);
			pref.put(Config.TAG_USER_ID, "");
			pref.put(Config.TAG_PW, "");
			pref.put(Config.TAG_MODEL, "");
			pref.put(Config.TAG_NAME, "");
			pref.put(Config.TAG_PHONE, "");
			pref.put(Config.TAG_PIC_PATH, "");
			stopService(new Intent(MessagePlayerActivity.this,
					ServicePage.class));

			finish();
			startActivity(new Intent(MessagePlayerActivity.this,
					SignInActivity.class));
			break;
		}
	}
}
