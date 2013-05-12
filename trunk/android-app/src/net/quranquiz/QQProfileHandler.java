package net.quranquiz;

import java.io.Serializable;
import java.util.Random;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class QQProfileHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String MY_PROFILE = "MyQQProfile";
	private transient static Context myContext;
	public static final String DEFAULT_STUDY_PARTS = "1,"
			+ String.valueOf(QQUtils.sura_idx[0]) + ",0,0,1.0;"
			+ String.valueOf(QQUtils.sura_idx[0] + 1) + ","
			+ String.valueOf(QQUtils.sura_idx[1] - QQUtils.sura_idx[0])
			+ ",0,0,1.0;";

	public QQProfile CurrentProfile;

	public QQProfileHandler(Context context) {
		myContext = context;
	}

	private boolean checkLastProfile() {
		// Check if a profile exists
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		return settings.contains("lastSeed");
	}

	public String getHashedUID() {
		String[] uid = new String[]{"0","0","0","0","0"};
		/* Get Google account */
		AccountManager manager = (AccountManager) myContext
				.getSystemService(android.content.Context.ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();
		if (list.length > 0) {
			for(int i=0;i<list.length;i++){ //Gl-Fb-Tw-Ap
				if(list[i].type.toLowerCase().contains("google"))
					uid[0] = list[i].name;
				else if(list[i].type.toLowerCase().contains("facebook"))
					uid[1] = list[i].name;
				else if(list[i].type.toLowerCase().contains("twitter"))
					uid[2] = list[i].name;
				else if(list[i].type.toLowerCase().contains("itunes"))
					uid[3] = list[i].name;
				else
					uid[4] = list[i].type+":"+list[i].name;
			}
			
		} else {
			/* If no account, get Phone ID */
			uid[4] = ((TelephonyManager) myContext
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			if (uid[4].length() < 1) {
				/* If not a phone, get Wifi MAC */
				uid[4] = ((WifiManager) myContext
						.getSystemService(Context.WIFI_SERVICE))
						.getConnectionInfo().getMacAddress();
				if (uid[4].length() < 1) {
					/* If off-line, get collide-able string */
					uid[4] = "35"
							+ // we make this look like a valid IMEI
							Build.BOARD.length() % 10 + Build.BRAND.length()
							% 10 + Build.CPU_ABI.length() % 10
							+ Build.DEVICE.length() % 10
							+ Build.DISPLAY.length() % 10 + Build.HOST.length()
							% 10 + Build.ID.length() % 10
							+ Build.MANUFACTURER.length() % 10
							+ Build.MODEL.length() % 10
							+ Build.PRODUCT.length() % 10 + Build.TAGS.length()
							% 10 + Build.TYPE.length() % 10
							+ Build.USER.length() % 10; // 13 digits
				}
			}
		}

		for(int i=0;i<5;i++)
			if (uid[i] != "0")
				uid[i] = QQUtils.md5(uid[i]);
		return uid[0]+"+"+uid[1]+"+"+uid[2]+"+"+uid[3]+"+"+uid[4];
	}

	private QQProfile getLastProfile() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(myContext);

		// Note: Pref entries from xml are strings!
		// manually inserted via editor are integers

		return new QQProfile(settings.getString("pref_uid", ""),
				settings.getInt("lastSeed", 0), Integer.parseInt(settings
						.getString("pref_userLevel", "")),
				// settings.getInt("score", 0),
				// settings.getInt("quesCount", 0),
				settings.getString("studyParts", ""),
				settings.getString("pref_scores", ""),
				settings.getInt("specialScore", 0));
	}

	public QQProfile getProfile() {
		QQProfile myQQProfile;

		if (checkLastProfile()) { // Found a previously saved profile
			myQQProfile = getLastProfile();
		} else { // Create a new profile with a random start
					// Toast.makeText(myContext, "Created a new profile!",
					// Toast.LENGTH_LONG).show();
			myQQProfile = new QQProfile(getHashedUID(),
					new Random().nextInt(QQUtils.QuranWords), 1,
					QQProfileHandler.DEFAULT_STUDY_PARTS,
					QQScoreRecord.getInitScoreRecordPack(),
					0);
			reLoadParts(myQQProfile);
			saveProfile(myQQProfile);
		}
		CurrentProfile = myQQProfile;
		return myQQProfile;
	}

	public void reLoadCurrentProfile() {
		CurrentProfile = getLastProfile();
	}

	public void reLoadParts(QQProfile profile) {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		int checked, start, end;
		String newParts = new String("");

		// Al-Fatiha is always enabled
		newParts = "1," + String.valueOf(QQUtils.sura_idx[0] - 1) + ","
				+ String.valueOf(profile.getCorrect(0)) + ","
				+ String.valueOf(profile.getQuesCount(0)) + ","
				+ String.valueOf(profile.getAvgLevel(0)) + ";";

		for (int i = 1; i < 45; i++) {
			checked = (settings.getBoolean("QPart_s" + String.valueOf(i + 1),
					false)) ? 1 : -1;
			start = QQUtils.sura_idx[i - 1];
			end = QQUtils.sura_idx[i] - 1;
			newParts += String.valueOf(start * checked) + ","
					+ String.valueOf(end - start) + ","
					+ String.valueOf(profile.getCorrect(i)) + ","
					+ String.valueOf(profile.getQuesCount(i)) + ","
					+ String.valueOf(profile.getAvgLevel(i)) + ";";
		}
		for (int i = 0; i < 4; i++) {
			checked = (settings.getBoolean("QPart_j" + String.valueOf(i + 26),
					false)) ? 1 : -1;
			start = QQUtils.last5_juz_idx[i];
			end = QQUtils.last5_juz_idx[i + 1] - 1;
			newParts += String.valueOf(start * checked) + ","
					+ String.valueOf(end - start) + ","
					+ String.valueOf(profile.getCorrect(i + 45)) + ","
					+ String.valueOf(profile.getQuesCount(i + 45)) + ","
					+ String.valueOf(profile.getAvgLevel(i + 45)) + ";";
		}
		start = QQUtils.last5_juz_idx[4];
		end = QQUtils.last5_juz_idx[5] - 1;
		// Juz' 3amma is always enabled
		newParts += String.valueOf(start) + "," + String.valueOf(end - start)
				+ "," + String.valueOf(profile.getCorrect(49)) + ","
				+ String.valueOf(profile.getQuesCount(49)) + ","
				+ String.valueOf(profile.getAvgLevel(49))+ ";";

		profile.setStudyParts(newParts);
		saveProfile(profile);
	}

	public void saveProfile(QQProfile prof) {

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("pref_uid", prof.getuid());
		editor.putInt("lastSeed", prof.getLastSeed());
		editor.putString("pref_userLevel", Integer.toString(prof.getLevel()));
		// editor.putInt("score", prof.getCorrect());
		// editor.putInt("quesCount", prof.getQuesCount());
		editor.putString("studyParts", prof.getStudyParts());
		editor.putString("pref_scores", prof.getScores());
		editor.putInt("specialScore", prof.getSpecialScore());

		editor.commit();

		CurrentProfile = prof;
	}

}
