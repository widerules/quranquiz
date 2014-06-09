/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.storage;

import android.annotation.SuppressLint;
import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;


 @SuppressLint("NewApi")
public class QQBackupAgent extends BackupAgentHelper {
     // An arbitrary string used within the BackupAgentHelper implementation to
     // identify the SharedPreferencesBackupHelper's data.
     static final String MY_PREFS_BACKUP_KEY = "myprefs";
    @Override
	public void onCreate() {
    	 SharedPreferencesBackupHelper helper =
                 new SharedPreferencesBackupHelper(this, this.getPackageName() + "_preferences");
         addHelper(MY_PREFS_BACKUP_KEY, helper);
    }
}