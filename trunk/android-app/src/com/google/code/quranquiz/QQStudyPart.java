package com.google.code.quranquiz;

public class QQStudyPart {
	private int start;
	private int length;
	private int numCorrect;
	private int numQuestions;
	
	public QQStudyPart(int start, int length){
		this.start = start;
		this.length = length;
		numCorrect = 0;
		numQuestions = 0;
	}
	
	public QQStudyPart(int start, int length, int corr, int total){
		this.start = start;
		this.length = length;
		numCorrect = corr;
		numQuestions = total;
	}
	
	public void addCorrect(){
		numCorrect   +=1;
		numQuestions +=1;
	}
	
	public void addIncorrect(){
		numQuestions +=1;
	}
	
	public int getStart(){
		return start;
	}
	
	public int getLength(){
		return length;
	}
	
	public int getNumCorrect(){
		return numCorrect;
	}
	
	public int getNumQuestions(){
		return numQuestions;
	}
	
	public float getCorrectRatio(){
		return numCorrect/(float)numQuestions;
	}
	
}
