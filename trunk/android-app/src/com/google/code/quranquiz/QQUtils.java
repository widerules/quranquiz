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
}
