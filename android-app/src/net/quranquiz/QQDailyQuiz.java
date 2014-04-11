package net.quranquiz;

import java.io.Serializable;

import android.text.format.Time;
import android.util.Log;

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
		questionsCount	= QQUtils.DAILYQUIZ_QPERPART_COUNT;
		
		objects = new QQQuestionObject[QQUtils.DAILYQUIZ_PARTS_COUNT][QQUtils.DAILYQUIZ_QPERPART_COUNT];
		
		for(int i=1;i<partCount;i++ ){ //Skip Al-Fatiha!
			for(int j=0;j<questionsCount;j++){
				objects[i][j] = QQQuestionaire.createDefinedQuestion(i);
			}
			Log.d("DailyQuiz","created part"+i);
		}
	}
	

}
