package com.google.code.quranquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QQUtils {

	public static int QuranWords = 77797; // TODO: Fix all indeces
	public static final int[] sura_idx = {30,6170,9671,13434,16271,19327,22668,23910,26415,28254,
			30200,31995,32848,33678,34335,36179,37737,39320,40291,41644,
			42818,44097,45149,46468,47364,48684,49843,51281,52259,53076,
			53626,53998,55301,56185,56963,57693,58558,59293,60470,61696,
			62490,63350,64186,64532,65020,65665,66207,66767,67120,67493,
			67853,68165,68525,68867,69219,69598,70173,70648,71095,71447,
			71673,71850,72031,72273,72562,72816,73149,73450,73710,73927,
			74154,74440,74640,74896,75060,75303,75484,75658,75837,75970,
			76074,76155,76324,76432,76541,76602,76674,76766,76905,76987,
			76995,77112,77152,77179,77213,77285,77315,77409,77445,77485,
			77521,77549,77563,77596,77619,77636,77661,77671,77698,77717,
			77740,77755,77778}; 
	public static final String[] sura_name = {"الفاتحة","البقرة","آل عمران","النساء",
			"المائدة","الأنعام","الأعراف","الأنفال","التوبة","يونس","هود","يوسف",
			"الرعد","إبراهيم","الحجر","النحل","الإسراء","الكهف","مريم","طه",
			"الأنبياء","الحج","المؤمنون","النور","الفرقان","الشعراء","النمل",
			"القصص","العنكبوت","الروم","لقمان","السجدة","الأحزاب","سبأ","فاطر",
			"يس","الصافات","ص","الزمر","غافر","فصلت","الشورى","الزخرف","الدخان",
			"الجاثية","الأحقاف","محمد","الفتح","الحجرات","ق","الذاريات","الطور",
			"النجم","القمر","الرحمن","الواقعة","الحديد","المجادلة","الحشر",
			"الممتحنة","الصف","الجمعة","المنافقون","التغابن","الطلاق","التحريم",
			"الملك","القلم","الحاقة","المعارج","نوح","الجن","المزمل","المدثر",
			"القيامة","الإنسان","المرسلات","النبأ","النازعات","عبس","التكوير",
			"الانفطار","المطففين","الانشقاق","البروج","الطارق","الأعلى","الغاشية",
			"الفجر","البلد","الشمس","الليل","الضحى","الشرح","التين","العلق",
			"القدر","البينة","الزلزلة","العاديات","القارعة","التكاثر","العصر",
			"الهمزة","الفيل","قريش","الماعون","الكوثر","الكافرون","النصر",
			"المسد","الإخلاص","الفلق","الناس"};
	
	public static final String[] last5_juz_name = {"الأحقاف","الذاريات","قد سمع","تبارك","عم"};
	/*Here the indexes point to "start-1" to "end" of each Juz'*/
	public static final int[] last5_juz_idx = {sura_idx[44],sura_idx[49],sura_idx[56],sura_idx[65],sura_idx[76],QuranWords};
	
	
	public static List<Integer> ListPlus(List<Integer> diffList, int i) {
		List<Integer> plus = new ArrayList<Integer>();
		plus = diffList;
		for(int j=0;j<plus.size();j++)
			plus.set(j, plus.get(j)+i);
		return plus;
	}
	
	public static int[] randperm(int n){
	// return a random permutation of size n
	// that is an array containing a random permutation of values 0, 1, ..., n-1
		Random randg = new Random();
		int[] perm = new int[n];
		for(int i=0; i < n; i++){
			perm[i] = i;
		}
		for(int i=0; i < n-1; i++){
			int j = randg.nextInt(n-i) + i;
			// sawp perm[i] and perm[j]
			int temp = perm[i];
			perm[i] = perm[j];
			perm[j] = temp;
		}
		return perm;
	}

	public static int findIdx(int[] scrambled, int i) {
		for(int j=0;j<scrambled.length;j++)
			if(scrambled[j]==i)
				return j;
		return -1;
	}
	
	public static String getSuraName(int wordIdx){
		
		// TODO: Make a binary search, faster!
		for(int i=0;i<113;i++)
			if ( wordIdx < sura_idx[i] ){
				return sura_name[i];
			}
		return sura_name[112];
	}
	
}
