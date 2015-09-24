package com.heart.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heart.R;
import com.heart.friend.Friend;
import com.heart.friend.FriendPagerAdapter;
import com.heart.util.Config;
import com.heart.util.CustomViewPager;
import com.heart.util.DownloadService;
import com.heart.util.JSONParser;
import com.heart.util.SlidingMenu;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class MainActivity extends AppCompatActivity {

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private static final String TAG_NO_NEW_FRIEND = "no friend";
	private static final String TAG_ALREADY_FRIEND = "already friend";
	private static final String NEW_FRIEND_ID = "relation_id";
	private static final String TAG_REGISTER = "register";
	private static final String ADD_FRIEND = "add";

	// VIEW
	private Window window = null;
	private TextView tvHome = null;
	private Button btnSelect = null;
	private LinearLayout layout = null;

	// JSON
	private JSONParser jsonParser = new JSONParser();

	// VARIABLE
	private int iUserId;
	public String strName;
	public String strPhone;
	public String strPic;
	public String strCPic;
	public String strEmail;

	// FRIEND
	private ArrayList<Friend> item = new ArrayList<Friend>();

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;

	public final int PAGES = 100;
	public static int maxPeople = 0;
	public final static int FIRST_PAGE = 0;
	public final static float BIG_SCALE = 1.0f;
	public final static float SMALL_SCALE = 0.7851f;
	public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

	public Integer[] bgColors = null;
	public Integer[] btnColors = null;

	public FriendPagerAdapter mpadapter = null;
	public CustomViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View menu = (View) findViewById(R.id.main_menu);
		btnSelect = (Button) findViewById(R.id.btn_select);
		tvHome = (TextView) findViewById(R.id.tv_home);
		layout = (LinearLayout) findViewById(R.id.nav_lv);

		// iUserId = SignInActivity.pref.getValue(Config.TAG_USER_ID, 0);
		iUserId = SignInActivity.iUserId;
		strPhone = SignInActivity.strPhone;
		strName = SignInActivity.strName;
		strPic = SignInActivity.strPic;
		strEmail = SignInActivity.strEmail;

		Log.d(CURRENT_ACTIVITY, "USER ID : " + iUserId + "/" + strName + "/"
				+ strPic);

		// CHANGING MENU CENTER ICON
		menu.findViewById(R.id.iv_toolbar_logo).setBackgroundResource(
				R.drawable.logo1_icon);

		menu.findViewById(R.id.iv_toolbar_back).setVisibility(View.INVISIBLE);

		imageInit(this); // IMAGE LOADER SETUP
		menuInit(); // SET MENU BAR

		// FONT
		tvHome.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));

		// STATUSBAR COLOR
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window = this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_base));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// CALL LoadAllFriends()
		new LoadAllFriends().execute();
	}

	/**
	 * WHEN A FRIEND PROFILE VIEW IS CLICKED ADD A FRIEND / SELECT A RECEIVER
	 * 
	 * @param v
	 *            view
	 */
	public void mainClick(View v) {

		String strFriendId = item.get(mpadapter.getCurPosition()).getId();
		String strFriendName = item.get(mpadapter.getCurPosition()).getName();
		String strFriendPhone = item.get(mpadapter.getCurPosition()).getPhone();
		String strFriendPic = item.get(mpadapter.getCurPosition()).getPicPath();
		String strFriendCPic = item.get(mpadapter.getCurPosition())
				.getCPicPath();
		String strFriendEmail = item.get(mpadapter.getCurPosition()).getEmail();

		Log.d(CURRENT_ACTIVITY, "CLICK : " + strFriendName);

		// ADD FRIEND
		if (strFriendId.equals(ADD_FRIEND)) {

			/*
			 * AlertDialog.Builder alert = new
			 * AlertDialog.Builder(MainActivity.this);
			 * alert.setTitle("WRITE DOWN THE FRIEND'S CONTACT NUMBER.");
			 * 
			 * // SET AN EDITTEXT VIEW TO GET USER INPUT final EditText
			 * editInput = new EditText(MainActivity.this);
			 * editInput.setInputType(InputType.TYPE_CLASS_PHONE);
			 * 
			 * alert.setView(editInput); alert.setPositiveButton("OK", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int whichButton) { // OK BUTTON
			 * String strValue = editInput.getText().toString();
			 * 
			 * if (strValue.equals("") || strValue == null) { // NO INPUT VALUE
			 * Toast.makeText(MainActivity.this,
			 * "WRITE DOWN CONNECT NUMBER TO ADD", Toast.LENGTH_SHORT).show(); }
			 * else { if (strValue.substring(1).equals(strPhone)) {
			 * Toast.makeText(MainActivity.this, "YOU CAN'T ADD YOURSELF",
			 * Toast.LENGTH_SHORT).show(); } else { new
			 * AddNewFriend().execute(strValue); } } } });
			 * 
			 * alert.setNegativeButton("CANCEL", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int whichButton) { // CANCELED. }
			 * }); alert.show();
			 */

			Intent in = new Intent(MainActivity.this,
					AddNewFriendActivity.class);

			startActivity(in);

		} else {
			// SELECT THE RECEIVER
			// TO RECORD ACTIVITY HAVING USER ID, FRIEND ID AND FRIEND NAME
			Intent in = new Intent(MainActivity.this, RecordActivity.class);
			in.putExtra(Config.TAG_FRIEND_ID, strFriendId);
			in.putExtra(Config.TAG_FIREND_PIC, strFriendPic);
			in.putExtra(Config.TAG_FRIEND_NAME, strFriendName);
			in.putExtra(Config.TAG_FIREND_PHONE, strFriendPhone);
			in.putExtra(Config.TAG_CPIC_PATH, strFriendCPic);
			in.putExtra(Config.TAG_USER_EMAIL, strFriendEmail);

			startActivity(in);
		}
	}

	class LoadAllFriends extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(Config.TAG_USER_ID, Integer
					.toString(iUserId)));

			JSONObject json = jsonParser.makeHttpRequest(
					Config.URL_GET_FIRENDS_LIST, "GET", params);

			if (json != null) {
				Log.d(CURRENT_ACTIVITY + "_ALL_FRIENDS", json.toString());

				try {
					int success = json.getInt(Config.TAG_SUCCESS);

					if (success == 0) {
						JSONArray userObj = json
								.getJSONArray(Config.TAG_FRIENDS);
						maxPeople = userObj.length() + 1;

						Log.d(CURRENT_ACTIVITY + "_NUM_OF_FRINEDS",
								userObj.length() + "");

						// LOOPING THROUGH ALL FRIENDS
						for (int i = 0; i < userObj.length(); i++) {
							JSONObject c = userObj.getJSONObject(i);

							// STORING EACH JSON ITEM IN VARIABLE
							String strFriendId = c
									.getString(Config.TAG_USER_ID);
							String strFriendName = c.getString(Config.TAG_NAME);
							String strFriendPhone = c
									.getString(Config.TAG_PHONE);
							String strFriendPicPath = c
									.getString(Config.TAG_PIC_PATH);
							String strFriendCPicPath = c
									.getString(Config.TAG_CPIC_PATH);
							String strFriendEmail = c
									.getString(Config.TAG_USER_EMAIL);

							Friend friend = new Friend(strFriendId,
									strFriendName, strFriendPhone,
									strFriendPicPath, strFriendCPicPath,
									strFriendEmail);
							item.add(friend);
						}

					} else {
						// NO FRIENDS FOUND
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		protected void onPostExecute(String file_url) {

			Friend friend = new Friend(ADD_FRIEND, "", "", null, null, "");
			item.add(friend);

			setUpColors(maxPeople); // SET COLOR

			// VIEW PAGER
			pager = (CustomViewPager) findViewById(R.id.myviewpager);
			pager.setCurrentItem(FIRST_PAGE);
			pager.setBackgroundColor(bgColors[0]);
			pager.setOffscreenPageLimit(3);
			pager.setPageMargin(-450);

			mpadapter = new FriendPagerAdapter(MainActivity.this,
					MainActivity.this.getSupportFragmentManager(), pager, item,
					btnSelect, bgColors, btnColors);
			pager.setAdapter(mpadapter);

			pager.setOnPageChangeListener(mpadapter);
		}
	}

	class AddNewFriend extends AsyncTask<String, String, String> {
		protected String doInBackground(String... args) {
			if (args == null) // NO FRIEND ID
				return null;

			String strPhone = args[0];
			try {
				// BEFORE ADD THE RELATION
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(Config.TAG_PHONE, strPhone));

				JSONObject json = jsonParser.makeHttpRequest(
						Config.URL_USER_DETAILS, "GET", params);

				if (json != null) {
					Log.d(CURRENT_ACTIVITY, json.toString());
					int success = json.getInt(Config.TAG_SUCCESS);
					if (success == 0) {

						JSONArray userObj = json.getJSONArray(Config.TAG_USER);
						JSONObject user = userObj.getJSONObject(0);

						Log.d(CURRENT_ACTIVITY, user.toString());
						int iNewFirendId = user.getInt(Config.TAG_USER_ID);
						String strNewFriendName = user
								.getString(Config.TAG_NAME);

						/*
						 * strNewFriendName으로 한번 물어봐주기 (strNewFriendName가 맞니?)
						 * userId와 relation_id 관계 등록
						 */

						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair(Config.TAG_USER_ID,
								Integer.toString(iUserId)));
						params.add(new BasicNameValuePair(NEW_FRIEND_ID,
								Integer.toString(iNewFirendId)));

						json = jsonParser.makeHttpRequest(
								Config.URL_SET_RELATION, "POST", params);
						Log.d(CURRENT_ACTIVITY + "_relations", json.toString());

						try {
							success = json.getInt(Config.TAG_SUCCESS);
							if (success == 0) {
								publishProgress(TAG_NO_NEW_FRIEND);
								Log.d(CURRENT_ACTIVITY,
										"RELATION SUCCESSFULLY CREATED");
							} else if (success == 1) {
								publishProgress(TAG_ALREADY_FRIEND);
								Log.d(CURRENT_ACTIVITY,
										"RELATION ALREADY CREATED");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else if (success == 1) {
						// NEED TO REGISTER FIRST
						publishProgress(TAG_REGISTER);
						Log.d(CURRENT_ACTIVITY + "_RELATIONS",
								"NEED TO REGISTER");
					} else { // NO PARAMETER
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

			if (values[0].equals(TAG_REGISTER)) {
				Toast.makeText(MainActivity.this,
						"NON-REGISTERED FRIEND. INVITE HIM!",
						Toast.LENGTH_SHORT).show();
				// // SMS 보내기 ////

			} else if (values[0].equals(TAG_NO_NEW_FRIEND)) {
				Toast.makeText(MainActivity.this, "COMPLETE!",
						Toast.LENGTH_SHORT).show();

				Intent intent = getIntent();
				finish();
				startActivity(intent);

			} else if (values[0].equals(TAG_ALREADY_FRIEND)) {
				Toast.makeText(MainActivity.this, "WE ARE ALREADY FRIENDS",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static void imageInit(Activity v) {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(1000)).cacheOnDisc()
				.cacheInMemory().imageScaleType(ImageScaleType.EXACTLY).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				v.getBaseContext()).defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(100 * 1024 * 1024).build();

		ImageLoader.getInstance().init(config);
	}

	private void setUpColors(int num) {
		// BG
		Integer[] bg_colors_temp = new Integer[100];
		Integer[] btn_colors_temp = new Integer[100];
		Integer color1 = getResources().getColor(R.color.page2_bg_color1);
		Integer color2 = getResources().getColor(R.color.page2_btn_color1);
		for (int i = 0; i < num; i++) {
			bg_colors_temp[i] = color1;
			btn_colors_temp[i] = color2;
		}
		bg_colors_temp[num] = color1;
		btn_colors_temp[num] = color2;
		// Integer color2 = getResources().getColor(R.color.page2_bg_color2);
		// Integer color3 = getResources().getColor(R.color.page2_bg_color3);
		// Integer color4 = getResources().getColor(R.color.page2_bg_color4);
		// Integer color5 = getResources().getColor(R.color.background_base);

		// Integer[] bg_colors_temp = { color5, color1, color2, color3, color4,
		// color4 };

		// BTN
		// color1 = getResources().getColor(R.color.page2_btn_color1);
		// color2 = getResources().getColor(R.color.page2_btn_color2);
		// color3 = getResources().getColor(R.color.page2_btn_color3);
		// color4 = getResources().getColor(R.color.page2_btn_color4);
		// color5 = getResources().getColor(R.color.button_base);
		//
		// Integer[] btn_colors_temp = { color5, color1, color2, color3, color4,
		// color4, };
		bgColors = bg_colors_temp;
		btnColors = btn_colors_temp;
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
		dlDrawer.openDrawer(Gravity.RIGHT);
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
			dlDrawer.closeDrawers();
			break;

		case R.id.ll_menu_message:
			i = new Intent(MainActivity.this, MessagePlayerActivity.class);
			dlDrawer.closeDrawers();
			startActivity(i);
			break;

		case R.id.ll_menu_setting:
			i = new Intent(MainActivity.this, SettingActivity.class);
			dlDrawer.closeDrawers();
			startActivity(i);
			break;

		case R.id.ll_menu_logout:
			dlDrawer.closeDrawers();
			finish();
			startActivity(new Intent(MainActivity.this, SignInActivity.class));
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		System.gc();
	}

	@Override
	public void onBackPressed() {/*
								 * 
								 * if (System.currentTimeMillis() >
								 * backKeyPressedTime + 2000) {
								 * backKeyPressedTime =
								 * System.currentTimeMillis(); toast =
								 * Toast.makeText(MainActivity.this,
								 * "Press back one more time to exit",
								 * Toast.LENGTH_SHORT); toast.show(); return; }
								 * if (System.currentTimeMillis() <=
								 * backKeyPressedTime + 2000) { finish();
								 * toast.cancel(); }
								 */
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean isKill = intent.getBooleanExtra("KILL_ACT", false);
		if (isKill)
			close();
		startActivity(new Intent(MainActivity.this, SignInActivity.class));
	}

	private void close() {
		stopService(new Intent(getApplicationContext(), DownloadService.class));
		finish();
		int nSDKVersion = Integer.parseInt(Build.VERSION.SDK);
		if (nSDKVersion < 8) {
			// 2.1이하
			ActivityManager actMng = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			actMng.restartPackage(getPackageName());
		}

		else {
			new Thread(new Runnable() {
				public void run() {
					ActivityManager actMng = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					String strProcessName = getApplicationInfo().processName;
					while (true) {
						List<RunningAppProcessInfo> list = actMng
								.getRunningAppProcesses();
						for (RunningAppProcessInfo rap : list) {
							if (rap.processName.equals(strProcessName)) {
								if (rap.importance >= RunningAppProcessInfo.IMPORTANCE_BACKGROUND)
									actMng.restartPackage(getPackageName());
								Thread.yield();
								break;
							}
						}
					}
				}
			}, "Process Killer").start();
		}
	}
}
