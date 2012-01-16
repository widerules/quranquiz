package com.google.code.quranquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QQUtils {

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
		final int[] sura_idx = {20,23434,232,23,23,23,77799};
		final String[] sura_name = {"الفاتحة","البقرة","آل عمران","النساء",
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
		
		// TODO: Make a binary search, faster!
		for(int i=0;i<114;i++)
			if ( wordIdx < sura_idx[i] ){
				return sura_name[113-i];
			}
		return new String("");
		
	}
}
