/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.model;

import android.util.Log;
import net.quranquiz.storage.QQProfile;
import net.quranquiz.util.QQUtils;


/**
 * This class handles the daily quiz logic while running it.
 * According to the user's study parts, it selects the quiz questions
 * to sum up to DAILYQUIZ_QPERPART_COUNT.
 * 
 * Example: User gets a dailyQuiz object covering all 49 parts, with 10 questions each. 
 * But user only selects 3 study parts {p1,p2,p3}, so he gets the following quiz questions:
 * 		- dailyQuiz.objects[p1][0:5]
 * 		- dailyQuiz.objects[p2][0:2]
 * 		- dailyQuiz.objects[p3][0]
 * 
 * This sums up to 10 question quiz. The question count from each study part is its
 * relative size.
 * 
 * @author TELDEEB
 *
 */
public class DailyQuizQuestionnaire implements QuestionnaireProvider {

	private QQDailyQuiz dailyQuiz;
	private int currentQuestion;
	private int currentPart;
	private int sparseQuestions[];
	private int remainingQuestions;
	private QQQuestionObject qo;
	private int preDQCorrectAnswers;
	
	public DailyQuizQuestionnaire(QQDailyQuiz dailyQuiz, QQProfile prof){
		this.dailyQuiz 		= dailyQuiz;
		currentPart 		= 0; 
		currentQuestion 	= 0;
		remainingQuestions	= QQUtils.DAILYQUIZ_QPERPART_COUNT;
		sparseQuestions 	= prof.getDailyQuizStudyPartsWeights(dailyQuiz.randomSeed);
		createNextQ(); //First Question
	}

	@Override
	public QQQuestionObject getQ() {
		return qo;
	}

	@Override
	public int getSeed() {
		return 0;
	}

	@Override
	public void createNextQ() {
		if(remainingQuestions>0){
			while(sparseQuestions[currentPart] == 0){
				currentPart++;
				currentQuestion = 0;
			}
			sparseQuestions[currentPart]--;
			qo = dailyQuiz.objects[currentPart][currentQuestion++];
			remainingQuestions--;
			//qo.currentPart = currentPart;
			Log.i("DQ-NextQ", "Part="+currentPart+" Question="+currentQuestion+" Remaining="+remainingQuestions+" Start:"+qo.startIdx);
		}
	}
	
	public int getRemainingQuestions(){
		return remainingQuestions;
	}

	public void postResults(QQProfile myQQProfile, long timeInMillies) {
		int dqCorrectPoints = myQQProfile.getTotalCorrect() - preDQCorrectAnswers;
		int dqSelectedParts = myQQProfile.getTotalStudyLength()/QQUtils.Juz2AvgWords;
		Log.i("DQ-NextQ", "Posting: "+ 	dqCorrectPoints + "/10 @" + dqSelectedParts +" in "+ timeInMillies +" ms.");
		//TODO: Make post
	}

	public void setPreDQCorrectAnswers(int totalCorrect) {
		this.preDQCorrectAnswers = totalCorrect;
	}

}
