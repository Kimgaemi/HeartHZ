package com.heart.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

	private boolean isPagingEnabled = true;
	private final int SWIPE_MIN_DISTANCE = 100;
	float downX; // initialized at the start of the swipe action
	float upX; // initialized at the end of the swipe action

	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			upX = event.getX();
			final float deltaX = downX - upX;
			if (deltaX < 0 && -deltaX > SWIPE_MIN_DISTANCE) {
				Log.i("SWIPE", "LEFT");
				this.isPagingEnabled = true;
			}
			break;
		}
		if (this.isPagingEnabled)
			return super.onTouchEvent(event);
		else
			return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return super.onInterceptTouchEvent(event);
	}

	public void setPagingEnabled(boolean b) {
		this.isPagingEnabled = b;
	}

}