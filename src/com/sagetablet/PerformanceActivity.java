package com.sagetablet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PerformanceActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.performance);
		
		SAGE communicator = MainActivity.communicator;
		List<Application> appList = communicator.getAppList();
		BufferedReader in = communicator.getIn();
		TextView tv = ((TextView) findViewById(R.id.perf));
		
		/*
		for (Application app : appList) {
			communicator.startPerformance(app.getID());
		}
		
		try {
			String line = in.readLine();
			tv.setText(line);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Application app : appList) {
			communicator.stopPerformance(app.getID());
		}
		*/
	}

}
