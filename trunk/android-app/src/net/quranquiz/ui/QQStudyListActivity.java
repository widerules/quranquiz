/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.ui;

import net.quranquiz.R;
import net.quranquiz.storage.QQProfileHandler;
import net.quranquiz.util.QQUtils;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.google.analytics.tracking.android.EasyTracker;

public class QQStudyListActivity extends SherlockPreferenceActivity{

	private QQProfileHandler ProfileHandler;
	private PreferenceCategory targetCategory;
	private int tot, corr;

	@SuppressLint("NewApi")
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
		ProfileHandler.reLoadParts(ProfileHandler.CurrentProfile);

		for (int i = 0; i < 45; i++) {
			// create one check box for each item you need
			// make sure each key is unique
			corr = ProfileHandler.CurrentProfile.getCorrect(i);
			tot  = ProfileHandler.CurrentProfile.getQuesCount(i);
			
			checkBoxPreference = new CheckBoxPreference(this);
			checkBoxPreference.setKey("QPart_s" + String.valueOf(i + 1));
			checkBoxPreference.setTitle(" سورة " + QQUtils.sura_name[i]);
			checkBoxPreference.setSummary( " إجاباتك الصحيحة "
					+ String.valueOf(corr)
					+ " من "
					+ String.valueOf(tot)
					); 
			
			if(android.os.Build.VERSION.SDK_INT 
					>= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
				if(tot == 0)
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_grey));
				else if(((double)corr/tot)>= 0.8)
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_green));
				else if(((double)corr/tot)>= 0.5)
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_yellow));
				else 
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_red));
			}

			if (i == 0) {
				checkBoxPreference.setEnabled(false);
				checkBoxPreference.setChecked(true);
			}
			targetCategory.addPreference(checkBoxPreference);
		}
		for (int i = 0; i < 5; i++) {
			// create one check box for each item you need
			// make sure each key is unique
			corr = ProfileHandler.CurrentProfile.getCorrect(45 + i);
			tot  = ProfileHandler.CurrentProfile.getQuesCount(45 + i);
			
			checkBoxPreference = new CheckBoxPreference(this);
			checkBoxPreference.setKey("QPart_j" + String.valueOf(i + 26));
			checkBoxPreference.setTitle(" جزء " + QQUtils.last5_juz_name[i]);
			checkBoxPreference.setSummary( " إجاباتك الصحيحة "
					+ String.valueOf(corr)
					+ " من "
					+ String.valueOf(tot)); 

			if(android.os.Build.VERSION.SDK_INT 
					>= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
				if(tot == 0)
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_grey));
				else if(((double)corr/tot)>= 0.8)
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_green));
				else if(((double)corr/tot)>= 0.5)
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_yellow));
				else 
					checkBoxPreference.setIcon(getResources().getDrawable(R.drawable.smiley_red));
			}
			targetCategory.addPreference(checkBoxPreference);
		}
	}

	public void ToggleAll() {
    	Boolean toggle = !((CheckBoxPreference)targetCategory.getPreference(2)).isChecked();
    	for (int i=1;i<targetCategory.getPreferenceCount();i++){ //Alfatiha is always selected
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