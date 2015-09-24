package com.heart.friend;

import java.util.ArrayList;

import android.animation.ArgbEvaluator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Button;

import com.example.heart.R;
import com.heart.activity.MainActivity;
import com.heart.util.CustomViewPager;

public class FriendPagerAdapter extends FragmentPagerAdapter implements
		ViewPager.OnPageChangeListener {

	private FriendLinearLayout cur = null;
	private FriendLinearLayout next = null;
	private FriendLinearLayout pre = null;
	private MainActivity context;
	private FragmentManager fm;
	private float scale;
	private int curPosition = 0;

	private ArrayList<Friend> item;
	private ArrayList<FriendFragment> mFragments;

	private CustomViewPager vp = null;
	private Button btn = null;
	private Integer[] bgColor = null;
	private Integer[] btnColor = null;
	private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

	public FriendPagerAdapter(MainActivity context, FragmentManager fm,
			CustomViewPager vp, ArrayList<Friend> item, Button btn,
			Integer[] bgColor, Integer[] btnColor) {
		super(fm);
		this.fm = fm;
		this.context = context;
		this.vp = vp;
		this.item = item;
		this.btn = btn;
		this.bgColor = bgColor;
		this.btnColor = btnColor;
		mFragments = new ArrayList<FriendFragment>();

	}

	@Override
	public Fragment getItem(int position) {
		Log.i("FRAGMENT", "GETITEM " + position);
		FriendFragment mf;
		// MAKE THE FIRST PAGER BIGGER THAN OTHERS
		if (position == MainActivity.FIRST_PAGE)
			scale = MainActivity.BIG_SCALE;
		else
			scale = MainActivity.SMALL_SCALE;
		mf = (FriendFragment) FriendFragment.newInstance(context, position,
				scale, item.get(position));
		mFragments.add(mf);
		return mf;
	}

	@Override
	public int getCount() {
		return MainActivity.PAGES;
	}

	// CLICK EVENT
	public int getCurPosition() {
		return curPosition;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		final int fposition = position;
		final float fpositionOffset = positionOffset;
		if (positionOffset >= 0f && positionOffset <= 1f) {
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					curPosition = fposition;
					cur = getRootView(fposition);
					cur.setScaleBoth(MainActivity.BIG_SCALE
							- MainActivity.DIFF_SCALE * fpositionOffset);
					vp.setBackgroundColor((Integer) argbEvaluator.evaluate(
							fpositionOffset, bgColor[fposition],
							bgColor[fposition + 1]));
					btn.setBackgroundColor((Integer) argbEvaluator.evaluate(
							fpositionOffset, btnColor[fposition],
							btnColor[fposition + 1]));

					if (fposition < MainActivity.maxPeople - 1) {
						next = getRootView(fposition + 1);
						next.setScaleBoth(MainActivity.SMALL_SCALE
								+ MainActivity.DIFF_SCALE * fpositionOffset);
					}

					if (fposition > 0) {
						pre = getRootView(fposition - 1);
						pre.setScaleBoth(MainActivity.SMALL_SCALE
								+ MainActivity.DIFF_SCALE * fpositionOffset);
					}
				}
			});
		}
	}

	@Override
	public void onPageSelected(int position) {
		MainActivity.FIRST_PAGE = position;
		Log.i("TEST ADAPTER", MainActivity.FIRST_PAGE + "");
		if (position == MainActivity.maxPeople - 1) {
			btn.setText("+ New RECEIVER");
			vp.setPagingEnabled(false);
			vp.setPagingMax(true);
		} else {
			btn.setText("SELECT");
			vp.setPagingMax(false);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private FriendLinearLayout getRootView(int position) {
		if (position == MainActivity.maxPeople - 1) {
			// LAST PAGE
			return (FriendLinearLayout) fm
					.findFragmentByTag(this.getFragmentTag(position)).getView()
					.findViewById(R.id.addroot);

		} else {
			return (FriendLinearLayout) fm
					.findFragmentByTag(this.getFragmentTag(position)).getView()
					.findViewById(R.id.root);
		}
	}

	private String getFragmentTag(int position) {
		return "android:switcher:" + vp.getId() + ":" + position;
	}

}
