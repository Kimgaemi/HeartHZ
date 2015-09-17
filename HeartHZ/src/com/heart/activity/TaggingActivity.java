package com.heart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heart.R;
import com.heart.tag.TagPagerAdapter;
import com.heart.util.Config;
import com.heart.util.CustomViewPager;
import com.heart.util.SlidingMenu;

public class TaggingActivity extends AppCompatActivity {

	public static int PAGES = 5;
	public final static int FIRST_PAGE = 0;

	public final static float BIG_SCALE = 1.0f;
	public final static float SMALL_SCALE = 0.8f;
	public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

	public Context mContext;
	public TagPagerAdapter adapter;
	public CustomViewPager pager;

	// LAYOUT
	private Window window = null;
	private TextView tvActionBar;
	private TextView tvKind;
	private ImageView swipeCircle;
	private ImageView left;
	private ImageView right;
	private Button btnFooterBar;

	// VARIABLE
	private String strFriendId;
	private String strFriendPic;
	private String strFriendName;
	private String strFriendPhone;
	private String strFriendCPic;
	private String strFriendEmail;
	private String musicTag;

	private String strDate;
	private int[] arrFileTag;

	// TAG
	private String[] tagKind = { "Emotion", "Time", "Weather", "Day" };
	public static int cur = 0;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		mContext = TaggingActivity.this;

		tvActionBar = (TextView) findViewById(R.id.tv_simulationtag);
		swipeCircle = (ImageView) findViewById(R.id.tag_swipe_circle);
		left = (ImageView) findViewById(R.id.tag_left);
		right = (ImageView) findViewById(R.id.tag_right);
		tvKind = (TextView) findViewById(R.id.tv_tag_kind);

		pager = (CustomViewPager) findViewById(R.id.emotionviewpager);
		adapter = new TagPagerAdapter(this, this.getSupportFragmentManager(),
				swipeCircle, pager);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(adapter);
		// Set current item to the middle page so we can fling to both
		// directions left and right
		pager.setCurrentItem(FIRST_PAGE);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		pager.setOffscreenPageLimit(4);

		// Set margin for pages as a negative number, so a part of next and
		// previous pages will be showed
		pager.setPageMargin(-500);
		// Color Animation

		Intent i = getIntent();
		strFriendId = i.getStringExtra(Config.TAG_FRIEND_ID);
		strFriendPic = i.getStringExtra(Config.TAG_FIREND_PIC);
		strFriendName = i.getStringExtra(Config.TAG_FRIEND_NAME);
		strFriendPhone = i.getStringExtra(Config.TAG_FIREND_PHONE);
		strFriendCPic = i.getStringExtra(Config.TAG_CPIC_PATH);
		strFriendEmail = i.getStringExtra(Config.TAG_USER_EMAIL);
		musicTag = i.getStringExtra(Config.TAG_MUSIC_PATH);

		tvKind.setText(tagKind[cur]);
		swipeCircle.setImageResource(R.drawable.swipe1_icon_01);
		btnFooterBar = (Button) findViewById(R.id.tag_next_page);

		left.setOnClickListener(kindListen);
		right.setOnClickListener(kindListen);

		// STATUSBAR COLOR
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window = this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_base));
		}

		// FONTS
		tvActionBar.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		btnFooterBar.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));
		tvKind.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));

		// MENU
		menuInit();
		View menu = (View) findViewById(R.id.tag_menu);
		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		logo.setImageResource(R.drawable.logo4_icon_01);
	}

	// BUTTON EVENT
	public void mOnClick(View v) {
		int what = v.getId();
		switch (what) {
		case R.id.tag_next_page:
			Intent intent = new Intent(TaggingActivity.this,
					SendActivity_pre.class);

			intent.putExtra(Config.TAG_DATE, strDate);
			intent.putExtra(Config.TAG_CONTEXT_ARR, arrFileTag);

			intent.putExtra(Config.TAG_FRIEND_ID, strFriendId);
			intent.putExtra(Config.TAG_FIREND_PIC, strFriendPic);
			intent.putExtra(Config.TAG_FRIEND_NAME, strFriendName);
			intent.putExtra(Config.TAG_FIREND_PHONE, strFriendPhone);
			intent.putExtra(Config.TAG_CPIC_PATH, strFriendCPic);
			intent.putExtra(Config.TAG_USER_EMAIL, strFriendEmail);
			intent.putExtra(Config.TAG_MUSIC_PATH, musicTag);
			startActivity(intent);
			break;
		}
	}

	private OnClickListener kindListen = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tag_left:
				if (cur > 0)
					cur--;
				tvKind.setText(tagKind[cur]);
				adapter.tagContextSwithing(cur);
				break;
			case R.id.tag_right:
				if (cur < 3)
					cur++;
				tvKind.setText(tagKind[cur]);
				adapter.tagContextSwithing(cur);
				break;
			}

		}
	};

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
	public void onPause() {
		super.onPause();
		cur = 0;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext = null;
		adapter = null;
		pager = null;
		window = null;
		tvActionBar = null;
		tvKind = null;
		swipeCircle = null;
		left = null;
		right = null;
		btnFooterBar = null;
		System.gc();
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
			i = new Intent(TaggingActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_message:
			dlDrawer.closeDrawers();
			i = new Intent(TaggingActivity.this, MessagePlayerActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_setting:
			dlDrawer.closeDrawers();
			i = new Intent(TaggingActivity.this, SettingActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_logout:
			close();
			break;
		}
	}

	private void close() {
		finish();
		Intent intent = new Intent(TaggingActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("KILL_ACT", true);
		startActivity(intent);
	}

}