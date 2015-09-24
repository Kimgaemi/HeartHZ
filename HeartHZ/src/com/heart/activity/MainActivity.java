package com.heart.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.SimpleFormatter;

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
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
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

	// FRIEND
	private ArrayList<Friend> item = new ArrayList<Friend>();

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;

	public final static int PAGES = 100;
	public static int maxPeople = 0;
	public static int FIRST_PAGE = 0;
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
		menuInit();
		new LoadAllFriends().execute();
				
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
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

		Log.d(CURRENT_ACTIVITY, "CLICK : " + strFriendName);

		// ADD FRIEND
		if (strFriendId.equals(ADD_FRIEND)) {
			Intent in = new Intent(MainActivity.this,AddNewFriendActivity.class);

			startActivity(in);

		} else {
			// SELECT THE RECEIVER
			// TO RECORD ACTIVITY HAVING USER ID, FRIEND ID AND FRIEND NAME
			Intent in = new Intent(MainActivity.this, RecordActivity.class);
			in.putExtra(Config.TAG_FRIEND_ID, strFriendId);
			in.putExtra(Config.TAG_FIREND_PIC, strFriendPic);
			in.putExtra(Config.TAG_FRIEND_NAME, strFriendName);
			in.putExtra(Config.TAG_FIREND_PHONE, strFriendPhone);

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
						item.clear();
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

							int daysOver = getHistory("0"+strFriendPhone);
							Log.d("tag", daysOver+"");

							Friend friend = new Friend(strFriendId,
									strFriendName, strFriendPhone,
									strFriendPicPath, daysOver);
							item.add(friend);
						}
						item.add(new Friend(ADD_FRIEND, "", "", "", 0));
						// DUmmy Friend
						item.add(new Friend("", "", "", "", 0));
						item.add(new Friend("", "", "", "", 0));
						item.add(new Friend("", "", "", "", 0));
						item.add(new Friend("", "", "", "", 0));
					} else {
						// NO FRIENDS FOUND
						maxPeople = 1;
						item.add(new Friend(ADD_FRIEND, "", "", "", 0));
						// DUmmy Friend
						item.add(new Friend("", "", "", "", 0));
						item.add(new Friend("", "", "", "", 0));
						item.add(new Friend("", "", "", "", 0));
						item.add(new Friend("", "", "", "", 0));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			setUpColors(maxPeople + 4); // SET COLOR

			// VIEW PAGER
			pager = (CustomViewPager) findViewById(R.id.myviewpager);

			pager.postDelayed(new Runnable() {
				@Override
				public void run() {
					pager.setCurrentItem(FIRST_PAGE);
					if (FIRST_PAGE == maxPeople - 1) {
						pager.setPagingEnabled(false);
						pager.setPagingMax(true);
					} else {
						pager.setPagingEnabled(true);
						pager.setPagingMax(false);
					}
				}
			}, 100);

			pager.setOffscreenPageLimit(4);
			pager.setBackgroundColor(bgColors[0]);
			pager.setPageMargin(-450);

			mpadapter = new FriendPagerAdapter(MainActivity.this,
					MainActivity.this.getSupportFragmentManager(), pager, item,
					btnSelect, bgColors, btnColors);

			pager.setAdapter(mpadapter);
			pager.setOnPageChangeListener(mpadapter);
		}
	}



	private int getHistory(String phoneNum) {
		/* Query the CallLog Content Provider */

		String selection = CallLog.Calls.NUMBER + "=?"; //WHERE절 타입이 
		String[] selectionArgs = { phoneNum }; 
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
		Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, selectionArgs, strOrder);

		if(managedCursor.getCount() > 0) {
			// long date = managedCursor.getLong(managedCursor.getColumnIndex(CallLog.Calls.DATE));
			long date = 111123011;

			Log.d("tag전화", date+"");
			int lastDate = GetDifferenceOfDate(Long.valueOf(date));
			managedCursor.close();
			return lastDate;
		} else return 365;   
	}

	public int GetDifferenceOfDate(long diff) {

		// 현재시간
		Calendar cal = Calendar.getInstance();
		int nYear1 = cal.get(cal.YEAR);
		int nMonth1 = cal.get(cal.MONTH) + 1;
		int nDate1 = cal.get(cal.DATE);

		// 구하고자 하는 시간
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String str = df.format(diff);
		int nYear2 = Integer.parseInt((String) str.subSequence(0, 4));
		int nMonth2 = Integer.parseInt((String) str.subSequence(5, 7));
		int nDate2 = Integer.parseInt((String) str.subSequence(8, 10));
		System.out.println(str );
		System.out.println(nYear2 + " " + nMonth2 + " " +nDate2 );
		int nTotalDate1 = 0, nTotalDate2 = 0, nDiffOfYear = 0, nDiffOfDay = 0;

		if (nYear1 > nYear2) {
			for (int i = nYear2; i < nYear1; i++) {
				cal.set(i, 12, 0);
				nDiffOfYear += cal.get(Calendar.DAY_OF_YEAR);
			}
			nTotalDate1 += nDiffOfYear;
		} else if (nYear1 < nYear2) {
			for (int i = nYear1; i < nYear2; i++) {
				cal.set(i, 12, 0);
				nDiffOfYear += cal.get(Calendar.DAY_OF_YEAR);
			}
			nTotalDate2 += nDiffOfYear;
		}

		cal.set(nYear1, nMonth1 - 1, nDate1);
		nDiffOfDay = cal.get(Calendar.DAY_OF_YEAR);
		nTotalDate1 += nDiffOfDay;

		cal.set(nYear2, nMonth2 - 1, nDate2);
		nDiffOfDay = cal.get(Calendar.DAY_OF_YEAR);
		nTotalDate2 += nDiffOfDay;

		return nTotalDate1 - nTotalDate2;
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
						user
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
