/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.ui;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter{

    PreferenceListFragment[] fragments;
    String[] titles;
    
    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
     /*   fragments = new PreferenceListFragment[4];
        fragments[0] = new PreferenceListFragment(R.xml.settings);
        fragments[1] = new PreferenceListFragment(R.xml.widget_settings);
        fragments[2] = new PreferenceListFragment(R.xml.s_widget_settings);
        fragments[3] = new PreferenceListFragment(R.xml.color_settings);
        
        titles = new String[4];
        titles[0] = context.getString(R.string.main_settings);
        titles[1] = context.getString(R.string.widget_settings);
        titles[2] = context.getString(R.string.s_widget_settings);
        titles[3] = context.getString(R.string.color_settings_main);
       */ 
    }

    @Override
    public Fragment getItem(int position){
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}