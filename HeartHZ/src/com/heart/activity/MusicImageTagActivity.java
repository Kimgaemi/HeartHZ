package com.heart.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
import android.widget.ToggleButton;

import com.example.heart.R;
import com.heart.util.AnimeUtils;
import com.heart.util.Config;
import com.heart.util.SlidingMenu;
import com.heart.util.TagKind;

public class MusicImageTagActivity extends AppCompatActivity {

	// MusicPlater
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

	// VARIABLE
	private String strFriendId;
	private String strFriendPic;
	private String strFriendName;
	private String strFriendPhone;
	private String strFriendCPic;
	private String strFriendEmail;
	private String path;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;

	public static TagKind resultTagKind;

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
		strFriendCPic = i.getStringExtra(Config.TAG_CPIC_PATH);
		strFriendEmail = i.getStringExtra(Config.TAG_USER_EMAIL);

		tagToggle.setChecked(true);
		tagToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!tagToggle.isChecked()) {
					for (int i = 0; i < musicNum; i++)
						if (mp[i] != null)
							mp[i].stop();
					Intent intent = new Intent(MusicImageTagActivity.this,
							MusicTagActivity.class);
					intent.putExtra(Config.TAG_FRIEND_ID, strFriendId);
					intent.putExtra(Config.TAG_FIREND_PIC, strFriendPic);
					intent.putExtra(Config.TAG_FRIEND_NAME, strFriendName);
					intent.putExtra(Config.TAG_FIREND_PHONE, strFriendPhone);
					intent.putExtra(Config.TAG_CPIC_PATH, strFriendCPic);
					intent.putExtra(Config.TAG_USER_EMAIL, strFriendEmail);
					MusicImageTagActivity.this.startActivity(intent);
					finish();
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
				+ "/MusicTag/";

		// MENU
		menuInit();
		View menu = (View) findViewById(R.id.music_image_tag_menu);
		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		logo.setImageResource(R.drawable.logo3_icon_01);

	}

	private class MusicAlbumArt {
		String title;
		Drawable d;
		int pos;
		boolean focus;
		boolean playing;

		MusicAlbumArt(String title, Drawable d, int pos, boolean focus) {
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
		musicLeftImageResource = new ArrayList<MusicAlbumArt>();
		musicRightImageResource = new ArrayList<MusicAlbumArt>();
		listViewLeft = (ListView) findViewById(R.id.left_image_musictag);
		listViewRight = (ListView) findViewById(R.id.right_image_musictag);

		Drawable d = getResources().getDrawable(R.drawable.album1_image);
		MusicAlbumArt maa = new MusicAlbumArt("Born to die", d, 0, false);
		musicLeftImageResource.add(maa);
		d = getResources().getDrawable(R.drawable.album2_image);
		maa = new MusicAlbumArt("Dream", d, 1, false);
		musicLeftImageResource.add(maa);
		d = getResources().getDrawable(R.drawable.album3_image);
		maa = new MusicAlbumArt("Hooka", d, 2, false);
		musicLeftImageResource.add(maa);
		d = getResources().getDrawable(R.drawable.album4_image);
		maa = new MusicAlbumArt("Healter Svelter", d, 3, false);
		musicLeftImageResource.add(maa);

		listViewLeft.setOnItemClickListener(onLeftClickListner);
		adapterLeft = new LAlbumArtAdapter(this, R.layout.list_musicimage,
				musicLeftImageResource);
		listViewLeft.setAdapter(adapterLeft);

		d = getResources().getDrawable(R.drawable.album5_image);
		maa = new MusicAlbumArt("Chvrches", d, 4, false);
		musicRightImageResource.add(maa);
		d = getResources().getDrawable(R.drawable.album6_image);
		maa = new MusicAlbumArt("Chvrches_s", d, 5, false);
		musicRightImageResource.add(maa);
		d = getResources().getDrawable(R.drawable.album7_image);
		maa = new MusicAlbumArt("Beast", d, 6, false);
		musicRightImageResource.add(maa);
		d = getResources().getDrawable(R.drawable.album8_image);
		maa = new MusicAlbumArt("Indians", d, 7, false);
		musicRightImageResource.add(maa);
		d = getResources().getDrawable(R.drawable.album_add_image);
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
		for (int i = 0; i < musicNum; i++)
			if (mp[i] != null)
				mp[i].stop();
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
			Drawable d = items.get(position).d;

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
				iv_imageMusic.setImageDrawable(pos_d);
			}
			if (isFocus) {
				iv_imageDim = (ImageView) v.findViewById(R.id.iv_image_dim);
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

			Drawable pos_d = items.get(position).d;
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
				iv_imageMusic.setImageDrawable(pos_d);
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
					playSong(path + title, what);
				}
			}
			adapterLeft.notifyDataSetChanged();
			adapterRight.notifyDataSetChanged();
		}
	};

	private OnItemClickListener onRightClickListner = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent,
				View view, int pos, long id) {
			// MusicTag
			int what = musicRightImageResource.get(pos).pos;
			// Data
			boolean isFocusState = musicRightImageResource.get(pos).focus;
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
				musicRightImageResource.get(musicRightImageResource.size() - 1).focus = true;

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
					title = musicRightImageResource.get(pos).title + ".wav";
					playSong(path + title, what);
				}
			}
			adapterRight.notifyDataSetChanged();
			adapterLeft.notifyDataSetChanged();
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
		intent.putExtra(Config.TAG_CPIC_PATH, strFriendCPic);
		intent.putExtra(Config.TAG_USER_EMAIL, strFriendEmail);
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
			close();
			break;
		}
	}

	private void close() {
		finish();
		Intent intent = new Intent(MusicImageTagActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("KILL_ACT", true);
		startActivity(intent);
	}
}
