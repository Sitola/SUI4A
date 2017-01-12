package com.sagetablet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FileChooserActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FileChooserDrawView drawView = new FileChooserDrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);
    	
        
        drawView.invalidate();
	}

}
