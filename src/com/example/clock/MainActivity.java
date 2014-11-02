package com.example.clock;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {

	MyAnalogClock clock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		clock = new MyAnalogClock(this);
		setContentView(clock);
	}
}
