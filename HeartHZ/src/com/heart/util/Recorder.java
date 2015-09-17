package com.heart.util;

import android.app.Activity;
import android.content.Context;
import android.media.AudioRecord;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heart.R;
import com.heart.wave.WaveDataStore;

public class Recorder extends Thread {

	public static boolean isRecording = false;
	public static boolean isPause = false;

	// Time Data
	String[] timeTable = { "00", "04", "08", "12", "16", "20", "24", "28",
			"32", "36", "40", "44", "48", "52", "56", "60", "64", "68", "72",
			"76", "80", "84", "88", "92", "96" };

	// Variable
	private Context context = null;
	private AudioRecord record = null;
	private int recBufSize = 0;

	// Layout
	private final WaveDataStore store;
	private TextView timeView1;
	private TextView timeView3;
	private TextView timeView5;

	// TIME Variable
	int t1 = 0;
	int t2 = 0;
	int s1 = 0;
	int s2 = 0;
	int ms = 0;

	public Recorder(Context mRecordActivity, AudioRecord record,
			WaveDataStore store, int recBufSize) {
		Log.i("RECORDER THREAD", "CREATE RECORDING");
		this.context = mRecordActivity;
		this.record = record;
		this.recBufSize = recBufSize;
		this.store = store;
		isRecording = true;

		// TEXTVIEW
		timeView1 = (TextView) ((Activity) context).findViewById(R.id.time1);
		timeView3 = (TextView) ((Activity) context).findViewById(R.id.time3);
		timeView5 = (TextView) ((Activity) context).findViewById(R.id.time5);
	}

	@Override
	public void run() {
		Log.i("RECORDER THREAD", "RECORDING STARTED");
		// Running about
		record.startRecording();
		isRecording = true;
		store.clearWaveData();
		writeAudioDataToFile();
	}

	public void finish() {
		Log.i("RECORDER THREAD", "RECORDING FINISHED");
		if (record != null) {
			record.stop();
			record.release();
			record = null;
		}
		this.isRecording = false;
		timeView1.setText("00");
		timeView3.setText("00");
		timeView5.setText("00");
	}

	public void audioPause() {
		Log.i("RECORDER THREAD", "RECORDING PAUSED");
		isPause = true;
	}

	public boolean isPause() {
		return isPause;
	}

	public void audioResume() {
		Log.i("RECORDER THREAD", "RECORDING PAUSED");
		isPause = false;
	}

	private void writeAudioDataToFile() {
		byte data[] = new byte[recBufSize];

		while (isRecording) {
			record.read(data, 0, recBufSize);
			if (!isPause) {
				store.addWaveData(data, 0, data.length);
				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						TimeCount();
					}
				});
			}
		}
		store.closeWaveData();
	}

	// TIME CHECK
	private void TimeCount() {
		timeView5.setText(String.valueOf(t1) + String.valueOf(t2));
		timeView3.setText(String.valueOf(s1) + String.valueOf(s2));
		timeView1.setText(timeTable[ms++]);
		if (ms == 25) {
			ms = 0;
			s2++;
		}
		if (s2 == 10) {
			s2 = 0;
			s1++;
		}
		if (s1 == 6) {
			s1 = 0;
			t2++;
		}
		if (t2 == 10) {
			t2 = 0;
			t1++;
		}
		if (t1 == 10) {
			// 최대 시간 초과
			finish();
			Toast.makeText(context, "녹음최대시간이 초과하였습니다.", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
