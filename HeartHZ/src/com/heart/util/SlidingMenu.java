package com.heart.util;

import java.util.ArrayList;

import com.example.heart.R;
import com.heart.activity.MainActivity;
import com.heart.activity.MessagePlayerActivity;
import com.heart.activity.SettingActivity;
import com.heart.activity.SignInActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class SlidingMenu extends AppCompatActivity{

	private Context mContext = null;
	private Toolbar toolBar = null;
	private DrawerLayout drawer = null;
	private ActionBarDrawerToggle toggle = null;
	private ListView lv = null;
	private ArrayList<MenuItems> item = new ArrayList<MenuItems>();

	public SlidingMenu(Context context) {
		this.mContext = context;
		SlidingMenuInit();
	}

	public Toolbar getToolbar() {
		if (toolBar == null)
			toolBar = (Toolbar) ((Activity) mContext)
			.findViewById(R.id.toolbar);
		return toolBar;
	}

	public DrawerLayout getDlDrwaer() {
		if (drawer == null)
			drawer = (DrawerLayout) ((Activity) mContext)
			.findViewById(R.id.drawer);
		return drawer;
	}

	public ActionBarDrawerToggle getToggle() {
		if (toggle == null)
			toggle = new ActionBarDrawerToggle((Activity) mContext,
					getDlDrwaer(), R.drawable.ic_launcher, R.string.app_name);
		return toggle;
	}

	private void SlidingMenuInit() {
		SharedPreferenceUtil pref = SignInActivity.pref;
		String strName = pref.getValue(Config.TAG_NAME, null);
		String strPic = pref.getValue(Config.TAG_PIC_PATH, null);
		
		MenuItems me = new MenuItems(strName, strPic, 0);
		item.add(me);

		lv = (ListView) ((Activity) mContext).findViewById(R.id.nav_list);
		lv.setAdapter(new MenuAdapter(((Activity) mContext).getBaseContext(), R.layout.list_menu, item));
		

	}
}
