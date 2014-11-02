package com.example.clock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class MyAnalogClock extends View {

	Bitmap clock, hour, minute;

	int canvasWidth, canvasHeight, clockWidth, clockHeight, clockLeft,
			clockTop, minuteHeight, minuteWidth, minuteLeft, minuteTop,
			hourHeight, hourWidth, hourLeft, hourTop;

	public MyAnalogClock(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		clock = BitmapFactory.decodeResource(getResources(),
				R.drawable.clock_dial);
		hour = BitmapFactory.decodeResource(getResources(),
				R.drawable.clock_hour);
		minute = BitmapFactory.decodeResource(getResources(),
				R.drawable.clock_minute);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

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
}