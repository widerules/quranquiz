/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class QQScoreRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String getInitScoreRecordPack() {
		return DateFormat.getDateTimeInstance().format(
				Calendar.getInstance().getTime())
				+ "|0";
	}

	private String date = "";

	private int score = 0;

	public QQScoreRecord(int score) {
		this.date = DateFormat.getDateTimeInstance().format(
				Calendar.getInstance().getTime());
		this.score = score;
	}

	public QQScoreRecord(String packed) {
		String[] tokens;
		tokens = packed.split("\\|");
		this.date = tokens[0];
		this.score = Integer.valueOf(tokens[1]);
	}

	public QQScoreRecord(String date, int score) {
		this.date = date;
		this.score = score;

	}

	public Date getDate() {
		DateFormat df = DateFormat.getDateInstance();

		try {
			return df.parse(this.date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		return new Date();
	}

	public int getScore() {
		return this.score;
	}

	public boolean isOlderThan1Day() {
		long a, b; // TODO: Check!
		a = Calendar.getInstance().getTime().getTime();
		b = Date.parse(date);
		if (a - b > 1000 * 60 * 60 * 24)
			return true;
		else
			return false;
	}

	public String packedString() {
		return date + "|" + String.valueOf(score);
	}
}
