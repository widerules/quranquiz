/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.util;

import net.quranquiz.model.ViewModel;
import android.app.Application;
import android.content.Context;

public class QQApp extends Application{

    private static Context mContext;
    private static ViewModel mViewModel; 

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mViewModel = new ViewModel();
    }

    public static Context getContext(){
        return mContext;
    }

	public static ViewModel getViewModel() {
		return mViewModel;
	}
}
