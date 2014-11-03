package com.example.clock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
		clock.setOnTouchListener(this);
		
		x = 0;
		y = 0;
		
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

		Thread thread = null;

		boolean isRunning = false;

		Bitmap clock, hour, minute;

		int canvasWidth, canvasHeight, clockWidth, clockHeight, clockLeft,
				clockTop, minuteHeight, minuteWidth, minuteLeft, minuteTop,
				hourHeight, hourWidth, hourLeft, hourTop;

		public MyAnalogClock(Context context) {
			
			super(context);
			holder = getHolder();
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
				
				if (x != 0 && y != 0) {
					
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

					canvas.drawBitmap(clock, clockLeft, clockTop, null);
					canvas.drawBitmap(hour, hourLeft, hourTop, null);
					canvas.drawBitmap(minute, minuteLeft, minuteTop, null);
				}
				
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
}
