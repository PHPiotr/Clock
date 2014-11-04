package com.example.clock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity implements OnTouchListener {

	MyAnalogClock clock;

	float x, y;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		clock = new MyAnalogClock(this);
		clock.resume();
		clock.setOnTouchListener(this);

		x = y = 0;

		setContentView(clock);
	}

	@Override
	protected void onResume() {

		super.onResume();
		clock.resume();
	}

	@Override
	protected void onPause() {

		super.onPause();
		clock.pause();
	}

	public boolean onTouch(View v, MotionEvent event) {

		x = event.getX();
		y = event.getY();
		
		return true;
	}

	public class MyAnalogClock extends SurfaceView implements Runnable {

		SurfaceHolder holder;

		Matrix matrix;

		Thread thread = null;

		boolean isRunning = false;

		Bitmap clock, hour, minute;

		int canvasWidth, canvasHeight, clockWidth, clockHeight, clockLeft,
				clockTop, minuteHeight, minuteWidth, minuteLeft, minuteTop,
				hourHeight, hourWidth, hourLeft, hourTop, minuteCenterX,
				minuteCenterY, degrees;

		public MyAnalogClock(Context context) {

			super(context);
			holder = getHolder();

			matrix = new Matrix();
		}

		public void pause() {

			isRunning = false;

			while (true) {

				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			thread = null;
		}

		public void resume() {

			isRunning = true;

			thread = new Thread(this);
			thread.start();
		}

		@Override
		public void run() {

			while (isRunning) {

				if (!holder.getSurface().isValid()) {
					continue;
				}

				Canvas canvas = holder.lockCanvas();

				Paint paint = new Paint();
				paint.setColor(Color.WHITE);
				paint.setStyle(Style.FILL);
				canvas.drawPaint(paint);

				clock = BitmapFactory.decodeResource(getResources(),
						R.drawable.clock_dial);
				hour = BitmapFactory.decodeResource(getResources(),
						R.drawable.clock_hour);
				minute = BitmapFactory.decodeResource(getResources(),
						R.drawable.clock_minute);

				// canvas.get... does not work!
				canvasWidth = getWidth();
				canvasHeight = getHeight();

				// height is not necessary as this is a square clock :)
				clockWidth = clock.getWidth();

				hourHeight = hour.getHeight();
				hourWidth = hour.getWidth();

				minuteHeight = minute.getHeight();
				minuteWidth = minute.getWidth();

				clockLeft = (canvasWidth - clockWidth) / 2;
				clockTop = (canvasHeight - clockWidth) / 2;

				hourLeft = (canvasWidth - hourWidth) / 2;
				hourTop = (canvasHeight - hourHeight) / 2;

				minuteLeft = (canvasWidth - minuteWidth) / 2;
				minuteTop = (canvasHeight - minuteHeight) / 2;

				minuteCenterX = minuteLeft + minuteWidth / 2;
				minuteCenterY = minuteTop + minuteHeight / 2;

				canvas.drawBitmap(clock, clockLeft, clockTop, null);
				canvas.drawBitmap(hour, hourLeft, hourTop, null);

				matrix.reset();
				matrix.postTranslate(minuteLeft, minuteTop);

				if (x == 0 && y == 0) {

					degrees = 0;
				}

				if (x != 0 && y != 0) {

					degrees = ((int) Math.toDegrees(Math.atan2(minuteCenterY
							- y, minuteCenterX - x))) - 90;
				}

				matrix.postRotate(degrees, minuteCenterX, minuteCenterY);

				canvas.drawBitmap(minute, matrix, null);

				paint.setColor(Color.BLACK);
				paint.setTextSize(20);
				canvas.drawText("" + degrees, 10, 25, paint);

				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
}
