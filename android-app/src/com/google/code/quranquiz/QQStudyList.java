package com.google.code.quranquiz;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

public class QQStudyList extends PreferenceActivity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 
    	CheckBoxPreference checkBoxPreference;
        super.onCreate(savedInstanceState);
 
        //add the layout
        addPreferencesFromResource(R.xml.study_list);     
        
        //fetch the item where you wish to insert the CheckBoxPreference, in this case a PreferenceCategory with key "targetCategory"
        PreferenceCategory targetCategory = (PreferenceCategory)findPreference("pref_studyList");

        for(int i=0;i<45;i++){
            //create one check box for each item you need
            checkBoxPreference = new CheckBoxPreference(this);
            //make sure each key is unique  
            checkBoxPreference.setKey("QPart_"+String.valueOf(i));
            checkBoxPreference.setChecked(false); //TODO: Load
            checkBoxPreference.setTitle(" سورة " + QQUtils.sura_name[i]);
            checkBoxPreference.setSummary("الحمد لله"); // TODO: Prev score            
            targetCategory.addPreference(checkBoxPreference);
        }
        for(int i=0;i<5;i++){
            //create one check box for each item you need
            checkBoxPreference = new CheckBoxPreference(this);
            //make sure each key is unique  
            checkBoxPreference.setKey("QPart_j"+String.valueOf(i+26));
            checkBoxPreference.setChecked(false); //TODO: Load
            checkBoxPreference.setTitle(" جزء " + QQUtils.last5_juz_name[i]);
            checkBoxPreference.setSummary("الحمد لله"); // TODO: Prev score            
            targetCategory.addPreference(checkBoxPreference);
        }
 
        //when the user choose other item the description changes too with the selected item
        /*myStudyParts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString());
                return true;    
        }});
        
        */
    }
} 