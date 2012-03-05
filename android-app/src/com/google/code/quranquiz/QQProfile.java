package com.google.code.quranquiz;

public class QQProfile {


	private int lastSeed;				// Seed for the Question
	private int level;					// User Level, currently
	private int score;
	private int quesCount;
	public QQProfile(int lastSeed, int level, int score, int quesCount ){
		setLastSeed(lastSeed);
		setLevel(level);
		setScore(score);
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
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getQuesCount() {
		return quesCount;
	}
	public void setQuesCount(int quesCount) {
		this.quesCount = quesCount;
	}
}
