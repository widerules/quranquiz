package net.quranquiz;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QQUtils {

	public static int QQDebug = 0;
	public static int QuranWords = 77878; 
	public static int Juz2AvgWords = QuranWords/30;
	
	private static boolean blFixQ = true;
	
	// Question Count after which the score starts to saturate per Juz2
	public static int Juz2SaturationQCount = 10; 
	
	public static final int[] sura_idx = { 
		30, 6150, 9635, 13386, 16194, 19248, 22572, 23809, 26307, 28144, 
		30065, 31846, 32703, 33537, 34196, 36044, 37604, 39187, 40152, 41491, 
		42664, 43942, 44996, 46316, 47213, 48535, 49690, 51124, 52104, 52925, 
		53475, 53851, 55142, 56029, 56808, 57537, 58402, 59139, 60315, 61538, 
		62336, 63200, 64034, 64384, 64876, 65523, 66066, 66630, 66981, 67358, 
		67722, 68038, 68402, 68748, 69103, 69486, 70064, 70540, 70989, 71341, 
		71566, 71745, 71929, 72174, 72465, 72718, 73055, 73359, 73621, 73842, 
		74072, 74361, 74564, 74823, 74991, 75238, 75423, 75600, 75783, 75920, 
		76028, 76112, 76285, 76396, 76509, 76574, 76650, 76746, 76887, 76973, 
		77031, 77106, 77150, 77181, 77219, 77295, 77329, 77427, 77467, 77511, 
		77551, 77583, 77601, 77638, 77665, 77686, 77715, 77729, 77759, 77782, 
		77809, 77828, 77855 };
	public static final String[] sura_name = { "الفاتحة", "البقرة", "آل عمران",
			"النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة",
			"يونس", "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل",
			"الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج", "المؤمنون",
			"النور", "الفرقان", "الشعراء", "النمل", "القصص", "العنكبوت",
			"الروم", "لقمان", "السجدة", "الأحزاب", "سبأ", "فاطر", "يس",
			"الصافات", "ص", "الزمر", "غافر", "فصلت", "الشورى", "الزخرف",
			"الدخان", "الجاثية", "الأحقاف", "محمد", "الفتح", "الحجرات", "ق",
			"الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة",
			"الحديد", "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة",
			"المنافقون", "التغابن", "الطلاق", "التحريم", "الملك", "القلم",
			"الحاقة", "المعارج", "نوح", "الجن", "المزمل", "المدثر", "القيامة",
			"الإنسان", "المرسلات", "النبأ", "النازعات", "عبس", "التكوير",
			"الانفطار", "المطففين", "الانشقاق", "البروج", "الطارق", "الأعلى",
			"الغاشية", "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح",
			"التين", "العلق", "القدر", "البينة", "الزلزلة", "العاديات",
			"القارعة", "التكاثر", "العصر", "الهمزة", "الفيل", "قريش",
			"الماعون", "الكوثر", "الكافرون", "النصر", "المسد", "الإخلاص",
			"الفلق", "الناس" };

	public static final String[] last5_juz_name = { "الأحقاف", "الذاريات",
			"قد سمع", "تبارك", "عم" };
	/* Here the indexes point to "start-1" to "end" of each Juz' */
	public static final int[] last5_juz_idx = { sura_idx[44], sura_idx[49],
			sura_idx[56], sura_idx[65], sura_idx[76], QuranWords };
	public static final String QQ_MD5_KEY = ""; // Edited only upon release!

	public static int findIdx(int[] scrambled, int i) {
		for (int j = 0; j < scrambled.length; j++)
			if (scrambled[j] == i)
				return j;
		return -1;
	}

	public static List<Integer> ListPlus(List<Integer> diffList, int i) {
		List<Integer> plus = new ArrayList<Integer>();
		plus = diffList;
		for (int j = 0; j < plus.size(); j++)
			plus.set(j, plus.get(j) + i);
		return plus;
	}

	public static String md5(String s) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// e.printStackTrace();
			return s; // Same string better than an empty/null one :)
		}
		digest.update(s.getBytes(), 0, s.length());
		String hash = new BigInteger(1, digest.digest()).toString(16);
		return hash;
	}

	public static int[] randperm(int n) {
		// return a random permutation of size n
		// that is an array containing a random permutation of values 0, 1, ...,
		// n-1
		Random randg = new Random();
		int[] perm = new int[n];
		for (int i = 0; i < n; i++) {
			perm[i] = i;
		}
		for (int i = 0; i < n - 1; i++) {
			int j = randg.nextInt(n - i) + i;
			// sawp perm[i] and perm[j]
			int temp = perm[i];
			perm[i] = perm[j];
			perm[j] = temp;
		}
		return perm;
	}
	
	public static double sCurve(double ratio, double max){
		double y[]={0.001, 0.11, 0.87, 0.98};
		double yp;

	    if (ratio<0.3*max)
	            yp = y[0] + (y[1]-y[0])/(0.3*max-0)*(ratio-0);
	    else if (ratio<0.7*max)
	            yp = y[1] + (y[2]-y[1])/(0.7*max-0.3*max)*(ratio-0.3*max);
	    else if (ratio<max)
	            yp = y[2] + (y[3]-y[2])/(max-0.7*max)*(ratio-0.7*max);
	    else  //(ratio>=max)
	            yp = y[3] + 0.005*(ratio-max);

		return yp;
	}

	public static void disableFixQ(){
		blFixQ = false;
	}
	public static void enableFixQ(){
		blFixQ = true;
	}
	
	/**
	 * Removes tashkeel from Quran text
	 * @param text: Quran text with tashkeel
	 * @return same text with no tashkeel
	 */
	public static String fixQ(String text){
		if (blFixQ)
			return text.replaceAll("[\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652\u06E6]", "");
		else
			return text;
	}

	public static String getSuraName(int wordIdx) {
		return sura_name[getSuraIdx(wordIdx)];
	}

	public static String getSuraNameFromIdx(int suraIdx) {
		return sura_name[suraIdx];
	}
	
	public static int getSuraIdx(int wordIdx) {
		// TODO: Make a binary search, faster!
		for (int i = 0; i < 113; i++)
			if (wordIdx < sura_idx[i]) {
				return i;
			}
		return 113;
	}
}
