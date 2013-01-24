package com.google.code.quranquiz;

import java.util.Vector;

public class QQProfile {

	private int lastSeed;				// Seed for the Question
	private int level;					// User Level, currently
	private int correct;
	private int quesCount;
	private Vector<QQStudyPart> QParts;
	
	public QQProfile(int lastSeed, int level, int corr, int quesCount ){
		setLastSeed(lastSeed);
		setLevel(level);
		setCorrect(corr);
		setQuesCount(quesCount);
	}
	public QQProfile(int lastSeed, int level, int corr, int quesCount, String QPartsString ){
		String[] partElements;
		
		setLastSeed(lastSeed);
		setLevel(level);
		setCorrect(corr);
		setQuesCount(quesCount);
		
		QParts = new Vector<QQStudyPart>();
		for (String token : QPartsString.split(";")) {
			partElements = token.split(",");
			QParts.add(new QQStudyPart(Integer.parseInt(partElements[0]),
					Integer.parseInt(partElements[1]),
					Integer.parseInt(partElements[2]),
					Integer.parseInt(partElements[3]) ));
		 }
	}
	public int getLastSeed() {
		return lastSeed;
	}
	public void setLastSeed(int lastSeed) {
		this.lastSeed = lastSeed;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCorrect() {
		return correct;
	}
	public void setCorrect(int corr) {
		this.correct = corr;
	}
	public int getQuesCount() {
		return quesCount;
	}
	public void setQuesCount(int quesCount) {
		this.quesCount = quesCount;
	}
	public int getTotalStudyLength(){
		int Length=0;
		for(int i=0;i<QParts.size();i++)
			Length += ((QQStudyPart)QParts.get(i)).getLength();
		return Length;
	}
	public int getSparsePoint(int CntTot){
		int Length=0,i,pLength;
		for(i=0;i<QParts.size();i++){
			pLength = ((QQStudyPart)QParts.get(i)).getLength();
			if(CntTot < Length + pLength ){
				return ((QQStudyPart)QParts.get(i)).getStart() + CntTot - Length;				
			}else{
				Length += pLength;	
			}
		}
		return ((QQStudyPart)QParts.get(i)).getStart(); //Should not be reached
	}
	public String getStudyParts(){
		String tokens="";
		QQStudyPart currentPart;
		for(int i=0;i<QParts.size();i++){
			currentPart = QParts.get(i);
			tokens += String.valueOf(currentPart.getStart()) + ",";
			tokens += String.valueOf(currentPart.getLength()) + ",";
			tokens += String.valueOf(currentPart.getNumCorrect()) + ",";
			tokens += String.valueOf(currentPart.getNumQuestions());
			if(i<QParts.size()-1)
				tokens += ";"; // Skip ; after the last token
		}
		return tokens;
	}
}
