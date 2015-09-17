package com.heart.wave;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import com.heart.activity.RecordActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class WaveDisplayView extends View implements WaveDataStore,
		OnGestureListener {

	private GestureDetector gestureDetector;
	private final Handler handler;
	private final ByteArrayOutputStream waveData = new ByteArrayOutputStream(
			44100 * 120);
	private final Paint waveBaseLine = new Paint();
	private final Paint markerLine = new Paint();
	private int unitSize = 3584;
	private int BoxSize = 20;
	int cur = 0;

	boolean flag = true;
	boolean ismoving = false;
	int movingPoint = 0;

	public static int startPoint = 0;
	public static int finishPoint = 0;
	int trimParameter = 0;

	float x;

	// ZOOM IN OUT
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private float oldDist = 1f;
	private int mode = NONE;

	private int count = 0;

	/**
	 * @param context
	 */
	public WaveDisplayView(Context context) {
		super(context);
		handler = new Handler();

		waveBaseLine.setARGB(128, 221, 83, 69);
		waveBaseLine.setStyle(Paint.Style.FILL);
		waveBaseLine.setStrokeWidth(5.0f);
		// waveBaseLine.setAntiAlias(true);
		waveBaseLine.setStrokeCap(Paint.Cap.ROUND);

		markerLine.setARGB(255, 128, 128, 255);
		markerLine.setStyle(Paint.Style.FILL);
		markerLine.setStrokeWidth(10.0f);
		markerLine.setStrokeCap(Paint.Cap.ROUND);

		gestureDetector = new GestureDetector(context, this);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		byte[] cbs = waveData.toByteArray();
		Log.d("ONDRAW", "WaveDate Size : " + String.valueOf(cbs.length)
				+ " Cur position : " + String.valueOf(cur));

		byte[] bs = null;
		if (cbs.length == 0) {
			return;
		}
		if (ismoving) {
			bs = Arrays.copyOfRange(cbs, movingPoint, movingPoint + unitSize
					* BoxSize);
		} else {
			if (cbs.length >= unitSize * BoxSize) {
				bs = Arrays.copyOfRange(cbs, cur, cur + unitSize * BoxSize);
				cur += unitSize * 2;
			} else {
				bs = cbs;
			}
		}

		final int margin = 2;
		int width = this.getWidth() / 2;
		int height = this.getHeight() - margin * 2;

		double[] ds = NormalizeWaveData.convertWaveData(bs);
		{
			double[][] plots = NormalizeWaveData.convertPlotData(ds, width);
			float lastY = height / 2.0f;
			boolean isLastPlus = true;
			for (int x = 0; x < width; x++) {
				if (plots != null && plots[x] != null) {
					boolean wValue = plots[x][0] > 0.0 && plots[x][1] < 0.0;
					if (wValue) {
						double[] values = isLastPlus ? new double[] {
								plots[x][1], plots[x][0] } : new double[] {
								plots[x][0], plots[x][1] };
						for (double d : values) {
							lastY = drawWaveLine(canvas, d, x, lastY, height,
									margin);
						}
					} else {
						double value = 0.0;
						if (plots[x][1] < 0.0) {
							value = plots[x][1];
							isLastPlus = false;
						} else {
							value = plots[x][0];
							isLastPlus = true;
						}
						lastY = drawWaveLine(canvas, value, x, lastY, height,
								margin);
					}
				}
			}
		}
		// StartMarkerPrinting
		if (startPoint >= movingPoint
				&& startPoint < movingPoint + unitSize * BoxSize && ismoving) {
			// canvas.drawLine()
			int xpoint = (int) (((double) (startPoint - movingPoint) * 1440) / (double) (unitSize * BoxSize));
			canvas.drawLine(xpoint, 50, xpoint, 1280 + 600, markerLine);
			Log.i("GESTURE", "ONLONGPRESS : " + String.valueOf(xpoint));
		}
		// FinishMarkerPrinting
		if (finishPoint >= movingPoint
				&& finishPoint < movingPoint + unitSize * BoxSize && ismoving) {
			// canvas.drawLine()
			int xpoint = (int) (((double) (finishPoint - movingPoint) * 1440) / (double) (unitSize * BoxSize));
			canvas.drawLine(xpoint, 50, xpoint, 1280 + 600, markerLine);
			Log.i("GESTURE", "ONLONGPRESS : " + String.valueOf(xpoint));
		}
	}

	private float drawWaveLine(Canvas canvas, double value, float x, float y,
			int height, int margin) {
		float nextY = height * -1 * (float) (value - 1.0) / 2.0f;
		if (count++ == 50) {
			canvas.drawLine(x + margin, y + margin, x + 1 + margin, nextY
					+ margin, waveBaseLine);
			count = 0;
		}
		return nextY;

	}

	@Override
	public byte[] getAllWaveData() {
		return waveData.toByteArray();
	}

	@Override
	public void addWaveData(byte[] data) {
		addWaveData(data, 0, data.length);
	}

	@Override
	public void addWaveData(byte[] data, int offset, int length) {
		waveData.write(data, offset, length);
		if (flag) {
			fireInvalidate();
			flag = false;
		} else {
			flag = true;
		}
	}

	@Override
	public void closeWaveData() {
		byte[] bs = waveData.toByteArray();
		byte[] data = NormalizeWaveData.normalizeWaveData(bs);
		waveData.reset();
		addWaveData(data);
	}

	@Override
	public void clearWaveData() {
		waveData.reset();
		fireInvalidate();
	}

	private void fireInvalidate() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				WaveDisplayView.this.invalidate();
			}
		});
	}

	// Gesture Detector
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (e2.getX() - e1.getX() > 10) {
			Log.i("GESTURE", "FRONT : " + String.valueOf(movingPoint));
			movingPoint = movingPoint - unitSize * BoxSize / 10;
			if (movingPoint < 0)
				movingPoint = 0;
			fireInvalidate();
		} else if (e1.getX() - e2.getX() > 10) {
			Log.i("GESTURE", "REAR : " + String.valueOf(movingPoint));
			movingPoint = movingPoint + unitSize * BoxSize / 10;
			if (movingPoint > waveData.toByteArray().length)
				movingPoint = waveData.toByteArray().length;
			fireInvalidate();
		}
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.i("GESTURE1", "ACTION_POINTER_DOWN");
			oldDist = spacing(event);
			if (oldDist > 50f) {
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			Log.i("GESTURE1", "ACTION_POINTER_UP");
			x = event.getX();
			mode = DRAG;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				Log.i("GESTURE1", "ZOOMING");
				float newDist = spacing(event);
				if (newDist > oldDist) {
					// 커지기
					if (movingPoint + unitSize * BoxSize <= waveData
							.toByteArray().length) {
						BoxSize += 5;
					}
				} else {
					// 작아지기
					if (BoxSize >= 25) {
						BoxSize -= 5;
					}
				}
				fireInvalidate();
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.i("GESTURE1", "ACTION_POINTER_UP");
			mode = NONE;
			break;
		}
		if (mode == DRAG) {
			if (gestureDetector.onTouchEvent(event))
				return true;
			else
				return super.onTouchEvent(event);
		}
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getY(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if (arg0.getX() - arg1.getX() > 120 && Math.abs(arg2) > 30) {
		} else if (arg1.getX() - arg0.getX() > 120 && Math.abs(arg2) > 30) {
		}
		return false;
	}

	public boolean onDown(MotionEvent event) {
		Log.i("GESTURE", "DOWN");
		if (RecordActivity.isTrim) {
			if (!ismoving) {
				ismoving = true;
				movingPoint = cur;
			}
		}
		return true;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		Log.i("GESTURE", "SINGLETAP");
		return true;
	}

	public void onShowPress(MotionEvent e) {
		Log.i("GESTURE", "ONSHOWPRESS");
	}

	public void onLongPress(MotionEvent e) {
		// if (mode != ZOOM) {
		// if (trimParameter == 0) {
		// startPoint = movingPoint
		// + (int) (unitSize * BoxSize * ((double) x / (double) 1440));
		// Log.i("GESTURE",
		// "ONLONGPRESS : " + "STARING POINT : "
		// + String.valueOf(startPoint));
		// trimParameter++;
		// } else if (trimParameter == 1) {
		// finishPoint = movingPoint
		// + (int) (unitSize * BoxSize * ((double) x / (double) 1440));
		// Log.i("GESTURE", "ONLONGPRESS : " + "FINISHING POINT : "
		// + String.valueOf(finishPoint));
		// }
		// fireInvalidate();
		// Log.i("GESTURE", "ONLONGPRESS : " + String.valueOf(x));
		// }
	}

}