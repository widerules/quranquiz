package com.google.code.quranquiz;

import java.util.Date;

public class QQScoreRecord {
	private Date date = null;
	private int score;
	
	public QQScoreRecord(String date, int score){
		this.date = new Date(date);
		this.score = score;
	}
}
