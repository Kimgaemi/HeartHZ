package com.heart.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.heart.R;
import com.heart.util.Config;
import com.heart.util.Converter;
import com.heart.util.Recorder;
import com.heart.util.SlidingMenu;
import com.heart.wave.WaveDisplayView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class RecordActivity extends AppCompatActivity {

	// LAYOUT
	Context mRecordActivity;
	private Window window = null;
	private TextView record_tv;
	private TextView time_tv1;
	private TextView time_tv2;
	private TextView time_tv3;
	private TextView time_tv4;
	private TextView time_tv5;
	private ImageView scaleTime;
	private ImageView cutLine;
	private LinearLayout displayLayout;
	private WaveDisplayView displayView;
	private Button btnNext;
	private ToggleButton pr_tgn;
	private ToggleButton ps_tgn;
	private ToggleButton tc_tgn;
	private Animation animation;

	// AUDIORECORD CONFIGURATION
	public AudioRecord audioRecord = null;
	public int recBufSize = 0;
	public static int frequency = 44100;
	public static int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
	public static int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;

	// RECORDER
	protected Recorder recorder;
	protected Converter convert;

	// MODE
	public static boolean isTrim = false;

	// VARIABLE
	private String strFriendId;
	private String strFriendPic;
	private String strFriendName;
	private String strFriendPhone;
	private String strFriendCPic;
	private String strFriendEmail;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private ActionBarDrawerToggle dtToggle;
	private DrawerLayout dlDrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRecordActivity = this;
		setContentView(R.layout.activity_recorder);

		Intent i = getIntent();
		strFriendId = i.getStringExtra(Config.TAG_FRIEND_ID);
		strFriendPic = i.getStringExtra(Config.TAG_FIREND_PIC);
		strFriendName = i.getStringExtra(Config.TAG_FRIEND_NAME);
		strFriendPhone = i.getStringExtra(Config.TAG_FIREND_PHONE);
		strFriendCPic = i.getStringExtra(Config.TAG_CPIC_PATH);
		strFriendEmail = i.getStringExtra(Config.TAG_USER_EMAIL);

		// LAYOUT
		displayLayout = (LinearLayout) findViewById(R.id.displayView);

		cutLine = (ImageView) findViewById(R.id.iv_cutline);
		scaleTime = (ImageView) findViewById(R.id.iv_scale_time);
		animation = AnimationUtils.loadAnimation(RecordActivity.this,
				R.anim.ani);
		record_tv = (TextView) findViewById(R.id.tv_record);
		time_tv1 = (TextView) findViewById(R.id.time1);
		time_tv2 = (TextView) findViewById(R.id.time2);
		time_tv3 = (TextView) findViewById(R.id.time3);
		time_tv4 = (TextView) findViewById(R.id.time4);
		time_tv5 = (TextView) findViewById(R.id.time5);

		btnNext = (Button) findViewById(R.id.record_nextbtn);
		pr_tgn = (ToggleButton) findViewById(R.id.pausebtn);

		pr_tgn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pr_tgn.isChecked()) {
					Log.i("MAINACTIVITY", "PAUSE");
					if (Recorder.isRecording) {
						animation.cancel();
						recorder.audioPause();
					} else {
						Toast.makeText(RecordActivity.this, "녹음을 먼저 실행해 주세요",
								Toast.LENGTH_SHORT).show();
						pr_tgn.setChecked(false);
					}
				} else {
					Log.i("MAINACTIVITY", "RESUME");
					if (Recorder.isRecording) {
						animation.reset();
						scaleTime.startAnimation(animation);
						recorder.audioResume();
					}
				}

			}
		});

		ps_tgn = (ToggleButton) findViewById(R.id.startbtn);
		ps_tgn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ps_tgn.isChecked()) {
					Log.i("MAINACTIVITY", "START RECORDING");
					startRecord();
				} else {
					Log.i("MAINACTIVITY", "STOP RECORDING");
					if (recorder.isPause()) {
						recorder.audioResume();
						stopRecord();
					} else {
						stopRecord();
					}
				}
			}
		});

		tc_tgn = (ToggleButton) findViewById(R.id.trimbtn);
		tc_tgn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tc_tgn.isChecked()) {
					Log.i("MAINACTIVITY", "TRIM MODE");
					isTrim = true;
					// INITIALIZING
					WaveDisplayView.startPoint = 0;
					WaveDisplayView.finishPoint = -1;
				} else {
					// TRIM MODE 해제
					isTrim = false;
					byte[] data = displayView.getAllWaveData();
					if (WaveDisplayView.finishPoint == -1)
						WaveDisplayView.finishPoint = data.length;

					// Short 이기 때문에 짝수 Index만 받음
					if (WaveDisplayView.startPoint % 2 != 0)
						WaveDisplayView.startPoint += 1;
					if (WaveDisplayView.finishPoint % 2 != 0)
						WaveDisplayView.finishPoint += 1;

					byte[] trimData = Arrays.copyOfRange(data,
							WaveDisplayView.startPoint,
							WaveDisplayView.finishPoint);
					Log.i("MAINACTIVITY",
							"CUTTING POINTER"
									+ String.valueOf(WaveDisplayView.startPoint)
									+ " "
									+ String.valueOf(WaveDisplayView.finishPoint));
					final File file = new File(
							convert.getTempFilename(Converter.AUDIO_RECORDER_TEMP_FILE));
					saveSoundPcmFile(file, trimData);
				}
			}
		});

		convert = new Converter(frequency, AudioRecord.getMinBufferSize(
				frequency, channelConfiguration, EncodingBitRate));
		// STATUSBAR COLOR
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window = this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_base));
		}

		imageInit(this);

		// MENU
		menuInit();
		View menu = (View) findViewById(R.id.record_menu);
		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		logo.setImageResource(R.drawable.logo2_icon_01);

		// FONT
		record_tv.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		btnNext.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));
		time_tv1.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		time_tv2.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		time_tv3.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		time_tv4.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));
		time_tv5.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));

	}

	@Override
	public void onResume() {
		super.onResume();
		cutLine.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.cutline2_btn_01)));
		scaleTime.setBackground(new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.time_scale)));
	}

	@Override
	public void onPause() {
		super.onPause();
		SignInActivity.recycleBgBitmap(cutLine);
		SignInActivity.recycleBgBitmap(scaleTime);
		if (Recorder.isRecording)
			stopRecord();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (displayView != null) {
			displayView.clearWaveData();
			displayView.closeWaveData();
		}
		window = null;
		record_tv = null;
		time_tv1 = null;
		time_tv2 = null;
		time_tv3 = null;
		time_tv4 = null;
		time_tv5 = null;
		cutLine = null;
		scaleTime = null;
		displayLayout = null;
		displayView = null;
		btnNext = null;
		pr_tgn = null;
		ps_tgn = null;
		tc_tgn = null;
		audioRecord = null;
		recorder = null;
		System.gc();
	}

	// BUTTON EVENT
	public void mOnClick(View v) {
		int what = v.getId();
		switch (what) {
		case R.id.record_nextbtn:
			// convert.convertPcmToWav();
			Intent intent = new Intent(RecordActivity.this,
					MusicImageTagActivity.class);
			intent.putExtra(Config.TAG_FRIEND_ID, strFriendId);
			intent.putExtra(Config.TAG_FIREND_PIC, strFriendPic);
			intent.putExtra(Config.TAG_FRIEND_NAME, strFriendName);
			intent.putExtra(Config.TAG_FIREND_PHONE, strFriendPhone);
			intent.putExtra(Config.TAG_CPIC_PATH, strFriendCPic);
			intent.putExtra(Config.TAG_USER_EMAIL, strFriendEmail);
			startActivity(intent);
		}
	}

	// START AUDIO RECORD
	private void startRecord() {
		if (displayView != null)
			displayLayout.removeAllViews();
		scaleTime.startAnimation(animation);
		displayView = new WaveDisplayView(getBaseContext());
		displayLayout.addView(displayView);
		Log.i("MAINACTIVITY", "START_RECORDING");
		createAudioRecord();
		recorder = new Recorder(mRecordActivity, audioRecord, displayView,
				recBufSize);
		recorder.start();
	}

	// STOP AUDIO RECORD
	private void stopRecord() {
		Log.i("MAINACTIVITY", "STOP_RECORDING");
		animation.cancel();
		if (pr_tgn.isChecked())
			pr_tgn.setChecked(false);
		recorder.finish();
		final File file = new File(
				convert.getTempFilename(Converter.AUDIO_RECORDER_TEMP_FILE));
		byte[] data = displayView.getAllWaveData();
		saveSoundPcmFile(file, data);
	}

	private boolean saveSoundPcmFile(File savefile, byte[] data) {
		if (data.length == 0) {
			Log.w("SAVEFILE", "SAVE DATA IS NOT FOUND.");
			return false;
		}
		try {
			savefile.createNewFile();
			FileOutputStream targetStream = new FileOutputStream(savefile);
			try {
				targetStream.write(data);
			} finally {
				if (targetStream != null) {
					targetStream.close();
				}
			}
			return true;
		} catch (IOException ex) {
			Log.w("SAVEFILE", "FAIL TO SAVE SOUND FILE.", ex);
			return false;
		}
	}

	// AUDIO RECORDING SETTING
	public void createAudioRecord() {
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, EncodingBitRate);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, EncodingBitRate, recBufSize);
		Log.i("MAINACTIVITY", String.valueOf(recBufSize));
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

	public void menuClick(View v) {
		Intent i;

		switch (v.getId()) {
		case R.id.ll_menu_home:
			i = new Intent(RecordActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_message:
			dlDrawer.closeDrawers();
			i = new Intent(RecordActivity.this, MessagePlayerActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_setting:
			dlDrawer.closeDrawers();
			i = new Intent(RecordActivity.this, SettingActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_logout:
			close();
			break;
		}
	}

	private void close() {
		finish();
		Intent intent = new Intent(RecordActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("KILL_ACT", true);
		startActivity(intent);
	}

}