package com.heart.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.heart.R;
import com.heart.activity.PopupActivity.ChangeFileFlag;
import com.heart.friend.Friend;
import com.heart.friend.FriendPagerAdapter;
import com.heart.service.RealService;
import com.heart.service.ServicePage;
import com.heart.util.AnimeUtils;
import com.heart.util.Config;
import com.heart.util.JSONParser;
import com.heart.util.RecycleUtils;
import com.heart.util.SharedPreferenceUtil;
import com.heart.util.SlidingMenu;
import com.heart.util.TagKind;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MusicImageTagActivity extends AppCompatActivity {

	// MusicPlayer
	private int musicNum = 8;
	private MediaPlayer mp[] = new MediaPlayer[musicNum];

	protected ListView listViewLeft;
	protected ListView listViewRight;
	ArrayList<MusicAlbumArt> musicLeftImageResource;
	ArrayList<MusicAlbumArt> musicRightImageResource;
	private LAlbumArtAdapter adapterLeft;
	private RAlbumArtAdapter adapterRight;
	String title;

	// View
	private Window window = null;
	private TextView musictag;
	private Button btnNext;
	private ToggleButton tagToggle;
	protected BitmapDrawable dim_drawable = null;
	protected BitmapDrawable add_drawable = null;
	private BitmapDrawable musicImageOn;
	private BitmapDrawable musicImageOff;

	// VARIABLE
	private String strFriendId;
	private String strFriendPic;
	private String strFriendName;
	private String strFriendPhone;
	private String path;
	private boolean isDownloadFinish = false;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;
	private BitmapDrawable logoBitmap;
	private BitmapDrawable toolbarBtnBitmap;
	private BitmapDrawable toolbarBackBitmap;

	public static TagKind resultTagKind;

	// 추가부분
	private static final int REQUEST_CODE = 6384;

	// TAG
	private final String CURRENT_ACTIVITY = getClass().getSimpleName().trim();
	private final String FOLDER_NAME = "/HeartHZ/MusicTag/";

	// VARIABLE
	private String strFileUrl = "http://210.125.96.96/Heart_php/musics/";

	// JSON
	private JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musicimagetag);

		musictag = (TextView) findViewById(R.id.tv_music_tag);
		btnNext = (Button) findViewById(R.id.musictag2_nextbtn);
		tagToggle = (ToggleButton) findViewById(R.id.music_toggle);

		resultTagKind = new TagKind();

		Intent i = getIntent();
		strFriendId = i.getStringExtra(Config.TAG_FRIEND_ID);
		strFriendPic = i.getStringExtra(Config.TAG_FIREND_PIC);
		strFriendName = i.getStringExtra(Config.TAG_FRIEND_NAME);
		strFriendPhone = i.getStringExtra(Config.TAG_FIREND_PHONE);

		tagToggle.setChecked(true);
		tagToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!tagToggle.isChecked()) {
					tagToggle.setBackground(musicImageOff);
					for (int i = 0; i < musicNum; i++)
						if (mp[i] != null)
							mp[i].stop();
					Intent intent = new Intent(MusicImageTagActivity.this,
							MusicTagActivity.class);
					intent.putExtra(Config.TAG_FRIEND_ID, strFriendId);
					intent.putExtra(Config.TAG_FIREND_PIC, strFriendPic);
					intent.putExtra(Config.TAG_FRIEND_NAME, strFriendName);
					intent.putExtra(Config.TAG_FIREND_PHONE, strFriendPhone);
					MusicImageTagActivity.this.startActivity(intent);
					finish();
				} else {
					tagToggle.setBackground(musicImageOn);
				}
			}
		});

		// STATUSBAR COLOR
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window = this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.status_bar_base));
		}

		// font
		musictag.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		btnNext.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));

		path = Environment.getExternalStorageDirectory().getPath()
				+ FOLDER_NAME;

		// MENU
		menuInit();
	}

	private class MusicAlbumArt {
		String title;
		BitmapDrawable d;
		int pos;
		boolean focus;
		boolean playing;

		MusicAlbumArt(String title, BitmapDrawable d, int pos, boolean focus) {
			this.title = title;
			this.d = d;
			this.pos = pos;
			this.focus = focus;
			playing = false;
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		View menu = (View) findViewById(R.id.music_image_tag_menu);
		logoBitmap = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.logo3_icon_01));
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

		musicImageOn = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.album_on_btn));
		musicImageOff = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.album_off_btn));
		tagToggle.setBackground(musicImageOn);
		dim_drawable = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.musictag_dim_play));
		add_drawable = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.add2_btn));

		musicLeftImageResource = new ArrayList<MusicAlbumArt>();
		musicRightImageResource = new ArrayList<MusicAlbumArt>();
		listViewLeft = (ListView) findViewById(R.id.left_image_musictag);
		listViewRight = (ListView) findViewById(R.id.right_image_musictag);
		BitmapDrawable d = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.album1_image));
		MusicAlbumArt maa = new MusicAlbumArt("Born to die", d, 0, false);
		musicLeftImageResource.add(maa);
		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album2_image));
		maa = new MusicAlbumArt("Dream", d, 1, false);
		musicLeftImageResource.add(maa);
		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album3_image));
		maa = new MusicAlbumArt("Hooka", d, 2, false);
		musicLeftImageResource.add(maa);
		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album4_image));
		maa = new MusicAlbumArt("Healter Svelter", d, 3, false);
		musicLeftImageResource.add(maa);

		listViewLeft.setOnItemClickListener(onLeftClickListner);
		adapterLeft = new LAlbumArtAdapter(this, R.layout.list_musicimage,
				musicLeftImageResource);
		listViewLeft.setAdapter(adapterLeft);

		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album5_image));
		maa = new MusicAlbumArt("Chvrches", d, 4, false);
		musicRightImageResource.add(maa);
		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album6_image));
		maa = new MusicAlbumArt("Chvrches_s", d, 5, false);
		musicRightImageResource.add(maa);
		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album7_image));
		maa = new MusicAlbumArt("Beast", d, 6, false);
		musicRightImageResource.add(maa);
		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album8_image));
		maa = new MusicAlbumArt("Indians", d, 7, false);
		musicRightImageResource.add(maa);
		d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(
				getResources(), R.drawable.album_add_image));
		maa = new MusicAlbumArt("", d, -1, false);
		musicRightImageResource.add(maa);

		listViewRight.setOnItemClickListener(onRightClickListner);
		adapterRight = new RAlbumArtAdapter(this, R.layout.list_musicimage,
				musicRightImageResource);
		listViewRight.setAdapter(adapterRight);

	}

	@Override
	public void onPause() {
		super.onPause();
		for (int i = 0; i < musicNum; i++) {
			if (mp[i] != null)
				mp[i].stop();
		}

		// Recycle

		logoBitmap.getBitmap().recycle();
		toolbarBtnBitmap.getBitmap().recycle();
		toolbarBackBitmap.getBitmap().recycle();

		dim_drawable.getBitmap().recycle();
		add_drawable.getBitmap().recycle();
		musicImageOn.getBitmap().recycle();
		musicImageOff.getBitmap().recycle();

		musicLeftImageResource.get(0).d.getBitmap().recycle();
		musicLeftImageResource.get(1).d.getBitmap().recycle();
		musicLeftImageResource.get(2).d.getBitmap().recycle();
		musicLeftImageResource.get(3).d.getBitmap().recycle();

		musicRightImageResource.get(0).d.getBitmap().recycle();
		musicRightImageResource.get(1).d.getBitmap().recycle();
		musicRightImageResource.get(2).d.getBitmap().recycle();
		musicRightImageResource.get(3).d.getBitmap().recycle();
		musicRightImageResource.get(4).d.getBitmap().recycle();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for (int i = 0; i < musicNum; i++)
			if (mp[i] != null)
				mp[i].stop();

		listViewLeft = null;
		listViewRight = null;
		musicLeftImageResource = null;
		musicRightImageResource = null;
		adapterLeft = null;
		adapterRight = null;
		window = null;
		musictag = null;
		btnNext = null;
		tagToggle = null;
		RecycleUtils.recursiveRecycle(getWindow().getDecorView());
		System.gc();
	}

	private class LAlbumArtAdapter extends ArrayAdapter<MusicAlbumArt> {

		private ArrayList<MusicAlbumArt> items;

		public LAlbumArtAdapter(Context context, int textViewResourceId,
				ArrayList<MusicAlbumArt> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("LEFT", "GETVIEW " + String.valueOf(position));
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_musicimage, null);
			}
			BitmapDrawable d = items.get(position).d;

			ImageView iv_imagePlay = null;
			ImageView iv_imageMusic = null;
			ImageView iv_imageDim = null;

			boolean isFocus = items.get(position).focus;
			if (d != null) {
				Drawable pos_d = items.get(position).d;
				iv_imageMusic = (ImageView) v
						.findViewById(R.id.iv_image_musictag);
				iv_imagePlay = (ImageView) v
						.findViewById(R.id.iv_image_musictag_play);
				iv_imageDim = (ImageView) v.findViewById(R.id.iv_image_dim);
				iv_imageDim.setBackground(dim_drawable);
				iv_imageMusic.setBackground(pos_d);

			}
			if (isFocus) {
				iv_imageDim.setVisibility(View.INVISIBLE);
				iv_imagePlay.setVisibility(View.VISIBLE);

				Drawable d1 = AnimeUtils.loadDrawableFromResource(
						getResources(), R.drawable.equal_30fps);
				iv_imagePlay.setScaleType(ImageView.ScaleType.CENTER);
				iv_imagePlay.setImageDrawable(d1);
				AnimeUtils.startViewAnimation(iv_imagePlay, true);
			} else {
				iv_imageDim = (ImageView) v.findViewById(R.id.iv_image_dim);
				iv_imageDim.setVisibility(View.VISIBLE);
				iv_imagePlay.setVisibility(View.INVISIBLE);
			}
			return v;
		}

	}

	private class RAlbumArtAdapter extends ArrayAdapter<MusicAlbumArt> {
		private ArrayList<MusicAlbumArt> items;

		public RAlbumArtAdapter(Context context, int textViewResourceId,
				ArrayList<MusicAlbumArt> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("RIGHT", "GETVIEW " + String.valueOf(position));
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_musicimage, null);
			}

			BitmapDrawable pos_d = items.get(position).d;
			boolean isFocus = items.get(position).focus;

			ImageView iv_imageMusic = null;
			ImageView iv_imageDim = null;
			ImageView iv_imageAdd = null;
			ImageView iv_imagePlay = null;

			if (pos_d != null) {
				iv_imageMusic = (ImageView) v
						.findViewById(R.id.iv_image_musictag);
				iv_imageAdd = (ImageView) v
						.findViewById(R.id.iv_image_musictag_add);
				iv_imageDim = (ImageView) v.findViewById(R.id.iv_image_dim);
				iv_imagePlay = (ImageView) v
						.findViewById(R.id.iv_image_musictag_play);
				iv_imageDim.setBackground(dim_drawable);
				iv_imageAdd.setBackground(add_drawable);
				iv_imageMusic.setBackground(pos_d);
			}

			if (position == items.size() - 1) {
				// iv_imageMusic.setAlpha(0.9f);
				iv_imageDim.setVisibility(View.INVISIBLE);
				iv_imageAdd.setVisibility(View.VISIBLE);
				// iv_imageAdd.setAlpha(0.9f);
				isFocus = true;
			} else {
				iv_imageDim.setVisibility(View.VISIBLE);
				iv_imageAdd.setVisibility(View.INVISIBLE);
			}

			if (isFocus) {
				iv_imageDim.setVisibility(View.INVISIBLE);
				if (position != items.size() - 1) {
					iv_imagePlay.setVisibility(View.VISIBLE);
					Drawable d1 = AnimeUtils.loadDrawableFromResource(
							getResources(), R.drawable.equal_30fps);
					iv_imagePlay.setScaleType(ImageView.ScaleType.CENTER);
					iv_imagePlay.setImageDrawable(d1);
					AnimeUtils.startViewAnimation(iv_imagePlay, true);
				} else
					iv_imagePlay.setVisibility(View.INVISIBLE);

			} else {
				iv_imageDim.setVisibility(View.VISIBLE);
				iv_imagePlay.setVisibility(View.INVISIBLE);
			}
			return v;
		}
	}

	private OnItemClickListener onLeftClickListner = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent,
				View view, int pos, long id) {

			// MusicTag
			int what = musicLeftImageResource.get(pos).pos;
			// Data
			boolean isFocusState = musicLeftImageResource.get(pos).focus;
			// 파일검사
			title = musicLeftImageResource.get(pos).title + ".wav";
			File file = new File(path + title);
			if (file.exists()) {
				if (isFocusState) {
					if (mp[what].isPlaying()) { // play중이면 stop
						mp[what].stop();
					}
					musicLeftImageResource.get(pos).focus = false;
					MusicImageTagActivity.resultTagKind.setMusicCur(-1);
				} else {
					MusicImageTagActivity.resultTagKind.setMusicCur(what);
					musicLeftImageResource.get(pos).focus = true;
					for (int i = 0; i < musicLeftImageResource.size(); i++) {
						if (i != pos) {
							musicLeftImageResource.get(i).focus = false;
						}
						musicRightImageResource.get(i).focus = false;
					}
					// MP 생성
					if (mp[what] == null)
						mp[what] = new MediaPlayer();
					if (mp[what].isPlaying()) { // play중이면 stop
						mp[what].stop();
					} else { // play시킬거
						for (int i = 0; i < musicNum; i++)
							// playing 중인 다른 노래 중지
							if (mp[i] != null && mp[i].isPlaying()) {
								mp[i].pause();
							}
						title = musicLeftImageResource.get(pos).title + ".wav";
						isExistFile(title, path + title, what);
					}
				}
				adapterLeft.notifyDataSetChanged();
				adapterRight.notifyDataSetChanged();
			} else {
				isExistFile(title, path + title, what);
			}
		}
	};

	private OnItemClickListener onRightClickListner = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent,
				View view, int pos, long id) {
			// MusicTag
			int what = musicRightImageResource.get(pos).pos;
			// fileManager 실행
			if (what == -1) {
				MusicImageTagActivity.this.startActivity(new Intent(
						MusicImageTagActivity.this, FileManageActivity.class));
			} else {
				// Data
				boolean isFocusState = musicRightImageResource.get(pos).focus;
				// 파일검사
				title = musicRightImageResource.get(pos).title + ".wav";
				File file = new File(path + title);

				if (file.exists()) {
					if (isFocusState) {
						if (mp[what].isPlaying()) { // play중이면 stop
							mp[what].stop();
						}
						musicRightImageResource.get(pos).focus = false;
						MusicImageTagActivity.resultTagKind.setMusicCur(-1);

					} else {
						musicRightImageResource.get(pos).focus = true;
						MusicImageTagActivity.resultTagKind.setMusicCur(what);
						for (int i = 0; i < musicRightImageResource.size() - 1; i++) {
							if (i != pos) {
								musicRightImageResource.get(i).focus = false;
							}
							musicLeftImageResource.get(i).focus = false;
						}
						musicRightImageResource.get(musicRightImageResource
								.size() - 1).focus = true;

						// MP 생성
						if (mp[what] == null)
							mp[what] = new MediaPlayer();
						if (mp[what].isPlaying()) { // play중이면 stop
							mp[what].stop();
						} else { // play시킬거
							for (int i = 0; i < musicNum; i++)
								// playing 중인 다른 노래 중지
								if (mp[i] != null && mp[i].isPlaying()) {
									mp[i].pause();
								}
							title = musicRightImageResource.get(pos).title
									+ ".wav";
							isExistFile(title, path + title, what);
						}
					}
					adapterRight.notifyDataSetChanged();
					adapterLeft.notifyDataSetChanged();
				} else {
					isExistFile(title, path + title, what);
				}
			}

		}
	};

	public void mOnClick(View v) {
		for (int i = 0; i < musicNum; i++)
			if (mp[i] != null)
				mp[i].stop();
		Intent intent = new Intent(MusicImageTagActivity.this,
				TaggingActivity.class);
		intent.putExtra(Config.TAG_FRIEND_ID, strFriendId);
		intent.putExtra(Config.TAG_FIREND_PIC, strFriendPic);
		intent.putExtra(Config.TAG_FRIEND_NAME, strFriendName);
		intent.putExtra(Config.TAG_FIREND_PHONE, strFriendPhone);
		intent.putExtra(Config.TAG_MUSIC_PATH, path + title);
		startActivity(intent);
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

	public void isExistFile(String title, String path, int pos) {
		Log.d("File Exist", path);
		File file = new File(path);
		Log.d("File Exist", file.exists() + ".");
		if (file.exists()) {
			Log.d("File Exist", "존재");
			playSong(path, pos);
		} else {
			Toast.makeText(MusicImageTagActivity.this, "Downloading...",
					Toast.LENGTH_SHORT).show();
			new MusicDownload().execute(strFileUrl + title, title);
		}
	}

	public static final String TAG_MUSIC_TITLE = "music_title";

	class MusicDownload extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// IF SDCARD EXISTS

				File file = new File(path);

				if (!file.exists()) {
					// IF DIRECTORY DOESN'T EXIST
					Log.d(CURRENT_ACTIVITY + "_PATH", "만들어");
					file.mkdirs();
				}
				Log.d(CURRENT_ACTIVITY + "_PATH", path);
			}
		}

		@Override
		protected String doInBackground(String... f_url) {

			int count = 0;
			try {
				URL url = new URL(f_url[0]);
				String title = f_url[1];

				Log.d(CURRENT_ACTIVITY + "_URL", url.toString() + " + " + title);

				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setRequestMethod("POST");
				con.connect();

				int len = con.getContentLength();
				Log.d(CURRENT_ACTIVITY + "_RESPONSE CODE",
						con.getResponseCode() + "!");
				byte[] tmpByte = new byte[len];

				File file = new File(path, title);

				if (!file.exists()) {
					file.createNewFile();
				}
				Log.d(CURRENT_ACTIVITY + "_PATH", file.getAbsolutePath());

				InputStream is = con.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);

				for (;;) {
					count = is.read(tmpByte);
					if (count <= 0) {
						break;
					}
					fos.write(tmpByte, 0, count);
				}
				isDownloadFinish = true;
				is.close();
				fos.close();
				con.disconnect();
			} catch (MalformedURLException e) {
				isDownloadFinish = false;
				Log.e(CURRENT_ACTIVITY + "_ERROR1", e.getMessage());
			} catch (IOException e) {
				isDownloadFinish = false;
				Log.e(CURRENT_ACTIVITY + "_ERROR2", e.getMessage());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			if (isDownloadFinish)
				Toast.makeText(MusicImageTagActivity.this,
						"finish to download!", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getApplicationContext(), "Fail to download",
						Toast.LENGTH_SHORT).show();
		}

	}

	public void playSong(String title, int pos) {

		try {
			mp[pos].reset();
			mp[pos].setDataSource(title);
			mp[pos].prepare();
			mp[pos].start();
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
			i = new Intent(MusicImageTagActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_message:
			dlDrawer.closeDrawers();
			i = new Intent(MusicImageTagActivity.this,
					MessagePlayerActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_setting:
			dlDrawer.closeDrawers();
			i = new Intent(MusicImageTagActivity.this, SettingActivity.class);
			startActivity(i);
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
			stopService(new Intent(MusicImageTagActivity.this,
					ServicePage.class));

			finish();
			startActivity(new Intent(MusicImageTagActivity.this,
					SignInActivity.class));
			break;
		}
	}
}
