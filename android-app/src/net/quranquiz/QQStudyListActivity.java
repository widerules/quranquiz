/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.google.analytics.tracking.android.EasyTracker;

public class QQStudyListActivity extends SherlockPreferenceActivity{

	private Button toggleSelection;
	private QQProfileHandler ProfileHandler;
	private PreferenceCategory targetCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		CheckBoxPreference checkBoxPreference;
		super.onCreate(savedInstanceState);

		// add the layout
		addPreferencesFromResource(R.xml.study_list);

		((Preference)findPreference("toggle_button_top")).
			setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		                public boolean onPreferenceClick(Preference arg0) { 
		                	ToggleAll();
		                	return true;
		                }
		            });
		
		((Preference)findPreference("toggle_button_bottom")).
		setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	                public boolean onPreferenceClick(Preference arg0) { 
	                	ToggleAll();
	                	return true;
	                }
	            });        
        
		// fetch the item where you wish to insert the CheckBoxPreference, in
		// this case a PreferenceCategory with key "targetCategory"
		targetCategory = (PreferenceCategory) findPreference("pref_studyList");

		// Get the passed QQProfile
		ProfileHandler = (QQProfileHandler) getIntent().getSerializableExtra(
				"ProfileHandler");

		for (int i = 0; i < 45; i++) {
			// create one check box for each item you need
			checkBoxPreference = new CheckBoxPreference(this);
			// make sure each key is unique
			checkBoxPreference.setKey("QPart_s" + String.valueOf(i + 1));
			checkBoxPreference.setTitle(" سورة " + QQUtils.sura_name[i]);
			checkBoxPreference.setSummary( " إجاباتك الصحيحة "
					+ String.valueOf(ProfileHandler.CurrentProfile.getCorrect(i))
					+ " من "
					+ String.valueOf(ProfileHandler.CurrentProfile
							.getQuesCount(i))
					); 
			if (i == 0) {
				checkBoxPreference.setEnabled(false);
				checkBoxPreference.setChecked(true);
			}
			targetCategory.addPreference(checkBoxPreference);
		}
		for (int i = 0; i < 5; i++) {
			checkBoxPreference = new CheckBoxPreference(this);
			checkBoxPreference.setKey("QPart_j" + String.valueOf(i + 26));
			checkBoxPreference.setTitle(" جزء " + QQUtils.last5_juz_name[i]);
			checkBoxPreference.setSummary( " إجاباتك الصحيحة "
					+ String.valueOf(ProfileHandler.CurrentProfile.getCorrect(45 + i))
					+ " من "
					+ String.valueOf(ProfileHandler.CurrentProfile
							.getQuesCount(45 + i))); // TODO: Prev score
			/** Last Juz2 Forced ****
			if (i == 4) {
				checkBoxPreference.setEnabled(false);
				checkBoxPreference.setChecked(true);
			}
			**/
			targetCategory.addPreference(checkBoxPreference);
		}
	}

	public void ToggleAll() {
    	Boolean toggle = !((CheckBoxPreference)targetCategory.getPreference(2)).isChecked();
    	for (int i=1;i<targetCategory.getPreferenceCount()-1;i++){
    		((CheckBoxPreference)targetCategory.getPreference(i)).setChecked(toggle);
    	}
	}

	  @Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance().activityStart(this);
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance().activityStop(this);
	  }
	  
	@Override
	protected void onDestroy() {
		ProfileHandler.reLoadParts(ProfileHandler.CurrentProfile);
		
		if(ProfileHandler.CurrentProfile.getTotalStudyLength()<0.75*QQUtils.Juz2AvgWords){
    		//Select Juz2 3amma (#30)
			((CheckBoxPreference)targetCategory.getPreferenceManager().
					findPreference("QPart_j30")).setChecked(true);
			ProfileHandler.reLoadParts(ProfileHandler.CurrentProfile);
			Toast.makeText(getApplicationContext(), "إختياراتك أقل من جزء: تم اضافة جزء عم", Toast.LENGTH_LONG).show();
			/* Leaks at onDestroy
			new AlertDialog.Builder(this)
				.setTitle(getString(R.string.menuitem_license))
 		   		.setMessage( "Hello" )
 		   		.setPositiveButton(this.getResources().getString(R.string.txt_ok), null)
 		   		.create()
 		   		.show();
 		   	*/
		}
		super.onDestroy();
	}
}