package com.heart.friend;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.heart.activity.MainActivity;

public class FriendLinearLayout extends LinearLayout {
	private float scale = MainActivity.BIG_SCALE;

	public FriendLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FriendLinearLayout(Context context) {
		super(context);
	}
	
	public void setScaleBoth(float scale) {
		this.scale = scale;
		this.invalidate(); 
		// IF YOU WANT TO SEE THE SCALE EVERY TIME YOU SET
		// SCALE YOU NEED TO HAVE THIS LINE HERE,
		// INVALIDATE() FUNCTION WILL CALL ONDRAW(CANVAS)
		// TO REDRAW THE VIEW FOR YOU
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// THE MAIN MECHANISM TO DISPLAY SCALE ANIMATION, YOU CAN CUSTOMIZE IT
		// AS YOUR NEEDS
		int w = this.getWidth();
		int h = this.getHeight();
		canvas.scale(scale, scale, w / 2, h / 2);
		super.onDraw(canvas);
	}
}
