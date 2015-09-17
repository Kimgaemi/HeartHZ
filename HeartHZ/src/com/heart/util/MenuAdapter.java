package com.heart.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heart.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class MenuAdapter extends ArrayAdapter<MenuItems> {

	private ArrayList<MenuItems> items;
	Context context;

	public MenuAdapter(Context context, int textViewResourceId,
			ArrayList<MenuItems> items) {
		super(context, textViewResourceId, items);

		this.items = items; // 생성자의 인자로 넘어온 리스트 객체를 내부 리스트 겍체로 연결
		this.context = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_menu, null);
		}

		ImageLoader imgloder = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory().displayer(new RoundedBitmapDisplayer(300))
				.cacheOnDisc().resetViewBeforeLoading()
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile).build();

		MenuItems menu = items.get(position);
		if (menu != null) {
			TextView tvName = (TextView) v.findViewById(R.id.list_name);
			ImageView ivPic = (ImageView) v.findViewById(R.id.iv_friend_pic);
			tvName.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/DINPRO-MEDIUM.ttf"));
			
			if (tvName != null) tvName.setText(menu.getName());
			if (ivPic != null) {
				if(menu.getPicPath() != null) imgloder.displayImage(menu.getPicPath(), ivPic, options);
				else {
					ivPic.setImageResource(menu.getDrawable());
				}
			}
		}
		return v;
	}

}
