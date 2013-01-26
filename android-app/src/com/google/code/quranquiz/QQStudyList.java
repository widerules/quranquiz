package com.google.code.quranquiz;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

public class QQStudyList extends PreferenceActivity {
 
	private QQProfileHandler ProfileHandler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 
    	CheckBoxPreference checkBoxPreference;
        super.onCreate(savedInstanceState);
 
        //add the layout
        addPreferencesFromResource(R.xml.study_list);     
        
        //fetch the item where you wish to insert the CheckBoxPreference, in this case a PreferenceCategory with key "targetCategory"
        PreferenceCategory targetCategory = (PreferenceCategory)findPreference("pref_studyList");

        //Get the passed QQProfile
        ProfileHandler = (QQProfileHandler) getIntent().getSerializableExtra("ProfileHandler");
        
        for(int i=0;i<45;i++){
            //create one check box for each item you need
            checkBoxPreference = new CheckBoxPreference(this);
            //make sure each key is unique  
            checkBoxPreference.setKey("QPart_s"+String.valueOf(i+1));
            checkBoxPreference.setTitle(" سورة " + QQUtils.sura_name[i]);
            checkBoxPreference.setSummary(String.valueOf(ProfileHandler.CurrentProfile.getCorrect(i))+" من "
            							  + String.valueOf(ProfileHandler.CurrentProfile.getQuesCount(i))); // TODO: Prev score            
            if(i==0)
            	checkBoxPreference.setEnabled(false);
            targetCategory.addPreference(checkBoxPreference);
        }
        for(int i=0;i<5;i++){
            checkBoxPreference = new CheckBoxPreference(this);
            checkBoxPreference.setKey("QPart_j"+String.valueOf(i+26));
            checkBoxPreference.setTitle(" جزء " + QQUtils.last5_juz_name[i]);
            checkBoxPreference.setSummary(String.valueOf(ProfileHandler.CurrentProfile.getCorrect(45+i))+" من "
					  + String.valueOf(ProfileHandler.CurrentProfile.getQuesCount(45+i))); // TODO: Prev score            
            if(i==4)
            	checkBoxPreference.setEnabled(false);
            targetCategory.addPreference(checkBoxPreference);
        }
     }

    @Override
    protected void onDestroy() {
    	ProfileHandler.reLoadParts(ProfileHandler.CurrentProfile);
    	super.onDestroy();
    }
} 