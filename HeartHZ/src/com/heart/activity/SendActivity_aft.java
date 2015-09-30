package com.heart.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heart.R;
import com.heart.service.ServicePage;
import com.heart.util.Config;
import com.heart.util.Recorder;
import com.heart.util.RecycleUtils;
import com.heart.util.SharedPreferenceUtil;
import com.heart.util.SlidingMenu;

public class SendActivity_aft extends AppCompatActivity {

	// VIEW
	private Window window = null;
	private TextView tvSuccessful = null;
	private TextView tvMent1 = null;
	private TextView tvMent2 = null;
	private Button btnNewMessage = null;
	private ImageView sendingIcon = null;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;
	private BitmapDrawable logoBitmap;
	private BitmapDrawable toolbarBtnBitmap;
	private BitmapDrawable toolbarBackBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send2);

		tvSuccessful = (TextView) findViewById(R.id.tv_successful_message);
		tvMent1 = (TextView) findViewById(R.id.tv_congraturation);
		tvMent2 = (TextView) findViewById(R.id.tv_sended);
		sendingIcon = (ImageView) findViewById(R.id.sending_icon);
		btnNewMessage = (Button) findViewById(R.id.btn_new_message);

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
		View menu = (View) findViewById(R.id.send2_menu);
		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		logo.setImageResource(R.drawable.logo5_icon);

		// FONTS
		tvSuccessful.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		btnNewMessage.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));
		tvMent1.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		tvMent2.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));

		// mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
		// mRevealLayout.hide(1000);
	}

	@Override
	public void onResume() {
		super.onResume();
		View menu = (View) findViewById(R.id.send2_menu);

		logoBitmap = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.logo2_icon_01));
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

		sendingIcon.setBackground(new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.sending_icon)));
	}

	@Override
	public void onPause() {
		super.onPause();
		logoBitmap.getBitmap().recycle();
		toolbarBtnBitmap.getBitmap().recycle();
		toolbarBackBitmap.getBitmap().recycle();
		SignInActivity.recycleBgBitmap(sendingIcon);
	}

	@Override
	public void onDestroy() {
		RecycleUtils.recursiveRecycle(getWindow().getDecorView());
		System.gc();
		super.onDestroy();
		window = null;
		tvSuccessful = null;
		tvMent1 = null;
		tvMent2 = null;
		btnNewMessage = null;
		sendingIcon = null;

		MusicImageTagActivity.resultTagKind = null;
		TaggingActivity.cur = 0;
	}

	public void newMessage(View v) {
		goToMain();
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
			goToMain();
			break;

		case R.id.iv_toolbar_btn:
			dlDrawer.openDrawer(Gravity.RIGHT);
			break;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
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
			goToMain();
			break;

		case R.id.ll_menu_message:
			i = new Intent(SendActivity_aft.this, MessagePlayerActivity.class);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_setting:
			i = new Intent(SendActivity_aft.this, SettingActivity.class);
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
			stopService(new Intent(SendActivity_aft.this, ServicePage.class));
			
			finish();
			startActivity(new Intent(SendActivity_aft.this, SignInActivity.class));
			break;
		}
	}

	public void goToMain() {
		Intent intent = new Intent(SendActivity_aft.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		goToMain();
	}

}