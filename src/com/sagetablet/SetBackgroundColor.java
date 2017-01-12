package com.sagetablet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetBackgroundColor extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setcolor);

		Button btn = ((Button) findViewById(R.id.setcolor));
		btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				EditText r = ((EditText) findViewById(R.id.r));
				EditText g = ((EditText) findViewById(R.id.g));
				EditText b = ((EditText) findViewById(R.id.b));

				String rs = r.getText().toString();
				String rg = g.getText().toString();
				String rb = b.getText().toString();
				
				System.out.println(rs);

				if (rs != "" && rg != "" && rb != "") {
					try {
						int red = Integer.parseInt(rs);
						int green = Integer.parseInt(rg);
						int blue = Integer.parseInt(rb);

						if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
							MainActivity.communicator.changeBackground(red, green, blue);
							finish();
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
