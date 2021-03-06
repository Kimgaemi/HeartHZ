package com.heart.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heart.R;
import com.heart.service.ServicePage;
import com.heart.util.Config;
import com.heart.util.JSONParser;
import com.heart.util.RecycleUtils;
import com.heart.util.SharedPreferenceUtil;
import com.heart.util.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class SettingActivity extends AppCompatActivity {

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private static int RESULT_LOAD_IMAGE = 1;

	// JSON
	private JSONParser jsonParser = new JSONParser();

	// VIEW
	/*
	 * private Button btnSave; private Button btnBack;
	 */
	private TextView tvName;
	private ImageView ivPic;
	private Window window = null;
	private TextView tvSetting = null;
	private TextView tvAccountSetting = null;
	private TextView tvDeviceSetting = null;
	private TextView tvNoticeSetting = null;

	private ImageView ivAccountSetting = null;
	private ImageView ivAccountSettingNext = null;
	private ImageView ivDeviceSetting = null;
	private ImageView ivDeviceSettingNext = null;
	private ImageView ivNoticeSetting = null;
	private ImageView ivNoticeSettingNext = null;

	private BitmapDrawable AS;
	private BitmapDrawable DS;
	private BitmapDrawable NS;
	private BitmapDrawable NN;

	// VARIABLE
	private int iUserId;
	private String strName;
	private String strPic;
	private String strNewPic = null;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;
	private BitmapDrawable logoBitmap;
	private BitmapDrawable toolbarBtnBitmap;
	private BitmapDrawable toolbarBackBitmap;

	public SharedPreferenceUtil pref;
	public ImageLoader imageLoader;
	public DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// iUserId = SignInActivity.iUserId;
		MainActivity.imageInit(this);
		// new GetUserDetails().execute();

		ivPic = (ImageView) findViewById(R.id.iv_pic);
		tvName = (TextView) findViewById(R.id.tv_name);

		pref = SignInActivity.pref;
		iUserId = pref.getValue(Config.TAG_USER_ID, 0);
		strName = pref.getValue(Config.TAG_NAME, null);
		strPic = pref.getValue(Config.TAG_PIC_PATH, null);

		tvName.setText(strName);

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory()
				.displayer(new RoundedBitmapDisplayer(1000)).cacheOnDisc()
				.resetViewBeforeLoading()
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile).build();

		imageLoader.displayImage(strPic, ivPic, options);

		// tvName = (EditText) findViewById(R.id.tv_name);
		/*
		 * btnSave = (Button) findViewById(R.id.btn_main_setting); btnBack =
		 * (Button) findViewById(R.id.btn_setting_back);
		 */

		// ivPic.setImageResource(R.drawable.setting_profile_user2);
		// tvName.setText(SignInActivity.strName);

		// ivPic.setOnClickListener(settingListener);
		/*
		 * btnSave.setOnClickListener(settingListener);
		 * btnBack.setOnClickListener(settingListener);
		 */

		tvSetting = (TextView) findViewById(R.id.tv_setting);
		tvAccountSetting = (TextView) findViewById(R.id.tv_accout_setting);
		tvDeviceSetting = (TextView) findViewById(R.id.tv_device_setting);
		tvNoticeSetting = (TextView) findViewById(R.id.tv_notice_setting);

		ivAccountSetting = (ImageView) findViewById(R.id.iv_account_setting);
		ivAccountSettingNext = (ImageView) findViewById(R.id.iv_account_setting_next);
		ivDeviceSetting = (ImageView) findViewById(R.id.iv_device_setting);
		ivDeviceSettingNext = (ImageView) findViewById(R.id.iv_device_setting_next);
		ivNoticeSetting = (ImageView) findViewById(R.id.iv_notice_setting);
		ivNoticeSettingNext = (ImageView) findViewById(R.id.iv_notice_setting_next);

		// STATUSBAR COLOR
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window = this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_base));
		}

		// MENU
		menuInit();

		// fonts
		tvSetting.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		tvName.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		tvAccountSetting.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		tvDeviceSetting.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		tvNoticeSetting.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
	}

	@Override
	public void onResume() {
		super.onResume();
		AS = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.accountsetting_icon));
		DS = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.devicesetting_icon));
		NS = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.noticesetting_icon));
		NN = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.next_right_btn));
		// MENU
		View menu = (View) findViewById(R.id.setting_menu);
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

		ivAccountSetting.setBackground(AS);
		ivAccountSettingNext.setBackground(NN);
		ivDeviceSetting.setBackground(DS);
		ivDeviceSettingNext.setBackground(NN);
		ivNoticeSetting.setBackground(NS);
		ivNoticeSettingNext.setBackground(NN);
	}

	@Override
	public void onPause() {
		super.onPause();
		logoBitmap.getBitmap().recycle();
		toolbarBtnBitmap.getBitmap().recycle();
		toolbarBackBitmap.getBitmap().recycle();
		AS.getBitmap().recycle();
		DS.getBitmap().recycle();
		NS.getBitmap().recycle();
		NN.getBitmap().recycle();
	}

	@Override
	public void onDestroy() {
		RecycleUtils.recursiveRecycle(getWindow().getDecorView());
		System.gc();
		super.onDestroy();

	}

	public void settingClick(View v) {
		switch (v.getId()) {
		case R.id.rl_account_setting:
			Intent i = new Intent(SettingActivity.this,
					SettingAccountActivity.class);
			startActivity(i);
			break;
		}
	}

	public void remoteSetting(View v) {
		Intent i = new Intent(SettingActivity.this, StateChangeActivity.class);
		startActivity(i);
	}

	/*
	 * private OnClickListener settingListener = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * switch (v.getId()) { case R.id.iv_pic: Intent intent = new
	 * Intent(Intent.ACTION_PICK,
	 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	 * 
	 * intent.setType("image/*"); intent.putExtra("crop", "true");
	 * intent.putExtra("aspectX", 1); intent.putExtra("aspectY", 1);
	 * intent.putExtra("outputX", 280); intent.putExtra("outputY", 280);
	 * intent.putExtra("return-data", true);
	 * 
	 * 
	 * // NEED TO CROP startActivityForResult(intent, RESULT_LOAD_IMAGE); break;
	 * 
	 * //case R.id.btn_main_setting: // new SaveProfile().execute(); //break;
	 * 
	 * case R.id.btn_setting_back: setResult(RESULT_OK, null); finish(); break;
	 * } } };
	 * 
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { super.onActivityResult(requestCode,
	 * resultCode, data);
	 * 
	 * if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null
	 * != data) {
	 * 
	 * // Bitmap bitmap = data.getParcelableExtra("data");
	 * 
	 * // Uri에서 파일명 추출 Uri selectedImage = data.getData(); String[]
	 * filePathColumn = { MediaStore.Images.Media.DATA };
	 * 
	 * Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
	 * null, null, null); cursor.moveToFirst();
	 * 
	 * int columnIndex = cursor.getColumnIndex(filePathColumn[0]); strNewPic =
	 * cursor.getString(columnIndex); cursor.close();
	 * 
	 * // ivPic.setImageBitmap(bitmap);
	 * ivPic.setImageBitmap(BitmapFactory.decodeFile(strNewPic));
	 * 
	 * Log.d(CURRENT_ACTIVITY + "_path", strNewPic); Log.d(CURRENT_ACTIVITY +
	 * "_selectedImage", selectedImage.toString()); } }
	 */
	/*
	 * private class SaveProfile extends AsyncTask<Void, Integer, String> {
	 * 
	 * @Override protected String doInBackground(Void... params) { String
	 * newName = tvName.getText().toString(); MainActivity.strName = newName;
	 * return uploadFile(newName); }
	 * 
	 * @SuppressWarnings("deprecation") private String uploadFile(String name) {
	 * String strResponse = null;
	 * 
	 * HttpClient httpclient = new DefaultHttpClient(); HttpPost httppost = new
	 * HttpPost(Config.URL_USER_UPDATE);
	 * 
	 * // SETTING HEADER httppost.setHeader("Accept-Charset", "UTF-8");
	 * httppost.setHeader("Connection", "Keep-Alive");
	 * httppost.setHeader("ENCTYPE", "multipart/form-data");
	 * 
	 * try { MultipartEntity entity = new MultipartEntity();
	 * 
	 * if (strNewPic != null) { File sourceFile = new File(strNewPic);
	 * 
	 * // ADDING FILE DATA TO HTTP BODY entity.addPart("image", new
	 * FileBody(sourceFile)); } else { strNewPic = strPic; }
	 * 
	 * // EXTRA PARAMETERS IF YOU WANT TO PASS TO SERVER
	 * entity.addPart(Config.TAG_USER_ID, new
	 * StringBody(Integer.toString(iUserId))); entity.addPart(Config.TAG_NAME,
	 * new StringBody(name, Charset.forName("UTF-8")));
	 * httppost.setEntity(entity);
	 * 
	 * // MAKING SERVER CALL HttpResponse response =
	 * httpclient.execute(httppost); HttpEntity r_entity = response.getEntity();
	 * 
	 * int statusCode = response.getStatusLine().getStatusCode(); if (statusCode
	 * == 200) { // SERVER RESPONSE strResponse =
	 * EntityUtils.toString(r_entity); } else { strResponse =
	 * "Error occurred! Http Status Code: " + statusCode; }
	 * 
	 * } catch (ClientProtocolException e) { strResponse = e.toString(); } catch
	 * (IOException e) { strResponse = e.toString(); }
	 * 
	 * return strResponse; }
	 * 
	 * @Override protected void onPostExecute(String result) {
	 * super.onPostExecute(result); Log.e(CURRENT_ACTIVITY,
	 * "RESPONSE FROM SERVER: " + result);
	 * 
	 * // tvName handling tvName.clearFocus(); InputMethodManager imm =
	 * (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	 * imm.hideSoftInputFromWindow(tvName.getWindowToken(), 0); } }
	 */
	class GetUserDetails extends AsyncTask<String, String, String> {
		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(Config.TAG_USER_ID, Integer
						.toString(iUserId)));

				JSONObject json = jsonParser.makeHttpRequest(
						Config.URL_GET_USER_PROFILE, "GET", params);

				if (json != null) {
					Log.d("SettingProfile_USER", json.toString());
					int success = json.getInt(Config.TAG_SUCCESS);
					if (success == 0) {

						JSONArray userObj = json.getJSONArray(Config.TAG_USER);
						JSONObject user = userObj.getJSONObject(0);
						Log.d("SettingProfile", user.toString());

						strName = user.getString(Config.TAG_NAME);
						strPic = user.getString(Config.TAG_PIC_PATH);

						Log.d("SettingProfile_Pic_Path", strName + " " + strPic);

					} else { // NO PARAMETER OR USER
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			tvName.setText(strName);
			// tvName.setSelection(tvName.length());

			ImageLoader imageLoader = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory()
					.displayer(new RoundedBitmapDisplayer(1000)).cacheOnDisc()
					.resetViewBeforeLoading()
					.showImageForEmptyUri(R.drawable.default_profile)
					.showImageOnFail(R.drawable.default_profile).build();

			imageLoader.displayImage(strPic, ivPic, options);

		}
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

	private void menuInit() {

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
			i = new Intent(SettingActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_message:
			dlDrawer.closeDrawers();
			i = new Intent(SettingActivity.this, MessagePlayerActivity.class);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_setting:
			dlDrawer.closeDrawers();
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
			stopService(new Intent(SettingActivity.this, ServicePage.class));

			finish();
			startActivity(new Intent(SettingActivity.this, SignInActivity.class));
			break;
		}
	}
}
