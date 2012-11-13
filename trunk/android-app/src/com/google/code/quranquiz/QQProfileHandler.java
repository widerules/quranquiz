package com.google.code.quranquiz;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QQProfileHandler {
    public static final String MY_PROFILE = "MyQQProfile";
    private static Context myContext;
    
    public QQProfileHandler(Context context) {
    	 myContext = context;
    }	
    
    public void saveProfile(QQProfile prof){

    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("lastSeed", prof.getLastSeed());
        editor.putString("pref_userLevel", Integer.toString(prof.getLevel()));
        editor.putInt("score", prof.getCorrect());     
        editor.putInt("quesCount", prof.getQuesCount());

        editor.commit();
				 
    }
    
	public QQProfile getProfile(){
		QQProfile myQQProfile;
		
		if (checkLastProfile()){ // Found a previously saved profile
			//Toast.makeText(myContext, "Loaded Your profile!", Toast.LENGTH_LONG).show();
			return getLastProfile();
		}else{ // Create a new profile with a random start
			//Toast.makeText(myContext, "Created a new profile!", Toast.LENGTH_LONG).show();
			myQQProfile = new QQProfile(new Random().nextInt(QQUtils.QuranWords), 1, 0, 0 );
			saveProfile(myQQProfile);
			return myQQProfile;
		}
	}
	
	private QQProfile getLastProfile() {
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myContext);
	    
	    // Note: Pref entries from xml are strings!
	    // manually inserted via editor are integers 
	    
		return new QQProfile(settings.getInt("lastSeed", 0),
							 Integer.parseInt(settings.getString("pref_userLevel", "")),
							 settings.getInt("score", 0),
							 settings.getInt("quesCount", 0) );

	}

	private boolean checkLastProfile(){
		// Check if a profile exists
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myContext);
	    return settings.contains("lastSeed");
	}
}
