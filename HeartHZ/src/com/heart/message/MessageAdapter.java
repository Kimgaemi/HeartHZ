package com.heart.message;

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

public class MessageAdapter extends ArrayAdapter<Message>{

	private ArrayList<Message> items;		 
	Context context;

	public MessageAdapter(Context context, int textViewResourceId, ArrayList<Message> items) {
		super(context, textViewResourceId, items);
		this.items = items;		
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_message, null);
		}

		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory()
				.displayer(new RoundedBitmapDisplayer(3000))
				.cacheOnDisc().resetViewBeforeLoading()
				.showImageForEmptyUri(R.drawable.default_profile)
				.showImageOnFail(R.drawable.default_profile).build();

		Message message = items.get(position);
		if (message != null) {
			
			ImageView ivPic = (ImageView) v.findViewById(R.id.iv_message_pic);

			TextView tvMss = (TextView) v.findViewById(R.id.tv_message_title);
			TextView tvTag  = (TextView)v.findViewById(R.id.tv_message_tag);
			TextView tvName = (TextView) v.findViewById(R.id.tv_message_friend);

			String tag = "";
			if(message.getEmotion() != null) tag = message.getEmotion() + " ";
			if(message.getWeather() != null) tag += message.getWeather() + " ";
			if(message.getTime() != null) tag += message.getTime() + " ";

			if(ivPic != null) imageLoader.displayImage(message.getPicPath(), ivPic, options);
			if(tvMss != null) tvMss.setText(message.getTitle());
			if(tvName!= null) tvName.setText(message.getFrom());
			if(tvTag != null) tvTag.setText(tag);
		}
		return v;
	}
}