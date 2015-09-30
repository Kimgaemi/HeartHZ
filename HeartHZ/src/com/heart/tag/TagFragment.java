package com.heart.tag;

import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.droidtools.android.graphics.GifDrawable;
import com.example.heart.R;
import com.heart.activity.MusicImageTagActivity;
import com.heart.activity.TaggingActivity;

public class TagFragment extends Fragment {

	public ImageView iv;
	private Drawable d1;

	private Handler mHandler;
	private Runnable mUpdateTimeTask;
	private long startTime = 0;
	
	BitmapWorkerTask task;
	private int pos;
	private TextView tvTagView1;
	public ToggleButton tgn;
	public TextView tvTagView2;
	public LinearLayout dateContainer;
	public EditText etTag1;
	public EditText etTag2;
	public EditText etTag3;

	public static Fragment newInstance(TaggingActivity context, int pos,
			float scale) {
		Log.i("FRAGMENT", "NEWINSTANCE : " + String.valueOf(pos));
		Bundle b = new Bundle();
		b.putInt("pos", pos);
		b.putFloat("scale", scale);
		return Fragment.instantiate(context, TagFragment.class.getName(), b);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		LinearLayout l = (LinearLayout) inflater.inflate(R.layout.fragment_tag,
				container, false);
		pos = this.getArguments().getInt("pos");
		Log.i("FRAGMENT 1", "ONCREATEVIEW " + String.valueOf(pos));

		iv = (ImageView) l.findViewById(R.id.tagcontent);
		tvTagView1 = (TextView) l.findViewById(R.id.tv_tag_view1);
		tvTagView2 = (TextView) l.findViewById(R.id.tv_tag_view2);

		dateContainer = (LinearLayout) l.findViewById(R.id.container);

		etTag1 = (EditText) l.findViewById(R.id.et_tag_date1);
		etTag2 = (EditText) l.findViewById(R.id.et_tag_date2);
		etTag3 = (EditText) l.findViewById(R.id.et_tag_date3);

		tgn = (ToggleButton) l.findViewById(R.id.tgn_select_tag);
		tgn.setOnClickListener(tag_fragment_listener);

		startAnimation(TaggingActivity.cur, pos);
		if (pos == 0) {
			tvTagView2.setText("in 'Joy'");
		} else if (pos == 1) {
			tvTagView2.setText("in 'Gloomy'");
		} else if (pos == 2) {
			tvTagView2.setText("in 'Relaxed'");
		} else if (pos == 3) {
			tvTagView2.setText("in 'Tired'");
		} else {
			tvTagView2.setText("in 'Tired'");
		}

		// fonts
		tvTagView1.setTypeface(Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/AppleSDGothicNeo-Medium.otf"));
		tvTagView2.setTypeface(Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/AppleSDGothicNeo-Medium.otf"));
		etTag1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/AppleSDGothicNeo-Medium.otf"));
		etTag2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/AppleSDGothicNeo-Medium.otf"));
		etTag3.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/AppleSDGothicNeo-Medium.otf"));

		mHandler = new Handler();
		mUpdateTimeTask = new Runnable() {
			public void run() {
				long time = System.currentTimeMillis();
				if (d1 != null) {
					if (d1.setLevel((int) (((time - startTime) / 10) % 10000))) {
						iv.postInvalidate();
					}
				}
				mHandler.postDelayed(this, 10);
			}
		};
		mHandler.post(mUpdateTimeTask);

		MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.tagroot);
		float scale = this.getArguments().getFloat("scale");
		root.setScaleBoth(scale);
		return l;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			if (d1 != null && TaggingActivity.cur != 3) {
				((GifDrawable) d1).recycle();
			}
		} catch (ClassCastException e) {

		}
		d1 = null;
		tgn = null;
		iv = null;
		tvTagView2 = null;
		dateContainer = null;
		etTag1 = null;
		etTag2 = null;
		etTag3 = null;
		System.gc();
	}

	View.OnClickListener tag_fragment_listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (((ToggleButton) v).isChecked()) {
				SelectTag(TaggingActivity.cur, pos, 1);
			} else {
				SelectTag(TaggingActivity.cur, pos, 0);
			}

		}
	};

	private void SelectTag(int mode, int pos, int value) {
		switch (mode) {
		case 0:
			MusicImageTagActivity.resultTagKind.setEmotion(pos, value);
			for (int i = 0; i < 4; i++) {
				if (i != pos)
					TagPagerAdapter.mFragments.get(i).tgn.setChecked(false);
			}
			break;
		case 1:
			MusicImageTagActivity.resultTagKind.setTime(pos, value);
			for (int i = 0; i < 5; i++) {
				if (i != pos)
					TagPagerAdapter.mFragments.get(i).tgn.setChecked(false);
			}
			break;
		case 2:
			MusicImageTagActivity.resultTagKind.setWeather(pos, value);
			for (int i = 0; i < 5; i++) {
				if (i != pos)
					TagPagerAdapter.mFragments.get(i).tgn.setChecked(false);
			}
			break;
		case 3:
			String str1 = etTag1.getText().toString();
			String str2 = etTag2.getText().toString();
			String str3 = etTag3.getText().toString();
			MusicImageTagActivity.resultTagKind.setDate(str1 + str3 + str2,
					value);
			break;
		}
	}

	public void startAnimation(int mode, int num) {
		task = new BitmapWorkerTask();
		switch (mode) {
		case 0:
			switch (num) {
			case 0:
				task.execute("ani_joy.gif");
				break;
			case 1:
				task.execute("ani_gloomy.gif");
				break;
			case 2:
				task.execute("ani_relax.gif");
				break;
			case 3:
				task.execute("ani_tired.gif");
				break;
			}
			break;
		case 1:
			switch (num) {
			case 0:
				task.execute("ani_morning.gif");
				break;
			case 1:
				task.execute("ani_daytime.gif");
				break;
			case 2:
				task.execute("ani_sunset.gif");
				break;
			case 3:
				task.execute("ani_night.gif");
				break;
			case 4:
				task.execute("ani_break.gif");
				break;
			}
			break;
		case 2:
			switch (num) {
			case 0:
				task.execute("ani_sunny.gif");
				break;
			case 1:
				task.execute("ani_rain.gif");
				break;
			case 2:
				task.execute("ani_snow.gif");
				break;
			case 3:
				task.execute("ani_light.gif");
				break;
			case 4:
				task.execute("ani_cloudy.gif");
				break;
			}
			break;
		case 3:
			d1 = getResources().getDrawable(R.drawable.specific_day);
			break;
		}
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, GifDrawable> {

		public BitmapWorkerTask() {
			iv.setImageDrawable(null);
			Log.i("TASK", "持失切 持失");
		}

		// Decode image in background.
		@Override
		protected GifDrawable doInBackground(String... params) {
			String im = params[0];
			Log.i("TASK", "doInBackground " + im);
			return GifDrawable.gifFromAsset(getResources(), im);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(GifDrawable drawable) {
			if (drawable != null) {
				try {
					if (d1 != null) {
						Log.i("TASK", "reCycle");
						((GifDrawable) d1).recycle();
					}
				} catch (ClassCastException e) {
					Log.i("TASK", " Date reCycle");
				}
				Log.i("TASK", "OnPostExecute");
				d1 = drawable;
				iv.setImageDrawable(drawable);
				startTime = System.currentTimeMillis();
			}
		}
	}

}
