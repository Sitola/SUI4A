package com.sagetablet;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.sagetablet.R; //Nezapomenou importovat vsude

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String SAGE_IP_ADDRESS = "SAGEIPAddress";
	public static final String SAGE_PORT = "SAGEPort";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        
        EditTextPreference textPref = (EditTextPreference) findPreference("SAGEIPAddress");
        textPref.setSummary(textPref.getText());
        
        textPref = (EditTextPreference) findPreference("SAGEPort");
        textPref.setSummary(textPref.getText());
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {    	
        Preference pref = findPreference(key);
        
        if (pref instanceof EditTextPreference) {
        	System.out.println("onSharedPrefChanged");
        	EditTextPreference textPref = (EditTextPreference) pref;
            pref.setSummary(textPref.getText());
        }
    }
}
