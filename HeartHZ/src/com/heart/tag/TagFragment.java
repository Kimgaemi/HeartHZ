package com.heart.tag;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.example.heart.R;
import com.heart.activity.MusicImageTagActivity;
import com.heart.activity.SignInActivity;
import com.heart.activity.TaggingActivity;
import com.heart.util.AnimeUtils;

public class TagFragment extends Fragment {

	private Drawable d1;
	private int pos;
	private TextView tvTagView1;
	public ToggleButton tgn;
	public ImageView iv;
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
		Log.i("FRAGMENT", "ONCREATEVIEW " + String.valueOf(pos));

		iv = (ImageView) l.findViewById(R.id.tagcontent);
		tvTagView1 = (TextView) l.findViewById(R.id.tv_tag_view1);
		tvTagView2 = (TextView) l.findViewById(R.id.tv_tag_view2);

		dateContainer = (LinearLayout) l.findViewById(R.id.container);

		etTag1 = (EditText) l.findViewById(R.id.et_tag_date1);
		etTag2 = (EditText) l.findViewById(R.id.et_tag_date2);
		etTag3 = (EditText) l.findViewById(R.id.et_tag_date3);

		tgn = (ToggleButton) l.findViewById(R.id.tgn_select_tag);
		tgn.setOnClickListener(tag_fragment_listener);

		if (pos == 0) {
			tvTagView2.setText("in 'Joy'");
			iv.setImageResource(R.drawable.back_joy);
			startAnimation(TaggingActivity.cur, 0);
		} else if (pos == 1) {
			tvTagView2.setText("in 'Gloomy'");
			iv.setImageResource(R.drawable.back_gloomy);
		} else if (pos == 2) {
			tvTagView2.setText("in 'Relaxed'");
			iv.setImageResource(R.drawable.back_relaxed);
		} else if (pos == 3) {
			tvTagView2.setText("in 'Tired'");
			iv.setImageResource(R.drawable.back_tired);
		} else {
			tvTagView2.setText("in 'Tired'");
			iv.setImageResource(R.drawable.back_tired);
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

		MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.tagroot);
		float scale = this.getArguments().getFloat("scale");
		root.setScaleBoth(scale);
		return l;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
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
		switch (mode) {
		case 0:
			switch (num) {
			case 0:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_joy);
				break;
			case 1:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_gloomy);
				break;
			case 2:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_relax);
				break;
			case 3:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_tired);
				break;
			}
			break;
		case 1:
			switch (num) {
			case 0:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_morning);
				break;
			case 1:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_daytime);
				break;
			case 2:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_sunset);
				break;
			case 3:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_night);
				break;
			case 4:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_break);
				break;
			}
			break;
		case 2:
			switch (num) {
			case 0:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_sunny);
				break;
			case 1:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_rain);
				break;
			case 2:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_snow);
				break;
			case 3:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_light);
				break;
			case 4:
				d1 = AnimeUtils.loadDrawableFromResource(getResources(),
						R.drawable.ani_cloudy);
				break;
			}
			break;
		case 3:
			d1 = getResources().getDrawable(R.drawable.specific_day);
			break;
		}
		iv.setImageDrawable(d1);
		if (mode != 3) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AnimeUtils.startViewAnimation(iv, true);
				}
			});
		}
	}

	public void stopAnimation() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AnimeUtils.startViewAnimation(iv, false);
			}
		});
		return;
	}

}
