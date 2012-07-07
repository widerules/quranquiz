package com.google.code.quranquiz;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class QQProfileHandler {
    public static final String MY_PROFILE = "MyQQProfile";
    private static Context myContext;
    
    public QQProfileHandler(Context context) {
    	 myContext = context;
    }	
    
    public void saveProfile(QQProfile prof){

    	SharedPreferences settings = myContext.getSharedPreferences(MY_PROFILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("lastSeed", prof.getLastSeed());
        editor.putInt("level", prof.getLevel());
        editor.putInt("score", prof.getScore());     
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
	    SharedPreferences settings = myContext.getSharedPreferences(MY_PROFILE, 0);
	    
		return new QQProfile(settings.getInt("lastSeed", 0),
							 settings.getInt("level", 0),
							 settings.getInt("score", 0),
							 settings.getInt("quesCount", 0) );

	}

	private boolean checkLastProfile(){
		// Check if a profile exists
	    SharedPreferences settings = myContext.getSharedPreferences(MY_PROFILE, 0);
	    return settings.contains("lastSeed");
	}
}
