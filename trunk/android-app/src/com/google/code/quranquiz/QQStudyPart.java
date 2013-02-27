package com.google.code.quranquiz;

import java.io.Serializable;

public class QQStudyPart implements Serializable {

	private static final long serialVersionUID = 34L;
	private int start;
	private int length;
	private int numCorrect;
	private int numQuestions;
	private double avgLevel=1.2; //TODO: Implement correctly

	public QQStudyPart(int start, int length) {
		this.start = start;
		this.length = length;
		numCorrect = 0;
		numQuestions = 0;
	}

	public QQStudyPart(int start, int length, int corr, int total) {
		this.start = start;
		this.length = length;
		numCorrect = corr;
		numQuestions = total;
	}

	public void addCorrect() {
		numCorrect += 1;
		numQuestions += 1;
	}

	public void addIncorrect() {
		numQuestions += 1;
	}

	public float getCorrectRatio() {
		
		return (numCorrect==0)? 0: numCorrect / (float) numQuestions;
	}

	public double getAvgLevel(){
		return avgLevel;
	}
	
	public int getLength() {
		if (start > 0)
			return length;
		else
			return 0;
	}

	public int getNonZeroLength() {
		return length;
	}

	public int getNumCorrect() {
		return numCorrect;
	}

	public int getNumQuestions() {
		return numQuestions;
	}

	public int getStart() {
		return start;
	}

}
