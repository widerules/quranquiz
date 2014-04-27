package net.quranquiz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.text.format.Time;
import android.util.Log;

public class QQDailyQuiz implements Serializable {

	private static final long serialVersionUID = 1L;
	private Time timeStamp;
	private int partCount;
	private int questionsCount;
	private QQQuestionObject[][] objects;
	public 	ByteArrayOutputStream bos;

	public QQDailyQuiz(){
		timeStamp 		= new Time();
		timeStamp.setToNow();
		
		partCount 		= QQUtils.DAILYQUIZ_PARTS_COUNT;
		questionsCount	= QQUtils.DAILYQUIZ_QPERPART_COUNT;
		
		objects = new QQQuestionObject[QQUtils.DAILYQUIZ_PARTS_COUNT][QQUtils.DAILYQUIZ_QPERPART_COUNT];
		
		for(int i=1;i<=partCount;i++ ){ //Skip Al-Fatiha!
			for(int j=0;j<questionsCount;j++){
				objects[i-1][j] = QQQuestionaire.createDefinedQuestion(i);
			}
			Log.d("DailyQuiz","created part"+i);
		}
		bos = new ByteArrayOutputStream();
	    ObjectOutput out;
		try {
			out = new ObjectOutputStream(bos);
		    out.writeObject(objects);
		    out.close();
		    bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("DailyQuiz","objects size = " + bos.toByteArray().length + "bytes");
	}
	

}
