package com.google.code.quranquiz;

import java.io.Serializable;
import java.util.Vector;

public class QQProfile implements Serializable {

	private static final long serialVersionUID = 21L;
	private String uid;
	private int lastSeed; // Seed for the Question
	private int level;
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
			QParts.get(currentPart).addCorrect(level);
		}
	}

	public void addIncorrect(int currentPart) {
		if (currentPart < QParts.size()) {
			QParts.get(currentPart).addIncorrect();
		}
	}

	public double getAvgLevel(int part) {
		if (part < QParts.size()) {
			return QParts.get(part).getAvgLevel();
		} else {
			return 1.0;
		}	
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
		
		double score=0.0;
		double partWeight,scaledQCount,avgLevel,scaledCorrectRatio;
		for(int i=0;i<QParts.size();i++){
			partWeight   = QParts.get(i).getNonZeroLength();
			partWeight	/= QQUtils.Juz2AvgWords;
			avgLevel     = QParts.get(i).getAvgLevel();
			scaledQCount = QQUtils.sCurve(QParts.get(i).getNumQuestions(),
										  QQUtils.Juz2SaturationQCount*partWeight);
			scaledCorrectRatio = QQUtils.sCurve(QParts.get(i).getCorrectRatio(),1);
			
			score += 100*partWeight*avgLevel*scaledQCount*scaledCorrectRatio;
		}
		return (int) Math.ceil(score);
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
			tokens += String.valueOf(currentPart.getNonZeroLength()) + ",";
			tokens += String.valueOf(currentPart.getNumCorrect()) + ",";
			tokens += String.valueOf(currentPart.getNumQuestions()) + ",";
			tokens += String.valueOf(currentPart.getAvgLevel());
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
					Integer.parseInt(partElements[1]), 
					Integer.parseInt(partElements[2]),
					Integer.parseInt(partElements[3]),
					Double.parseDouble(partElements[4])));
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

	public double getTotAvgLevel() {
		double avg=0.0,studyWeight=0.0;
		double partWeight,avgLevel;
		for(int i=0;i<QParts.size();i++){
			partWeight   = QParts.get(i).getLength();
			partWeight	/= QQUtils.Juz2AvgWords;
			avgLevel     = QParts.get(i).getAvgLevel();
			
			studyWeight += partWeight;
			avg += avgLevel*partWeight;
		}
		return avg/studyWeight;
	}
}
