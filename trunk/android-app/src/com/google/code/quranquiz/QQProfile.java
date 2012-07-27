package com.google.code.quranquiz;

public class QQProfile {

	private int lastSeed;				// Seed for the Question
	private int level;					// User Level, currently
	private int correct;
	private int quesCount;
	public QQProfile(int lastSeed, int level, int corr, int quesCount ){
		setLastSeed(lastSeed);
		setLevel(level);
		setCorrect(corr);
		setQuesCount(quesCount);
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
}
