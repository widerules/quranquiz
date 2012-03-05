package com.google.code.quranquiz;

import java.util.Random;

public class QQProfileHandler {
	
	public static QQProfile getProfile(){
		if (checkLastProfile())
			return getLastProfile();
		else
			return new QQProfile(new Random().nextInt(QQUtils.QuranWords), 1, 0, 0 );
	}
	
	private static QQProfile getLastProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	private static boolean checkLastProfile(){
		//Do real try
		// work ...
		if(3>2){
			return false;
		} else {
			return true;
		}
	}
}
