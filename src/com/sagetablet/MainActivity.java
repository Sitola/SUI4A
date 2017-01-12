package com.sagetablet;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.Random;
import java.util.prefs.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.conn.HttpHostConnectException;

//import sage.cz.DrawView;

import com.sagetablet.android.XMLRPCClient;
import com.sagetablet.android.XMLRPCException;
import com.sagetablet.android.XMLRPCFault;
import com.sagetablet.android.XMLRPCSerializable;

import com.sagetablet.R;

public class MainActivity extends Activity {
	static int appID = 0;
	PrintWriter out = null;
	Socket socket = null;
	Socket socketApp = null;

	private DrawView drawView;
	// Communicator communicator = null;
	public static SAGE communicator;
	
	public static SharedPreferences sharedPref;

	// private boolean badStart = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preferences:
			Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
			startActivity(settingsActivity);
			return true;
		case R.id.set_color:
			Intent SetBackgroundColor = new Intent(getBaseContext(), SetBackgroundColor.class);
			startActivity(SetBackgroundColor);
			return true;
		case R.id.performance:
			/*
			Intent Performance = new Intent(getBaseContext(), PerformanceActivity.class);
			startActivity(Performance);
			*/
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String sageIP = sharedPref.getString(SettingsActivity.SAGE_IP_ADDRESS, "");
		String port = sharedPref.getString(SettingsActivity.SAGE_PORT, "");
		
		
		if (!Helper.checkIPAndPort(sageIP, port) || Helper.badStart) {
			setContentView(R.layout.startconfig);
			if (Helper.badStart) {				
				TextView tv = ((TextView) findViewById(R.id.tvCouldnotConnect));				
				tv.setVisibility(View.VISIBLE);
			} else {
				TextView tv = ((TextView) findViewById(R.id.tvCouldnotConnect));				
				tv.setVisibility(View.GONE);					
			}			
			EditText ip = ((EditText) findViewById(R.id.SAGEIP));
			EditText p = ((EditText) findViewById(R.id.SAGEPORT));
			ip.setText(sageIP);
			if (port.equals("")) {
				p.setText("20001");
			} else {
			   p.setText(port);
			}
			Button btn = ((Button) findViewById(R.id.connecttosage));
			btn.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Helper.badStart = false;

					EditText ip = ((EditText) findViewById(R.id.SAGEIP));
					EditText port = ((EditText) findViewById(R.id.SAGEPORT));

					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("SAGEIPAddress", ip.getText().toString());
					editor.putString("SAGEPort", port.getText().toString());
					editor.commit();

					Intent intent = getIntent();
					finish();
					startActivity(intent);
				}
			});
		} else {
			boolean cont = true;
			try {
				// Starts the SAGe thread that connects to SAGE and ensures all communication between UI and SAGE
				Helper.badStart = false;
				System.out.println("Pred communicatorem");
				communicator = new SAGE(sageIP, Integer.valueOf(port));
			} catch (Exception e) {
				System.out.println("Chytl jsem vyjimku");
				Helper.badStart = true;
				Intent intent = getIntent();
				finish();
				startActivity(intent);
				cont = false;
				e.printStackTrace();
			}
			if (cont) {
				System.out.println("Pred sleepem");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Starts the drawView activity which ensures all the usage of GUI
				System.out.println("Pred drawView");
				if (communicator == null) {
					System.out.println("Communicator je null");
				}
				drawView = new DrawView(this, communicator);
				drawView.setBackgroundColor(Color.WHITE);
				setContentView(drawView);

				drawView.invalidate();
			}
		}

		// commented code if starting an application is possibly needed
		/*
		 * 
		 * System.out.println("Before XML");
		 * 
		 * XMLRPCClient client = new XMLRPCClient("http://147.251.54.71:19010");
		 * 
		 * System.out.println("XML connected  ");
		 * 
		 * 
		 * try { Object s = client.call("getAppList"); s = client.call("startDefaultApp","atlantis","147.251.54.71",20002,false,"default",false,false,""); String p = s.toString(); System.out.println (p); } catch (XMLRPCException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 * 
		 * try { Thread.sleep(3200); } catch (InterruptedException e) { e.printStackTrace(); }
		 * 
		 * 
		 * System.out.println("XML  used");
		 */

		System.out.println("Ending onCreate");
	}

	protected void onStart() {
		super.onStart();

	}

	protected void onResume() {
		super.onResume();

		System.out.println("Jsem v onResume");

	}
}