package com.google.code.quranquiz;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class QQPreferences extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// add the prefernces.xml layout
		addPreferencesFromResource(R.xml.preferences);

		// get the specified preferences using the key declared in
		// preferences.xml
		ListPreference userLevel = (ListPreference) findPreference("pref_userLevel");

		// get the description from the selected item
		userLevel.setSummary(userLevel.getEntry());

		// when the user choose other item the description changes too with the
		// selected item
		userLevel
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object o) {
						preference.setSummary(o.toString());
						return true;
					}
				});
	}
}