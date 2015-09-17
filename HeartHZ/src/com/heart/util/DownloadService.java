package com.heart.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class DownloadService extends Service {
	public static boolean IsPeriSer = false;
	DownloadGetter getter;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		IsPeriSer = true;
		getter = new DownloadGetter(DownloadService.this);
		getter.start();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		IsPeriSer = false;
		// getter.setRunning();
		super.onDestroy();
	}
}