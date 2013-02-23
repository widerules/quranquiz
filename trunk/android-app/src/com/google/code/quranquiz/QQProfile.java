package com.google.code.quranquiz;

import java.io.Serializable;
import java.util.Vector;

public class QQProfile implements Serializable {

	private static final long serialVersionUID = 21L;
	private String uid;
	private int lastSeed; // Seed for the Question
	private int level; // TODO: Encapsulate
	private Vector<QQStudyPart> QParts;
	private Vector<QQScoreRecord> QScores;

	public QQProfile(int lastSeed, int level) {
		setLastSeed(lastSeed);
		setLevel(level);
	}

	public QQProfile(String uid, int lastSeed, int level, String QPartsString,
			String QScoresString) {

		setLastSeed(lastSeed);
		setLevel(level);
		setStudyParts(QPartsString);
		setScoreHistory(QScoresString);
		setuid(uid);
	}

	public void addCorrect(int currentPart) {
		if (currentPart < QParts.size()) {
			QParts.get(currentPart).addCorrect();
		}
	}

	public void addIncorrect(int currentPart) {
		if (currentPart < QParts.size()) {
			QParts.get(currentPart).addIncorrect();
		}
	}

	public double getAvgLevel() {
		// TODO Auto-generated method stub
		return 1.5;
	}

	public int getCorrect(int part) {
		if (part < QParts.size()) {
			return QParts.get(part).getNumCorrect();
		} else {
			return 0;
		}
	}

	public int getLastSeed() {
		return lastSeed;
	}

	public int getLevel() {
		return level;
	}

	public int getQuesCount(int part) {
		if (part < QParts.size()) {
			return QParts.get(part).getNumQuestions();
		} else {
			return 0;
		}
	}

	public int getScore() {
		int studyCount = this.getTotalStudyLength();
		int correct = this.getTotalCorrect();
		int total = this.getTotalQuesCount();

		return (int) Math.ceil(3000
				* ((double) studyCount / QQUtils.QuranWords) * 0.5
				* (1 + Math.tanh(5 * (double) correct / total - 2.5)));
	}

	public String getScores() {
		String tokens = "";
		for (int i = 0; i < QScores.size(); i++) {
			tokens += QScores.get(i).packedString();
			if (i < QScores.size() - 1)
				tokens += ";"; // Skip ; after the last token
		}
		return tokens;
	}

	public QQSparseResult getSparsePoint(int CntTot) {
		int Length = 0, i, pLength;
		for (i = 0; i < QParts.size(); i++) {
			pLength = QParts.get(i).getLength();
			if (CntTot < Length + pLength) {
				return new QQSparseResult(QParts.get(i).getStart() + CntTot
						- Length, i);
			} else {
				Length += pLength;
			}
		}
		return new QQSparseResult(QParts.get(i).getStart(), i);
	}

	public String getStudyParts() {
		String tokens = "";
		QQStudyPart currentPart;
		for (int i = 0; i < QParts.size(); i++) {
			currentPart = QParts.get(i);
			tokens += String.valueOf(currentPart.getStart()) + ",";
			tokens += String.valueOf(currentPart.getLength()) + ",";
			tokens += String.valueOf(currentPart.getNumCorrect()) + ",";
			tokens += String.valueOf(currentPart.getNumQuestions());
			if (i < QParts.size() - 1)
				tokens += ";"; // Skip ; after the last token
		}
		return tokens;
	}

	public int getTotalCorrect() {
		int Tot = 0;
		QQStudyPart QPart;
		for (int i = 0; i < QParts.size(); i++) {
			QPart = QParts.get(i);
			if (QPart.getStart() > 0)
				Tot += QPart.getNumCorrect();
		}
		return Tot;
	}

	public int getTotalQuesCount() {
		int Tot = 0;
		QQStudyPart QPart;
		for (int i = 0; i < QParts.size(); i++) {
			QPart = QParts.get(i);
			if (QPart.getStart() > 0)
				Tot += QPart.getNumQuestions();
		}
		return Tot;
	}

	public int getTotalStudyLength() {
		int Length = 0;
		QQStudyPart QPart;
		for (int i = 0; i < QParts.size(); i++) {
			QPart = QParts.get(i);
			if (QPart.getStart() > 0)
				Length += QPart.getLength();
		}
		return Length;
	}

	public String getuid() {
		return uid;
	}

	public void setLastSeed(int lastSeed) {
		this.lastSeed = lastSeed;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	private void setScoreHistory(String QScoresString) {
		QScores = new Vector<QQScoreRecord>();
		for (String token : QScoresString.split(";")) {
			QScores.add(new QQScoreRecord(token));
		}
	}

	public void setStudyParts(String QPartsString) {
		String[] partElements;

		QParts = new Vector<QQStudyPart>();
		for (String token : QPartsString.split(";")) {
			partElements = token.split(",");
			QParts.add(new QQStudyPart(Integer.parseInt(partElements[0]),
					Integer.parseInt(partElements[1]), Integer
							.parseInt(partElements[2]), Integer
							.parseInt(partElements[3])));
		}
	}

	public void setuid(String id) {
		uid = id;
	}

	public boolean updateScoreRecord() {
		if (QScores.size() < 1) {
			QScores.add(new QQScoreRecord(this.getScore()));
			return true;
		} else if (QScores.get(QScores.size() - 1).isOlderThan1Day()) {
			QScores.add(new QQScoreRecord(this.getScore()));
			return true;
		}
		return false;
	}
}
