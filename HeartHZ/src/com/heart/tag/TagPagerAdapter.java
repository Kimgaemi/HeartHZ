package com.heart.tag;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.heart.R;
import com.heart.activity.MusicImageTagActivity;
import com.heart.activity.TaggingActivity;
import com.heart.util.CustomViewPager;

public class TagPagerAdapter extends FragmentPagerAdapter implements
		ViewPager.OnPageChangeListener {
	private MyLinearLayout cur = null;
	private MyLinearLayout next = null;
	private MyLinearLayout pre = null;
	private ImageView swipe;
	private TaggingActivity context;
	private FragmentManager fm;
	private float scale;
	private int curPosition = 0;
	private boolean init = true;
	private int maxPage = 0;
	private CustomViewPager pager;

	public static ArrayList<TagFragment> mFragments = null;

	public TagPagerAdapter(TaggingActivity context, FragmentManager fm,
			ImageView swipe, CustomViewPager pager) {
		super(fm);
		this.fm = fm;
		this.context = context;
		this.swipe = swipe;
		this.pager = pager;
		maxPage = TaggingActivity.PAGES - 1;
		mFragments = new ArrayList<TagFragment>();
		Log.i("MyPAgerAdapter", "FINIXH");
	}

	@Override
	public Fragment getItem(int position) {
		Log.i("FRAGMENT", "getItem : " + String.valueOf(position));
		// make the first pager bigger than others
		if (position == TaggingActivity.FIRST_PAGE)
			scale = TaggingActivity.BIG_SCALE;
		else
			scale = TaggingActivity.SMALL_SCALE;
		TagFragment mf = (TagFragment) TagFragment.newInstance(context,
				position, scale);
		mFragments.add(mf);
		return mf;
	}

	@Override
	public int getCount() {
		return TaggingActivity.PAGES;
	}

	// CLICK Event
	public int getCurPosition() {
		return curPosition;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (init) {
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.hide(mFragments.get(4));
			fragmentTransaction.commit();
			init = false;
		}

		if (positionOffset >= 0f && positionOffset <= 1f) {
			curPosition = position;
			cur = getRootView(position);
			cur.setScaleBoth(TaggingActivity.BIG_SCALE
					- TaggingActivity.DIFF_SCALE * positionOffset);

			maxPage = findMaxPage(TaggingActivity.cur);

			if (position < maxPage) {
				next = getRootView(position + 1);
				next.setScaleBoth(TaggingActivity.SMALL_SCALE
						+ TaggingActivity.DIFF_SCALE * positionOffset);
			}
			if (position > 0) {
				pre = getRootView(position - 1);
				pre.setScaleBoth(TaggingActivity.SMALL_SCALE
						+ TaggingActivity.DIFF_SCALE * positionOffset);
			}
		}
	}

	@Override
	public void onPageSelected(int position) {
		Log.i("FRAGMENT", "OnPageSelected " + String.valueOf(position));
		justOneAnimation(position);
		fourSwipe(TaggingActivity.cur, position);
		if (position == maxFragment(TaggingActivity.cur))
			pager.setPagingEnabled(false);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		Log.i("FRAGMENT", "onPageScrollStateChanged " + String.valueOf(state));
	}

	private MyLinearLayout getRootView(int position) {
		return (MyLinearLayout) fm
				.findFragmentByTag(this.getFragmentTag(position)).getView()
				.findViewById(R.id.tagroot);
	}

	private String getFragmentTag(int position) {
		return "android:switcher:" + context.pager.getId() + ":" + position;
	}

	private void justOneAnimation(int position) {
		Log.i("JUSTONEANIMATION", "GOODMAS");
		mFragments.get(position).startAnimation(TaggingActivity.cur, position);
		for (int i = 0; i < mFragments.size(); i++) {
			if (i != position) {
				mFragments.get(i).stopAnimation();
			}
		}
	}

	private void SelectTag(int mode) {
		int result = -1;
		switch (mode) {
		case 0:
			result = MusicImageTagActivity.resultTagKind.getEmotion();
			for (int i = 0; i < 5; i++) {
				if (i != result)
					TagPagerAdapter.mFragments.get(i).tgn.setChecked(false);
			}
			if (result != -1)
				TagPagerAdapter.mFragments.get(result).tgn.setChecked(true);
			break;
		case 1:
			result = MusicImageTagActivity.resultTagKind.getTime();
			for (int i = 0; i < 5; i++) {
				if (i != result)
					TagPagerAdapter.mFragments.get(i).tgn.setChecked(false);
			}
			if (result != -1)
				TagPagerAdapter.mFragments.get(result).tgn.setChecked(true);
			break;
		case 2:
			result = MusicImageTagActivity.resultTagKind.getWeather();
			for (int i = 0; i < 5; i++) {
				if (i != result)
					TagPagerAdapter.mFragments.get(i).tgn.setChecked(false);
			}
			if (result != -1)
				TagPagerAdapter.mFragments.get(result).tgn.setChecked(true);

			break;
		case 3:
			String str = MusicImageTagActivity.resultTagKind.getDate();
			TagPagerAdapter.mFragments.get(0).etTag2.setText(str
					.substring(2, 4));
			TagPagerAdapter.mFragments.get(0).etTag1.setText(str
					.substring(4, 8));
			TagPagerAdapter.mFragments.get(0).etTag3.setText(str
					.substring(0, 2));
			break;
		}
	}

	public int maxFragment(int num) {
		int result = 0;
		switch (num) {
		case 0:
			result = 3;
			break;
		case 1:
			result = 4;
			break;
		case 2:
			result = 4;
			break;
		case 3:
			result = 0;
			break;
		}
		return result;
	}

	public void tagContextSwithing(int pos) {

		pager.setCurrentItem(0);
		pager.setPagingEnabled(true);
		FragmentTransaction fragmentTransaction;
		fragmentTransaction = fm.beginTransaction();
		SelectTag(TaggingActivity.cur);
		fourSwipe(TaggingActivity.cur, 0);
		switch (pos) {
		case 0:
			fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.hide(mFragments.get(4));
			fragmentTransaction.commit();

			mFragments.get(0).iv
					.setImageResource(R.drawable.simul_emotion_joy_00000);
			mFragments.get(0).tvTagView2.setText("in 'Joy'");
			mFragments.get(1).iv
					.setImageResource(R.drawable.simul_emotion_gloomy_00000);
			mFragments.get(1).tvTagView2.setText("in 'Gloomy'");
			mFragments.get(2).iv
					.setImageResource(R.drawable.simul_emotion_relaxed_00000);
			mFragments.get(2).tvTagView2.setText("in 'Relaxed'");
			mFragments.get(3).iv
					.setImageResource(R.drawable.simul_emotion_tired_00000);
			mFragments.get(3).tvTagView2.setText("in 'Tired'");
			mFragments.get(0).startAnimation(TaggingActivity.cur, 0);
			break;

		case 1:
			fragmentTransaction.show(mFragments.get(4));
			fragmentTransaction.commit();
			SelectTag(TaggingActivity.cur);
			// VIEW
			mFragments.get(4).iv
					.setImageResource(R.drawable.simul_time_break_00000);
			mFragments.get(4).tvTagView2.setText("at 'Break of Day'");

			mFragments.get(0).iv
					.setImageResource(R.drawable.simul_time_morning_00000);
			mFragments.get(0).tvTagView2.setText("in the 'Morning'");
			mFragments.get(1).iv
					.setImageResource(R.drawable.simul_time_daytime_00000);
			mFragments.get(1).tvTagView2.setText("in the 'DayTime'");
			mFragments.get(2).iv
					.setImageResource(R.drawable.simul_time_sunset_00000);
			mFragments.get(2).tvTagView2.setText("at 'Sunset'");
			mFragments.get(3).iv
					.setImageResource(R.drawable.simul_time_night_00000);
			mFragments.get(3).tvTagView2.setText("at 'Night'");

			mFragments.get(0).startAnimation(TaggingActivity.cur, 0);
			break;
		case 2:
			fragmentTransaction = fm.beginTransaction();
			for (int i = 1; i < 5; i++) {
				fragmentTransaction.show(mFragments.get(i));
			}
			fragmentTransaction.commit();
			SelectTag(TaggingActivity.cur);
			TagPagerAdapter.mFragments.get(0).dateContainer
					.setVisibility(View.INVISIBLE);
			// VIEW
			mFragments.get(0).iv
					.setImageResource(R.drawable.simul_weather_sunny_00000);
			mFragments.get(0).tvTagView2.setText("on 'Sunny day'");
			mFragments.get(1).iv
					.setImageResource(R.drawable.simul_weather_rain_00000);
			mFragments.get(1).tvTagView2.setText("on 'Rainy day'");
			mFragments.get(2).iv
					.setImageResource(R.drawable.simul_weather_snow_00030);
			mFragments.get(2).tvTagView2.setText("on 'Snowy day'");
			mFragments.get(3).iv
					.setImageResource(R.drawable.simul_weather_light_00000);
			mFragments.get(3).tvTagView2.setText("on 'Lighting day'");
			mFragments.get(4).iv
					.setImageResource(R.drawable.simul_weather_cloudy_00000);
			mFragments.get(4).tvTagView2.setText("on 'Cloudy day'");

			mFragments.get(0).startAnimation(TaggingActivity.cur, 0);
			break;
		case 3:
			pager.setPagingEnabled(false);
			fragmentTransaction = fm.beginTransaction();
			for (int i = 1; i < 5; i++) {
				fragmentTransaction.hide(mFragments.get(i));
			}
			fragmentTransaction.commit();
			TagPagerAdapter.mFragments.get(0).dateContainer
					.setVisibility(View.VISIBLE);
			SelectTag(TaggingActivity.cur);
			mFragments.get(0).iv.setImageResource(R.drawable.specific_day);
			mFragments.get(0).tvTagView2.setText("on 'Special day'");
			break;
		}
	}

	private void fourSwipe(int mode, int position) {
		switch (mode) {
		case 0:
			switch (position) {
			case 0:
				swipe.setImageResource(R.drawable.swipe1_icon_01);
				break;
			case 1:
				swipe.setImageResource(R.drawable.swipe2_icon_01);
				break;
			case 2:
				swipe.setImageResource(R.drawable.swipe3_icon_01);
				break;
			case 3:
				swipe.setImageResource(R.drawable.swipe4_icon_01);
				break;
			}
			break;
		case 1:
			switch (position) {
			case 0:
				swipe.setImageResource(R.drawable.swipe5_icon);
				break;
			case 1:
				swipe.setImageResource(R.drawable.swipe6_icon);
				break;
			case 2:
				swipe.setImageResource(R.drawable.swipe7_icon);
				break;
			case 3:
				swipe.setImageResource(R.drawable.swipe8_icon);
				break;
			case 4:
				swipe.setImageResource(R.drawable.swipe9_icon);
				break;
			}
			break;
		case 2:
			swipe.setVisibility(View.VISIBLE);
			switch (position) {
			case 0:
				swipe.setImageResource(R.drawable.swipe5_icon);
				break;
			case 1:
				swipe.setImageResource(R.drawable.swipe6_icon);
				break;
			case 2:
				swipe.setImageResource(R.drawable.swipe7_icon);
				break;
			case 3:
				swipe.setImageResource(R.drawable.swipe8_icon);
				break;
			case 4:
				swipe.setImageResource(R.drawable.swipe9_icon);
				break;
			}
			break;
		case 3:
			swipe.setVisibility(View.INVISIBLE);
			break;
		}
	}

	private int findMaxPage(int num) {
		int pager = 0;
		switch (num) {
		case 0:
			pager = 3;
			break;
		case 1:
			pager = 4;
			break;
		case 2:
			pager = 4;
			break;
		case 3:
			pager = 0;
			break;
		}
		return pager;
	}
}
