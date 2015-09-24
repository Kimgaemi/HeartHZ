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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
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
	private AbsoluteLayout trimBox1;
	private AbsoluteLayout trimBox2;
	private TextView record_tv;
	private TextView time_tv1;
	private TextView time_tv2;
	private TextView time_tv3;
	private TextView time_tv4;
	private TextView time_tv5;
	private ImageView scaleTime;
	private ImageView cutLine0;
	private ImageView cutLine1;
	private ImageView cutLine2;
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
	public static boolean isTrim;
	private boolean startTrim;
	private boolean finishTrim;

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

	// TRIM
	private int startPoint;
	private int finishPoint;
	private View finishDim;
	private View fristDim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRecordActivity = this;
		setContentView(R.layout.activity_recorder);
		isTrim = false;
		Intent i = getIntent();
		strFriendId = i.getStringExtra(Config.TAG_FRIEND_ID);
		strFriendPic = i.getStringExtra(Config.TAG_FIREND_PIC);
		strFriendName = i.getStringExtra(Config.TAG_FRIEND_NAME);
		strFriendPhone = i.getStringExtra(Config.TAG_FIREND_PHONE);
		strFriendCPic = i.getStringExtra(Config.TAG_CPIC_PATH);
		strFriendEmail = i.getStringExtra(Config.TAG_USER_EMAIL);

		// LAYOUT
		displayLayout = (LinearLayout) findViewById(R.id.displayView);
		trimBox1 = (AbsoluteLayout) findViewById(R.id.trimContainer1);
		trimBox2 = (AbsoluteLayout) findViewById(R.id.trimContainer2);
		fristDim = (View) findViewById(R.id.dimview1);
		finishDim = (View) findViewById(R.id.dimview2);

		cutLine0 = (ImageView) findViewById(R.id.iv_cutline);
		cutLine1 = (ImageView) findViewById(R.id.iv_cutline1);
		cutLine2 = (ImageView) findViewById(R.id.iv_cutline2);

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
					// Trim 중이라면 Trim 닫고 시작하기
					if (isTrim) {
						Toast.makeText(mRecordActivity, "Trim 작업을 먼저 끝내주세요",
								Toast.LENGTH_SHORT).show();
						pr_tgn.setChecked(false);
					} else {
						if (Recorder.isRecording) {
							animation.cancel();
							recorder.audioPause();
						} else {
							Toast.makeText(RecordActivity.this,
									"녹음을 먼저 실행해 주세요", Toast.LENGTH_SHORT)
									.show();
							pr_tgn.setChecked(false);
						}
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
				// Trim 중이라면 Trim 닫고 시작하기
				if (isTrim) {
					Toast.makeText(mRecordActivity, "Trim 작업을 먼저 끝내주세요",
							Toast.LENGTH_SHORT).show();
					ps_tgn.setChecked(false);
				} else {
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
			}
		});

		tc_tgn = (ToggleButton) findViewById(R.id.trimbtn);
		tc_tgn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tc_tgn.isChecked()) {
					// RECORD 중이면 TOAST
					if (Recorder.isRecording) {
						Toast.makeText(mRecordActivity, "녹음을 먼저 중지해주세요",
								Toast.LENGTH_SHORT).show();
						tc_tgn.setChecked(false);
					} else {
						Log.i("MAINACTIVITY", "TRIM MODE");
						if (displayView != null) {
							if (displayView.isData()) {
								isTrim = true;
								displayView.fireInvalidate();

								cutLine0.setVisibility(View.INVISIBLE);
								cutLine1.setVisibility(View.VISIBLE);
								LayoutParams lp = new LayoutParams(69, 55,
										(int) 685, (int) 238);
								cutLine1.setLayoutParams(lp);
								cutLine2.setVisibility(View.VISIBLE);
								lp = new LayoutParams(69, 55, (int) 686,
										(int) 0);
								cutLine2.setLayoutParams(lp);

								// DIM
								fristDim.setVisibility(View.VISIBLE);
								finishDim.setVisibility(View.VISIBLE);

								// INITIALIZING
								startPoint = 0;
								finishPoint = -1;

							} else {
								Toast.makeText(mRecordActivity,
										"녹음한 데이터가 없습니다.", Toast.LENGTH_SHORT)
										.show();
								tc_tgn.setChecked(false);
							}
						} else {
							Toast.makeText(mRecordActivity, "녹음한 데이터가 없습니다.",
									Toast.LENGTH_SHORT).show();
							tc_tgn.setChecked(false);
						}
					}
				} else {
					// TRIM MODE 해제
					isTrim = false;

					cutLine0.setVisibility(View.VISIBLE);
					cutLine1.setVisibility(View.INVISIBLE);
					cutLine2.setVisibility(View.INVISIBLE);
					fristDim.setVisibility(View.INVISIBLE);
					finishDim.setVisibility(View.INVISIBLE);

					// 좌표 가져오기
					Log.i("TRIM",
							"CUTTING POINTER : "
									+ String.valueOf(cutLine1.getX()) + " "
									+ String.valueOf(cutLine2.getX()));

					float BoxSize = (float) displayView.getBoxSize();
					int MovingCur = displayView.getMovingCur();

					float sRate = cutLine1.getX() / 1440.0f;
					float fRate = cutLine2.getX() / 1440.0f;

					startPoint = (int) (BoxSize * 3584.0f * (sRate))
							+ MovingCur;
					finishPoint = (int) (BoxSize * 3584.0f * (fRate))
							+ MovingCur;

					Log.i("TRIM",
							"CUTTING POINTER : " + String.valueOf(BoxSize)
									+ " " + String.valueOf(MovingCur));

					Log.i("TRIM", "CUTTING POINTER : " + String.valueOf(sRate)
							+ " " + String.valueOf(fRate));
					Log.i("TRIM",
							"CUTTING POINTER : " + String.valueOf(startPoint)
									+ " " + String.valueOf(finishPoint));

					byte[] data = displayView.getAllWaveData();

					// Short 이기 때문에 짝수 Index만 받음
					if (startPoint % 2 != 0)
						startPoint += 1;
					if (finishPoint % 2 != 0)
						finishPoint += 1;

					byte[] trimData = Arrays.copyOfRange(data, startPoint * 2,
							finishPoint * 2);

					final File file = new File(
							convert.getTempFilename(Converter.AUDIO_RECORDER_TEMP_FILE));
					saveSoundPcmFile(file, trimData);
					convert.convertPcmToWav();
					// 트림 끝난뒤의 View는 어떻게?
					displayView.clearWaveData();
					displayView.fireInvalidate();
				}
			}
		});

		// Trim Mode
		startTrim = false;
		finishTrim = false;

		cutLine1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("TRIM_IMAGE1_CLICK", "START");
				startTrim = true;
				return false;
			}
		});

		cutLine2.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("TRIM_IMAGE2_CLICK", "START");
				finishTrim = true;
				return false;
			}
		});

		trimBox1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// Trim 중 일때만 작
				if (isTrim) {
					if (startTrim) // any event from down and move
					{
						if (cutLine2.getX() > event.getX()) {
							LayoutParams lp = new LayoutParams(69, 55,
									(int) event.getX(), (int) 238);
							cutLine1.setLayoutParams(lp);
							lp = new LayoutParams((int) event.getX() + 34, 998,
									0, (int) 0);
							fristDim.setLayoutParams(lp);
						}

					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						startTrim = false;
						// cutLine1.setBackgroundColor(Color.TRANSPARENT);
					}

				}
				return true;
			}
		});

		trimBox2.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// Trim 중 일때만 작
				if (isTrim) {
					if (finishTrim) // any event from down and move
					{
						// start 브분의 위치를 못 넘도록 만든다.
						if (cutLine1.getX() < event.getX()
								&& event.getX() < 1371) {
							// y를 고정하면 y축은 고정됩니다.
							LayoutParams lp = new LayoutParams(69, 55,
									(int) event.getX(), (int) 0);
							cutLine2.setLayoutParams(lp);
							lp = new LayoutParams(1405 - (int) event.getX(),
									998, (int) event.getX() + 35, (int) 0);
							finishDim.setLayoutParams(lp);
						}
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						finishTrim = false;
						// cutLine1.setBackgroundColor(Color.TRANSPARENT);
					}
				}
				return true;
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
		cutLine0.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.cutline2_btn_01)));
		cutLine1.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.cut_start)));
		cutLine2.setBackground(new BitmapDrawable(getResources(), BitmapFactory
				.decodeResource(getResources(), R.drawable.cut_finish)));
		scaleTime.setBackground(new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.time_scale)));
	}

	@Override
	public void onPause() {
		super.onPause();
		SignInActivity.recycleBgBitmap(cutLine0);
		SignInActivity.recycleBgBitmap(cutLine1);
		SignInActivity.recycleBgBitmap(cutLine2);
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
		cutLine0 = null;
		cutLine1 = null;
		cutLine2 = null;
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