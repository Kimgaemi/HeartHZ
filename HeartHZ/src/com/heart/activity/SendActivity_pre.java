package com.heart.activity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioRecord;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heart.R;
import com.heart.util.Config;
import com.heart.util.Converter;
import com.heart.util.SlidingMenu;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class SendActivity_pre extends AppCompatActivity {

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private static final String TAG_CONTEXT = "contenxt";

	// VARIABLE
	private int iUserId;
	private String strTag;
	private String strTitle;

	private int[] arrFileTag;
	private String strDate;
	private String strFriendId;
	private String strFriendPic;
	private String strFriendName;
	private String strFriendPhone;
	private String musicPath;

	protected Converter convert;

	// VIEW
	private Window window = null;
	private TextView tvSend = null;
	private Button btnSend = null;
	private EditText editTitle = null;
	private ImageView ivFriendPic;
	private TextView tvFirendName;
	private TextView tvFirendPhone;
	private ImageView ivTagSelected1;
	private ImageView ivTagSelected2;
	private ImageView ivTagSelected3;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send1);

		iUserId = SignInActivity.iUserId;

		Intent i = getIntent();
		// strDate = i.getStringExtra(Config.TAG_DATE);
		// arrFileTag = i.getIntArrayExtra(Config.TAG_CONTEXT_ARR);

		arrFileTag = new int[] { 0, 0, 0 };
		arrFileTag[0] = MusicImageTagActivity.resultTagKind.getEmotion() + 1;
		arrFileTag[1] = MusicImageTagActivity.resultTagKind.getTime() + 1;
		arrFileTag[2] = MusicImageTagActivity.resultTagKind.getWeather() + 1;

		if (MusicImageTagActivity.resultTagKind.getCDate() >= 0) {

			// strDate = MusicImageTagActivity.resultTagKind.getRealDate();
			strDate = "0000-00-00";
		} else {
			strDate = "0000-00-00";
		}
		Log.d("atrDate", arrFileTag[0] + arrFileTag[1] + arrFileTag[2] + "?");
		Log.d("atrDate", strDate + "?");

		strFriendId = i.getStringExtra(Config.TAG_FRIEND_ID);
		strFriendPic = i.getStringExtra(Config.TAG_FIREND_PIC);
		strFriendName = i.getStringExtra(Config.TAG_FRIEND_NAME);
		strFriendPhone = i.getStringExtra(Config.TAG_FIREND_PHONE);
		musicPath = i.getStringExtra(Config.TAG_MUSIC_PATH);

		Log.i("MUSICPATH", musicPath);

		btnSend = (Button) findViewById(R.id.btn_send1);
		tvSend = (TextView) findViewById(R.id.tv_sending_message);
		editTitle = (EditText) findViewById(R.id.edit_message_title);

		ivFriendPic = (ImageView) findViewById(R.id.iv_friend);
		tvFirendName = (TextView) findViewById(R.id.tv_friend_name);
		tvFirendPhone = (TextView) findViewById(R.id.tv_friend_phone);

		tvFirendName.setText(strFriendName);
		tvFirendPhone.setText(strFriendPhone);

		ivTagSelected1 = (ImageView) findViewById(R.id.iv_conf_im1);
		ivTagSelected2 = (ImageView) findViewById(R.id.iv_conf_im2);
		ivTagSelected3 = (ImageView) findViewById(R.id.iv_conf_im3);

		convert = new Converter(RecordActivity.frequency,
				AudioRecord.getMinBufferSize(RecordActivity.frequency,
						RecordActivity.channelConfiguration,
						RecordActivity.EncodingBitRate));

		// STATUSBAR COLOR
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window = this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_base));
		}

		// IMAGE LOADER
		ImageLoader imgloder = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory().displayer(new RoundedBitmapDisplayer(300))
				.cacheOnDisc().resetViewBeforeLoading()
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile).build();

		imgloder.displayImage(strFriendPic, ivFriendPic, options);

		// MENU
		menuInit();
		View menu = (View) findViewById(R.id.send1_menu);
		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		logo.setImageResource(R.drawable.logo5_icon);

		// FONTS
		tvSend.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		btnSend.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));
		editTitle.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AppleSDGothicNeo-Regular.otf"));

		Log.d("RESULT + Emotion", String
				.valueOf(MusicImageTagActivity.resultTagKind.getEmotion()));
		Log.d("RESULT + Time",
				String.valueOf(MusicImageTagActivity.resultTagKind.getTime()));
		Log.d("RESULT + Weather", String
				.valueOf(MusicImageTagActivity.resultTagKind.getWeather()));
		if (MusicImageTagActivity.resultTagKind.getCDate() == 1)
			Log.d("RESULT + Date", String
					.valueOf(MusicImageTagActivity.resultTagKind.getDate()));
		if (MusicImageTagActivity.resultTagKind.getMusicCur() != -1)
			Log.d("RESULT + Date", String
					.valueOf(MusicImageTagActivity.resultTagKind.getMusicCur()));
		confTag();
		converting();
	}

	public void converting() {
		if (MusicImageTagActivity.resultTagKind.getMusicCur() >= 0) {
			convert.convertWavToPcm(musicPath);
			convert.mixingWav();
		}else{
			convert.convertPcmToWav();
		}
	}

	public void confTag() {
		int num = MusicImageTagActivity.resultTagKind.getTagCount();
		if (num >= 3)
			num = 3;
		switch (num) {
		case 0:
			ivTagSelected1.setVisibility(View.GONE);
			ivTagSelected2.setVisibility(View.GONE);
			ivTagSelected3.setVisibility(View.GONE);
			break;
		case 1:
			ivTagSelected1.setVisibility(View.GONE);
			ivTagSelected3.setVisibility(View.GONE);
			// 음악태그 우선순위
			if (MusicImageTagActivity.resultTagKind.getMusicCur() >= 0) {
				ivTagSelected2.setImageDrawable(getDrawableMusicTag());
				break;
			}
			if (MusicImageTagActivity.resultTagKind.getEmotion() >= 0) {
				ivTagSelected2.setImageDrawable(getDrawableEmotionTag());
				break;
			}
			if (MusicImageTagActivity.resultTagKind.getTime() >= 0) {
				ivTagSelected2.setImageDrawable(getDrawableTimeTag());
				break;
			}
			if (MusicImageTagActivity.resultTagKind.getWeather() >= 0) {
				ivTagSelected2.setImageDrawable(getDrawableWeatherTag());
				break;
			}
			if (MusicImageTagActivity.resultTagKind.getCDate() >= 0) {
				ivTagSelected2.setImageDrawable(getDrawableDateTag());
				break;
			}
			break;
		case 2:
			boolean flag1 = true;
			boolean flag2 = true;
			ivTagSelected2.setVisibility(View.GONE);
			if (MusicImageTagActivity.resultTagKind.getMusicCur() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableMusicTag());
					flag1 = false;
				} else {
					ivTagSelected1.setImageDrawable(getDrawableMusicTag());
					flag2 = false;
				}
				if (flag2 == false && flag1 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getEmotion() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableEmotionTag());
					flag1 = false;
				} else {
					ivTagSelected1.setImageDrawable(getDrawableEmotionTag());
					flag2 = false;
				}
				if (flag2 == false && flag1 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getTime() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableTimeTag());
					flag1 = false;
				} else {
					ivTagSelected1.setImageDrawable(getDrawableTimeTag());
					flag2 = false;
				}
				if (flag2 == false && flag1 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getWeather() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableWeatherTag());
					flag1 = false;
				} else {
					ivTagSelected1.setImageDrawable(getDrawableWeatherTag());
					flag2 = false;
				}
				if (flag2 == false && flag1 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getCDate() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableDateTag());
					flag1 = false;
				} else {
					ivTagSelected1.setImageDrawable(getDrawableDateTag());
					flag2 = false;
				}
				if (flag2 == false && flag1 == false)
					break;
			}
			break;
		case 3:
			flag1 = true;
			flag2 = true;
			boolean flag3 = true;
			if (MusicImageTagActivity.resultTagKind.getMusicCur() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableMusicTag());
					flag1 = false;
				} else {
					if (flag2) {
						ivTagSelected2.setImageDrawable(getDrawableMusicTag());
						flag2 = false;
					} else {
						ivTagSelected1.setImageDrawable(getDrawableMusicTag());
						flag3 = false;
					}
				}
				if (flag2 == false && flag1 == false && flag3 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getEmotion() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableEmotionTag());
					flag1 = false;
				} else {
					if (flag2) {
						ivTagSelected2
								.setImageDrawable(getDrawableEmotionTag());
						flag2 = false;
					} else {
						ivTagSelected1
								.setImageDrawable(getDrawableEmotionTag());
						flag3 = false;
					}
				}
				if (flag2 == false && flag1 == false && flag3 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getTime() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableTimeTag());
					flag1 = false;
				} else {
					if (flag2) {
						ivTagSelected2.setImageDrawable(getDrawableTimeTag());
						flag2 = false;
					} else {
						ivTagSelected1.setImageDrawable(getDrawableTimeTag());
						flag3 = false;
					}
				}
				if (flag2 == false && flag1 == false && flag3 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getWeather() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableWeatherTag());
					flag1 = false;
				} else {
					if (flag2) {
						ivTagSelected2
								.setImageDrawable(getDrawableWeatherTag());
						flag2 = false;
					} else {
						ivTagSelected1
								.setImageDrawable(getDrawableWeatherTag());
						flag3 = false;
					}
				}
				if (flag2 == false && flag1 == false && flag3 == false)
					break;
			}
			if (MusicImageTagActivity.resultTagKind.getCDate() >= 0) {
				if (flag1) {
					ivTagSelected3.setImageDrawable(getDrawableDateTag());
					flag1 = false;
				} else {
					if (flag2) {
						ivTagSelected2.setImageDrawable(getDrawableDateTag());
						flag2 = false;
					} else {
						ivTagSelected1.setImageDrawable(getDrawableDateTag());
						flag3 = false;
					}
				}
				if (flag2 == false && flag1 == false && flag3 == false)
					break;
			}
			break;
		}
	}

	public Drawable getDrawableMusicTag() {
		Drawable d = null;
		int pos = MusicImageTagActivity.resultTagKind.getMusicCur();
		switch (pos) {
		case 0:
			d = getResources().getDrawable(R.drawable.tagmusic1_img);
			break;
		case 1:
			d = getResources().getDrawable(R.drawable.tagmusic2_img);
			break;
		case 2:
			d = getResources().getDrawable(R.drawable.tagmusic3_img);
			break;
		case 3:
			d = getResources().getDrawable(R.drawable.tagmusic4_img);
			break;
		case 4:
			d = getResources().getDrawable(R.drawable.tagmusic5_img);
			break;
		case 5:
			d = getResources().getDrawable(R.drawable.tagmusic6_img);
			break;
		case 6:
			d = getResources().getDrawable(R.drawable.tagmusic7_img);
			break;
		case 7:
			d = getResources().getDrawable(R.drawable.tagmusic8_img);
			break;
		}
		return d;
	}

	public Drawable getDrawableEmotionTag() {
		Drawable d = null;
		int pos = MusicImageTagActivity.resultTagKind.getEmotion();
		switch (pos) {
		case 0:
			d = getResources().getDrawable(R.drawable.emotion1_icon);
			break;
		case 1:
			d = getResources().getDrawable(R.drawable.emotion2_icon);
			break;
		case 2:
			d = getResources().getDrawable(R.drawable.emotion3_icon);
			break;
		case 3:
			d = getResources().getDrawable(R.drawable.emotion4_icon);
			break;
		}
		return d;
	}

	public Drawable getDrawableTimeTag() {
		Drawable d = null;
		int pos = MusicImageTagActivity.resultTagKind.getTime();
		switch (pos) {
		case 0:
			d = getResources().getDrawable(R.drawable.time1_icon);
			break;
		case 1:
			d = getResources().getDrawable(R.drawable.time2_icon);
			break;
		case 2:
			d = getResources().getDrawable(R.drawable.time3_icon);
			break;
		case 3:
			d = getResources().getDrawable(R.drawable.time4_icon);
			break;
		case 4:
			d = getResources().getDrawable(R.drawable.time5_icon);
			break;
		}
		return d;
	}

	public Drawable getDrawableWeatherTag() {
		Drawable d = null;
		int pos = MusicImageTagActivity.resultTagKind.getWeather();
		switch (pos) {
		case 0:
			d = getResources().getDrawable(R.drawable.weather1_icon);
			break;
		case 1:
			d = getResources().getDrawable(R.drawable.weather2_icon);
			break;
		case 2:
			d = getResources().getDrawable(R.drawable.weather3_icon);
			break;
		case 3:
			d = getResources().getDrawable(R.drawable.weather4_icon);
			break;
		case 4:
			d = getResources().getDrawable(R.drawable.weather5_icon);
			break;
		}
		return d;
	}

	public Drawable getDrawableDateTag() {
		Drawable d = getResources().getDrawable(R.drawable.tagspecificday_icon);
		return d;
	}

	public void send1Button(View v) {
		new UploadFileToServer().execute();
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		convert = null;
		window = null;
		tvSend = null;
		editTitle = null;
		btnSend = null;
		ivFriendPic = null;
		tvFirendPhone = null;
		tvFirendName = null;
		ivTagSelected1 = null;
		ivTagSelected2 = null;
		ivTagSelected3 = null;
		System.gc();
	}

	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... params) {
			strTitle = editTitle.getText().toString();
			if (strTitle == null || strTitle.equals(""))
				strTitle = "Message Title";
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String strResponse = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Config.URL_FILE_UPLOAD);

			// SETTING HEADER
			httppost.setHeader("Accept-Charset", "UTF-8");
			httppost.setHeader("Connection", "Keep-Alive");
			httppost.setHeader("ENCTYPE", "multipart/form-data");

			try {
				MultipartEntity entity = new MultipartEntity();

				File sourceFile = new File(
						convert.getFilename(Converter.AUDIO_RECORDER_FILE));

				// ADDING FILE DATA TO HTTP BODY
				entity.addPart("sound", new FileBody(sourceFile));

				String strFileNo = "heart" + getCurrentTime()
						+ Integer.toString(iUserId) + strFriendId;

				// EXTRA PARAMETERS IF YOU WANT TO PASS TO SERVER
				entity.addPart("From",
						new StringBody(Integer.toString(iUserId)));
				entity.addPart("To", new StringBody(strFriendId));
				entity.addPart("File_No", new StringBody(strFileNo));
				entity.addPart("Title",
						new StringBody(strTitle, Charset.forName("UTF-8")));
				entity.addPart("TAG_Emotion",
						new StringBody(Integer.toString(arrFileTag[0])));
				entity.addPart("TAG_Weather",
						new StringBody(Integer.toString(arrFileTag[1])));
				entity.addPart("TAG_Time",
						new StringBody(Integer.toString(arrFileTag[2])));
				entity.addPart("TAG_Date", new StringBody(strDate));

				httppost.setEntity(entity);

				// MAKING SERVER CALL
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// SERVER RESPONSE
					strResponse = EntityUtils.toString(r_entity);

					JSONObject json = new JSONObject(strResponse);
					int success = json.getInt(Config.TAG_SUCCESS);

					if (success == 0) {
						String title = json.getString("File_name");
						Intent i = new Intent(SendActivity_pre.this,
								SendActivity_aft.class);
						startActivity(i);
					}

				} else {
					strResponse = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				strResponse = e.toString();
			} catch (IOException e) {
				strResponse = e.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			convert.deleteFile("/test.wav");
			return strResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e(CURRENT_ACTIVITY, "RESPONSE FROM SERVER: " + result);

			// showAlert(result);
		}
	}

	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("RESPONSE FROM SERVERS")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static String getCurrentTime() {
		String time = "";
		Calendar cal = Calendar.getInstance();

		time = String.format("%02d%02d%02d%02d%02d", cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		return time + "@";
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
			i = new Intent(SendActivity_pre.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_message:
			dlDrawer.closeDrawers();
			i = new Intent(SendActivity_pre.this, MessagePlayerActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_setting:
			dlDrawer.closeDrawers();
			i = new Intent(SendActivity_pre.this, SettingActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_logout:
			close();
			break;
		}
	}

	private void close() {
		finish();
		Intent intent = new Intent(SendActivity_pre.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("KILL_ACT", true);
		startActivity(intent);
	}
}
