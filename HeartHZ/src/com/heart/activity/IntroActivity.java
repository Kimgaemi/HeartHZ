package com.heart.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.heart.R;

public class IntroActivity extends Activity {

	final int TIME_DELAY = 500;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		intent = new Intent(IntroActivity.this, SignInActivity.class);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				startActivity(intent);
				finish();
			}
		}, TIME_DELAY);
	}
}
