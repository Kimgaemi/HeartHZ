package com.heart.friend;

import java.util.ArrayList;

import android.content.Context;
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

public class AcquAdapter extends ArrayAdapter<Friend>{

	private ArrayList<Friend> items;		 
	Context context;

	public AcquAdapter(Context context, int textViewResourceId, ArrayList<Friend> items) {
		super(context, textViewResourceId, items);
		this.items = items;		
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_friend, null);
		}

		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory()
				.displayer(new RoundedBitmapDisplayer(3000))
				.cacheOnDisc().resetViewBeforeLoading()
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile).build();

		Friend friend = items.get(position);
		if (friend != null) {
			
			ImageView ivPic = (ImageView) v.findViewById(R.id.list_iv_pic);
			TextView tvID = (TextView) v.findViewById(R.id.list_tv_uid);
			TextView tvName  = (TextView)v.findViewById(R.id.list_tv_name);

			if(ivPic != null) imageLoader.displayImage(friend.getPicPath(), ivPic, options);
			if(tvID != null) tvID.setText(friend.getId());
			if(tvName!= null) tvName.setText(friend.getName());
		}
		return v;
	}
}