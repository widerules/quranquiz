package com.google.code.quranquiz;

import java.io.Serializable;
import java.util.Vector;

public class QQProfile implements Serializable{

	private static final long serialVersionUID = 21L;
	private int lastSeed;				// Seed for the Question
	private int level;					// TODO: Encapsulate
	private Vector<QQStudyPart> QParts;
	
	public QQProfile(int lastSeed, int level ){
		setLastSeed(lastSeed);
		setLevel(level);
	}
	public QQProfile(int lastSeed, int level, String QPartsString ){
		
		setLastSeed(lastSeed);
		setLevel(level);
		setStudyParts(QPartsString);
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
	public int getCorrect(int part) {
		if(part<QParts.size()){
			return ((QQStudyPart)QParts.get(part)).getNumCorrect();			
		}else{
			return 0;
		}
	}

	public int getQuesCount(int part) {
		if(part<QParts.size()){
			return ((QQStudyPart)QParts.get(part)).getNumQuestions();			
		}else{
			return 0;
		}
	}

	public int getTotalStudyLength(){
		int Length=0;
		QQStudyPart QPart; 
		for(int i=0;i<QParts.size();i++){
			QPart = QParts.get(i);
			if(QPart.getStart()>0)
				Length += QPart.getLength();			
		}
		return Length;
	}
	public QQSparseResult getSparsePoint(int CntTot){
		int Length=0,i,pLength;
		for(i=0;i<QParts.size();i++){
			pLength = ((QQStudyPart)QParts.get(i)).getLength();
			if(CntTot < Length + pLength ){
				return new QQSparseResult(((QQStudyPart)QParts.get(i)).getStart() + CntTot - Length,i);				
			}else{
				Length += pLength;	
			}
		}
		return new QQSparseResult(((QQStudyPart)QParts.get(i)).getStart(),i);
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
	public void addIncorrect(int currentPart) {
		if(currentPart<QParts.size()){
			((QQStudyPart)QParts.get(currentPart)).addIncorrect();
		}
	}
	public void addCorrect(int currentPart) {
		if(currentPart<QParts.size()){
			((QQStudyPart)QParts.get(currentPart)).addCorrect();
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
					Integer.parseInt(partElements[3]) ));
		 }		
	}
	public int getTotalCorrect() {
		int Tot=0;
		QQStudyPart QPart; 
		for(int i=0;i<QParts.size();i++){
			QPart = QParts.get(i);
			if(QPart.getStart()>0)
				Tot += QPart.getNumCorrect();			
		}
		return Tot;
	}
	public int getTotalQuesCount() {
		int Tot=0;
		QQStudyPart QPart; 
		for(int i=0;i<QParts.size();i++){
			QPart = QParts.get(i);
			if(QPart.getStart()>0)
				Tot += QPart.getNumQuestions();			
		}
		return Tot;
	}
	public int getScore() {
		int studyCount 	= this.getTotalStudyLength();
		int correct   	= this.getTotalCorrect();
		int total		= this.getTotalQuesCount();
		
		return (int)Math.ceil(
				3000*((double)studyCount/QQUtils.QuranWords)*
				0.5*(1+Math.tanh(5*(double)correct/total-2.5)));
	}
	public String getuid() {
		// TODO Auto-generated method stub
		return "";
	}
	public double getAvgLevel() {
		// TODO Auto-generated method stub
		return 1.5;
	}
}
