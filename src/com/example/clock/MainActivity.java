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
import android.graphics.Paint.Align;
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

	float eventX, eventY = 0;

	int canvasWidth, canvasHeight, clockWidth, clockHeight, clockLeft,
			clockTop, minuteHeight, minuteWidth, minuteLeft, minuteTop,
			hourHeight, hourWidth, hourLeft, hourTop, minuteCenterX,
			minuteCenterY, minuteDegrees, hourDegrees, minsHelper,
			hourHelper = 0;

	int q1, q2, q3, q4 = 0;

	protected void onSaveInstanceState(Bundle outState) {

		outState.putInt("MINUTE_DEGREES", minuteDegrees);
		outState.putInt("HOUR_DEGREES", hourDegrees);

		outState.putInt("MINUTE_HELPER", minsHelper);
		outState.putInt("HOUR_HELPER", hourHelper);

		outState.putInt("MINUTE_CENTER_X", minuteCenterX);
		outState.putInt("MINUTE_CENTER_Y", minuteCenterY);

		outState.putInt("Q1", q1);
		outState.putInt("Q2", q2);
		outState.putInt("Q3", q3);
		outState.putInt("Q4", q4);

		super.onSaveInstanceState(outState);
	}

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			
			eventX = eventY = 0;

			minuteDegrees = savedInstanceState.getInt("MINUTE_DEGREES");
			hourDegrees = savedInstanceState.getInt("HOUR_DEGREES");

			minsHelper = savedInstanceState.getInt("MINUTE_HELPER");
			hourHelper = savedInstanceState.getInt("HOUR_HELPER");

			minuteCenterX = savedInstanceState.getInt("MINUTE_CENTER_X");
			minuteCenterY = savedInstanceState.getInt("MINUTE_CENTER_Y");

			q1 = savedInstanceState.getInt("Q1");
			q2 = savedInstanceState.getInt("Q2");
			q3 = savedInstanceState.getInt("Q3");
			q4 = savedInstanceState.getInt("Q4");
		}

		clock = new MyAnalogClock(this);
		clock.resume();
		clock.setOnTouchListener(this);

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

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			eventX = event.getX();
			eventY = event.getY();
		}

		return true;
	}

	public class MyAnalogClock extends SurfaceView implements Runnable {

		SurfaceHolder holder;

		Matrix minuteMatrix, hourMatrix;

		Thread thread = null;

		boolean isRunning = false;

		Bitmap clock, hour, minute;

		int hourFormat, minsFormat;

		public MyAnalogClock(Context context) {

			super(context);
			holder = getHolder();

			minuteMatrix = new Matrix();
			hourMatrix = new Matrix();
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

				minuteMatrix.reset();
				minuteMatrix.postTranslate(minuteLeft, minuteTop);
				
				hourMatrix.reset();
				hourMatrix.postTranslate(minuteLeft, minuteTop);

				if (eventX != 0 && eventY != 0) {

					minuteDegrees = ((int) Math.toDegrees(Math.atan2(
							minuteCenterY - eventY, minuteCenterX - eventX))) - 90;

					minsHelper = minuteDegrees;

					if (minuteDegrees < 0) {
						minsHelper = minuteDegrees + 360;
					}

					// 1st quarter
					if (minuteDegrees >= 0 && minuteDegrees < 90) {
						q1 = 1;
						if (minuteDegrees > 0) {
							q2 = q3 = 0;
						}
					}
					// 2nd quarter
					if (minuteDegrees >= -270 && minuteDegrees < -180) {
						q2 = 1;
						if (minuteDegrees > -270) {
							q1 = q3 = q4 = 0;
						}
					}
					// 3rd quarter
					if (minuteDegrees >= -180 && minuteDegrees < -90) {
						q3 = 1;
						if (minuteDegrees > -180) {
							q1 = q2 = q4 = 0;
						}
					}
					// 4th quarter
					if (minuteDegrees >= -90 && minuteDegrees < 0) {
						q4 = 1;
						if (minuteDegrees > -90) {
							q2 = q3 = 0;
						}
					}
					
					// Clockwise move
					if (q4 == 1) {
						if (minuteDegrees > -1) {
							if (hourHelper < 24) {
								q4 = 0;
								hourHelper++;
							} else {
								hourHelper = 0;
							}
						}
					}
					
					// Anti-clockwise move
					if (q1 == 1) {
						if (minuteDegrees < 0) {
							if (hourHelper > 0) {
								q4 = 1;
								if (minuteDegrees < 0) {
									q1 = 0;
								}
								hourHelper--;
							} else {
								hourHelper = 24;
							}
						}
					}
					
					// What's the hour?
					hourDegrees = (minsHelper + hourHelper * 360) / 12;
				}

				minuteMatrix.postRotate(minuteDegrees, minuteCenterX,
						minuteCenterY);
				hourMatrix
						.postRotate(hourDegrees, minuteCenterX, minuteCenterY);

				canvas.drawBitmap(hour, hourMatrix, null);
				canvas.drawBitmap(minute, minuteMatrix, null);

				paint.setColor(Color.BLACK);
				paint.setTextSize(20);

				canvas.drawText("eventX: " + (int) eventX, 10, 30, paint);
				canvas.drawText("eventY: " + (int) eventY, 10, 60, paint);

				canvas.drawText("mins degrees: " + minuteDegrees, 10, 100, paint);
				canvas.drawText("hour degrees: " + hourDegrees, 10, 130, paint);

				canvas.drawText("minsHelper: " + minsHelper, 10, 170, paint);
				canvas.drawText("hourHelper: " + hourHelper, 10, 200, paint);

				canvas.drawText("1 " + (q1 == 1 ? "<" : ""), 10, 240, paint);
				canvas.drawText("2 " + (q2 == 1 ? "<" : ""), 10, 270, paint);
				canvas.drawText("3 " + (q3 == 1 ? "<" : ""), 10, 300, paint);
				canvas.drawText("4 " + (q4 == 1 ? "<" : ""), 10, 330, paint);

				if (hourHelper == 24) {
					hourFormat = 0;
				} else {
					hourFormat = hourHelper;
				}

				minsFormat = minsHelper / 6;

				// Display digital clock
				paint.setColor(Color.RED);
				paint.setTextSize(40);
				paint.setTextAlign(Align.RIGHT);

				canvas.drawText(
						String.format("%02d:%02d", hourFormat, minsFormat),
						canvasWidth - 10, 40, paint);

				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
}
