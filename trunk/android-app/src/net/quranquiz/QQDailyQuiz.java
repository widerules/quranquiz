package net.quranquiz;

import java.io.Serializable;
import java.util.Date;

import android.text.format.Time;

public class QQDailyQuiz implements Serializable {

	private static final long serialVersionUID = 1L;
	private Time timeStamp;
	private int partCount;
	private int questionsCount;
	private QQQuestionObject[][] objects;

	public QQDailyQuiz(){
		timeStamp 		= new Time();
		timeStamp.setToNow();
		
		partCount 		= QQUtils.DAILYQUIZ_PARTS_COUNT;
		questionsCount	= QQUtils.DAILYQUIZ_QUESTIONS_COUNT;
		
		objects = new QQQuestionObject[QQUtils.DAILYQUIZ_PARTS_COUNT][QQUtils.DAILYQUIZ_QUESTIONS_COUNT];
		
		for(int i=0;i<partCount;i++ )
			for(int j=0;j<questionsCount;j++){
				objects[i][j] = QQQuestionaire.createDefinedQuestion(i);
			}
	}
	

}
