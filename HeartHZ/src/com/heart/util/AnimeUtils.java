/**
 * Animated GIF utility class
 */
package com.heart.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class AnimeUtils extends Activity {

	static public void startViewAnimation(View view, boolean start) {
		if (view != null) {
			// background drawable
			startDrawableAnimation(view.getBackground(), start);
			if (view instanceof ImageView) {
				// image drawable
				startDrawableAnimation(((ImageView) view).getDrawable(), start);
			}
		}
	}

	static public void startDrawableAnimation(Drawable d, boolean start) {
		if (d instanceof AnimationDrawable) {
			if (start) {
				((AnimationDrawable) d).start();
			} else {
				((AnimationDrawable) d).stop();

			}
		}
	}

	static public Drawable loadDrawableFromResource(Resources rsrc, int resid) {
		// load from resource
		Movie movie = Movie.decodeStream(rsrc.openRawResource(resid));
		if ((movie != null) && movie.duration() > 0) {
			return makeMovieDrawable(rsrc, movie);
		} else {
			// not animated GIF
			return rsrc.getDrawable(resid);
		}
	}

	static private Drawable makeMovieDrawable(Resources rsrc, Movie movie) {
		int duration = movie.duration();
		int width = movie.width(), height = movie.height();

		Log.d("Tag", duration + "");
		AnimationDrawable result = new AnimationDrawable();
		result.setOneShot(false); // for loop

		Drawable frame = null;
		int start = 0;
		for (int time = 0; time < duration; time += 20) {
			if (movie.setTime(time)) {
				if (frame != null) {
					// add previous frame
					result.addFrame(frame, time - start);
				}

				// make frame
				Bitmap bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_4444);// low quality
				// Bitmap.Config.RGB_565 // save heap
				movie.draw(new Canvas(bitmap), 0, 0);
				frame = new BitmapDrawable(rsrc, bitmap);
				start = time;
			}
		}

		if (frame != null) {
			// add last frame
			result.addFrame(frame, duration - start);
		}
		return result;
	}
}
