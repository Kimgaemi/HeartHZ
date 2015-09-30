package com.heart.friend;

import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.heart.R;
import com.heart.activity.MainActivity;
import com.heart.activity.SignInActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FriendFragment extends Fragment {

	private ImageView ivPic;
	private ImageView add_plus;
	private TextView tvId;
	private TextView tvName;
	private TextView tvPhone;
	private TextView days;
	private TextView daysOver;

	public static Fragment newInstance(MainActivity context, int pos,
			float scale, Friend item) {
		Log.i("FRAGMENT", "NEWINSTANCE : " + String.valueOf(pos));

		Bundle b = new Bundle();
		b.putInt("POS", pos);
		b.putFloat("SCALE", scale);
		b.putString("ID", item.getId());
		b.putString("NAME", item.getName());
		b.putString("PHONE", item.getPhone());
		b.putString("PIC", item.getPicPath());
		b.putInt("DAY", item.getDays());

		return Fragment.instantiate(context, FriendFragment.class.getName(), b);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		int pos = this.getArguments().getInt("POS");
		if (pos == MainActivity.maxPeople - 1) {
			SignInActivity.recycleBgBitmap(add_plus);
		}
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;

		LinearLayout l = null;

		int pos = this.getArguments().getInt("POS");
		if (pos == MainActivity.maxPeople - 1) {
			// 마지막인 경우에는 Add하는 경우를 올려야함
			l = (LinearLayout) inflater.inflate(R.layout.fragment_add,
					container, false);
			TextView add_tv = (TextView) l.findViewById(R.id.add_friend_text);
			add_tv.setTypeface(Typeface.createFromAsset(getActivity()
					.getAssets(), "fonts/DINPRO-MEDIUM.ttf"));
			add_plus = (ImageView) l.findViewById(R.id.add_friend);

			add_plus.setBackground(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(),R.drawable.add3_btn)));
			
			FriendLinearLayout addroot = (FriendLinearLayout) l
					.findViewById(R.id.addroot);
			float scale = this.getArguments().getFloat("SCALE");
			addroot.setScaleBoth(scale);
		}

		else {

			l = (LinearLayout) inflater.inflate(R.layout.fragment_friend,
					container, false);

			String id = getArguments().getString("ID");
			String name = this.getArguments().getString("NAME");
			String phone = this.getArguments().getString("PHONE");
			String pic = this.getArguments().getString("PIC");
			int dayNum = this.getArguments().getInt("DAY");

			tvId = (TextView) l.findViewById(R.id.tv_home_id);
			tvName = (TextView) l.findViewById(R.id.tv_home_name);
			tvPhone = (TextView) l.findViewById(R.id.tv_home_phone);
			ivPic = (ImageView) l.findViewById(R.id.iv_home_pic);

			days = (TextView) l.findViewById(R.id.tv_days_over);
			daysOver = (TextView) l.findViewById(R.id.tv_days_text);

			tvId.setText(id);
			tvName.setText(name);
			tvPhone.setText("0" + phone);
			days.setText(String.valueOf(dayNum));
			
			DaysOverColor(dayNum);
			daysOver.setText("days over");

			ImageLoader imgloder = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory().cacheOnDisc().resetViewBeforeLoading()
					.showImageForEmptyUri(R.drawable.default_profile)
					.showImageOnFail(R.drawable.default_profile).build();
			imgloder.displayImage(pic, ivPic, options);

			// FONT
			tvName.setTypeface(Typeface.createFromAsset(getActivity()
					.getAssets(), "fonts/DINPRO-MEDIUM.ttf"));
			tvPhone.setTypeface(Typeface.createFromAsset(
					getActivity().getAssets(), "fonts/DINPRO-REGULAR.ttf"));
			days.setTypeface(Typeface.createFromAsset(
					getActivity().getAssets(), "fonts/DINPRO-REGULAR.ttf"));
			daysOver.setTypeface(Typeface.createFromAsset(getActivity()
					.getAssets(), "fonts/DINPRO-MEDIUM.ttf"));

			FriendLinearLayout root = (FriendLinearLayout) l
					.findViewById(R.id.root);

			float scale = this.getArguments().getFloat("SCALE");
			root.setScaleBoth(scale);
			if (pos > MainActivity.maxPeople - 1
					&& pos != MainActivity.maxPeople - 1) {
				root.setVisibility(View.GONE);
			}
		}
		return l;
	}
	private void DaysOverColor(int num){
		if (num <= 7) {
			//  ~7 
			days.setTextColor(getResources().getColor(R.color.page2_text_color4));
		} else if (num <= 15) {
			// ~15
			days.setTextColor(getResources().getColor(R.color.page2_text_color3));
		} else if (num <= 31) {
			// ~31
			days.setTextColor(getResources().getColor(R.color.page2_text_color2));
		} else {
			days.setTextColor(getResources().getColor(R.color.page2_text_color1));
		}
	}

}
