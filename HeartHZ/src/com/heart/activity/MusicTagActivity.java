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

public class MusicTagActivity extends AppCompatActivity {

	protected static final int TAG_STOP = 0;
	protected static final int TAG_PLAY = 1;
	private MusicAdapter adapter;
	private int musicNum = 8;
	private MediaPlayer mp[] = new MediaPlayer[musicNum];
	private boolean[] isOpen = new boolean[musicNum];
	ArrayList<Music> m_orders;
	String title;

	// VARIABLE
	private String strFriendId;
	private String strFriendPic;
	private String strFriendName;
	private String strFriendPhone;
	private String strFriendCPic;
	private String strFriendEmail;
	private String path;

	// VIEW
	private Window window = null;
	private TextView musictag;
	private ToggleButton tagToggle;
	private Button btnNext;
	private ListView listView;

	// SLIDING TOOL BAR
	private Toolbar toolbar;
	private DrawerLayout dlDrawer;
	private ActionBarDrawerToggle dtToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musictag);

		musictag = (TextView) findViewById(R.id.tv_music_tag);
		btnNext = (Button) findViewById(R.id.musictag1_nextbtn);
		listView = (ListView) findViewById(R.id.music_list);

		Intent i = getIntent();
		strFriendId = i.getStringExtra(Config.TAG_FRIEND_ID);
		strFriendPic = i.getStringExtra(Config.TAG_FIREND_PIC);
		strFriendName = i.getStringExtra(Config.TAG_FRIEND_NAME);
		strFriendPhone = i.getStringExtra(Config.TAG_FIREND_PHONE);
		strFriendCPic = i.getStringExtra(Config.TAG_CPIC_PATH);
		strFriendEmail = i.getStringExtra(Config.TAG_USER_EMAIL);

		tagToggle = (ToggleButton) findViewById(R.id.music_toggle);
		tagToggle.setChecked(false);
		tagToggle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tagToggle.isChecked()) {
					for (int i = 0; i < musicNum; i++)
						if (mp[i] != null)
							mp[i].stop();
					Intent intent = new Intent(MusicTagActivity.this,
							MusicImageTagActivity.class);
					MusicTagActivity.this.startActivity(intent);
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

		// FONT
		musictag.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINPRO-MEDIUM.ttf"));
		btnNext.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/DINMed.ttf"));
		path = Environment.getExternalStorageDirectory().getPath()
				+ "/MusicTag/";
		Log.d("Tag", path);
		ArrayList<Music> m_orders = new ArrayList<Music>();
		listView.setOnItemClickListener(onClickListner);

		Music m1 = new Music("Hooka", "Hyukoh");
		Music m2 = new Music("Indians", "Somewhere Else");
		Music m3 = new Music("Born to die", "Landa Del Ray");
		Music m4 = new Music("Dream", "Coltrane Plays");
		Music m5 = new Music("Chvrches", "Plays The Blue");
		Music m6 = new Music("Healter Svelter", "The Beatles");
		Music m7 = new Music("Chvrches_s", "Special Edition");
		Music m8 = new Music("Beast", "Pearl And The Beard");

		m_orders.add(m3);
		m_orders.add(m4);
		m_orders.add(m1);
		m_orders.add(m6);
		m_orders.add(m5);
		m_orders.add(m7);
		m_orders.add(m8);
		m_orders.add(m2);

		adapter = new MusicAdapter(this, R.layout.list_musictag, m_orders);
		listView.setAdapter(adapter);

		// MENU
		menuInit();
		View menu = (View) findViewById(R.id.musictag_menu);
		ImageView logo = (ImageView) menu.findViewById(R.id.iv_toolbar_logo);
		logo.setImageResource(R.drawable.logo3_icon_01);
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

		adapter = null;
		for (int i = 0; i < musicNum; i++) {
			mp[i] = null;
		}
		m_orders = null;

		window = null;
		musictag = null;
		btnNext = null;
		tagToggle = null;
		listView = null;
		System.gc();
	}

	private OnItemClickListener onClickListner = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent,
				View view, int pos, long id) {
			ImageView iv_pause = (ImageView) view
					.findViewById(R.id.iv_musictag_pause);
			ImageView iv_play = (ImageView) view
					.findViewById(R.id.iv_musictag_playing);
			TextView tv_num = (TextView) view
					.findViewById(R.id.tv_musictag_num);
			TextView tv_title = (TextView) view
					.findViewById(R.id.tv_musictag_title);
			TextView tv_singer = (TextView) view
					.findViewById(R.id.tv_musictag_singer);

			if (!isOpen[pos]) {
				isOpen[pos] = true;
				// MusicTag
				MusicImageTagActivity.resultTagKind.setMusicCur(pos);
				tv_num.setVisibility(View.INVISIBLE);
				iv_pause.setVisibility(View.VISIBLE);
				iv_play.setVisibility(View.VISIBLE);

				tv_title.setTextColor(getResources().getColor(
						R.color.musictag_focus_tv));
				tv_singer.setTextColor(getResources().getColor(
						R.color.musictag_focus_tv));

				Drawable d1 = AnimeUtils.loadDrawableFromResource(
						getResources(), R.drawable.mlequal);
				iv_play.setScaleType(ImageView.ScaleType.CENTER);
				iv_play.setImageDrawable(d1);
				AnimeUtils.startViewAnimation(iv_play, true);

				// MP 생성
				if (mp[pos] == null)
					mp[pos] = new MediaPlayer();
				if (mp[pos].isPlaying()) { // play중이면 stop
					mp[pos].stop();
				} else { // play시킬거
					for (int i = 0; i < musicNum; i++)
						// playing 중인 다른 노래 중지
						if (mp[i] != null && mp[i].isPlaying()) {
							mp[i].pause();
						}

					title = adapter.getItem(pos).getTitle() + ".wav";
					playSong(path + title, pos);
				}

				for (int i = 0; i < musicNum; i++) {
					if (i != pos) {

						View itemblock = listView.getChildAt(i);
						ImageView not_iv_pause = (ImageView) itemblock
								.findViewById(R.id.iv_musictag_pause);
						ImageView not_iv_play = (ImageView) itemblock
								.findViewById(R.id.iv_musictag_playing);
						TextView not_tv_num = (TextView) itemblock
								.findViewById(R.id.tv_musictag_num);
						TextView not_tv_title = (TextView) itemblock
								.findViewById(R.id.tv_musictag_title);
						TextView not_tv_singer = (TextView) itemblock
								.findViewById(R.id.tv_musictag_singer);

						not_iv_pause.setVisibility(View.INVISIBLE);
						not_iv_play.setVisibility(View.INVISIBLE);
						not_tv_num.setVisibility(View.VISIBLE);

						not_tv_title.setTextColor(getResources().getColor(
								R.color.musictag_not_focus_title));
						not_tv_singer.setTextColor(getResources().getColor(
								R.color.musictag_not_focus_singer));

					}
				}
			} else {
				for (int i = 0; i < musicNum; i++)
					if (mp[i] != null)
						mp[i].stop();
				AnimeUtils.startViewAnimation(iv_play, false);
				isOpen[pos] = false;
				MusicImageTagActivity.resultTagKind.setMusicCur(-1);
				tv_num.setVisibility(View.VISIBLE);
				iv_pause.setVisibility(View.INVISIBLE);
				iv_play.setVisibility(View.INVISIBLE);

				tv_title.setTextColor(getResources().getColor(
						R.color.musictag_not_focus_title));
				tv_singer.setTextColor(getResources().getColor(
						R.color.musictag_not_focus_singer));
			}

		};
	};

	private class MusicAdapter extends ArrayAdapter<Music> {

		private ArrayList<Music> items;

		public MusicAdapter(Context context, int textViewResourceId,
				ArrayList<Music> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_musictag, null);
			}
			Music p = items.get(position);
			if (p != null) {
				TextView tv_num = (TextView) v
						.findViewById(R.id.tv_musictag_num);
				TextView tv_title = (TextView) v
						.findViewById(R.id.tv_musictag_title);
				TextView tv_singer = (TextView) v
						.findViewById(R.id.tv_musictag_singer);
				tv_num.setTypeface(Typeface.createFromAsset(getAssets(),
						"fonts/DINPRO-MEDIUM.ttf"));
				tv_title.setTypeface(Typeface.createFromAsset(getAssets(),
						"fonts/DINPRO-REGULAR.ttf"));
				tv_singer.setTypeface(Typeface.createFromAsset(getAssets(),
						"fonts/AppleSDGothicNeo-Regular.otf"));
				tv_num.setText("0" + ++position);
				tv_title.setText(p.getTitle());
				tv_singer.setText(p.getSinger());
			}
			return v;
		}

	}

	class Music {
		private String title;
		private String singer;
		private boolean open;

		public Music(String _Name, String _Number) {
			this.title = _Name;
			this.singer = _Number;
			this.open = false;
		}

		public String getTitle() {
			return title;
		}

		public String getSinger() {
			return singer;
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

	public void mOnClick(View v) {
		Intent intent = new Intent(MusicTagActivity.this, TaggingActivity.class);
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
			i = new Intent(MusicTagActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;

		case R.id.ll_menu_message:
			dlDrawer.closeDrawers();
			i = new Intent(MusicTagActivity.this, MessagePlayerActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_setting:
			dlDrawer.closeDrawers();
			i = new Intent(MusicTagActivity.this, SettingActivity.class);
			startActivity(i);
			break;

		case R.id.ll_menu_logout:
			close();
			break;
		}
	}

	private void close() {
		finish();
		Intent intent = new Intent(MusicTagActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("KILL_ACT", true);
		startActivity(intent);
	}
}
