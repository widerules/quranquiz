package net.quranquiz.model;

import java.util.Calendar;

import net.quranquiz.R;
import net.quranquiz.model.QQQuestionaire.QType;
import net.quranquiz.util.QQUtils;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;

import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

public class Model {

	private ViewModel viewModel;

	public Model() {
		
	}

	public void userAction(int selID) {

		if (QOptIdx >= 0 && correct_choice != selID) {// Wrong choice!!

			//btnArrayR[selID].startAnimation(animFadeOut);
			//btnArrayR[selID].set
			// Vibrate for 300 milliseconds
			Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			mVibrator.vibrate(300);

			setBackCard();
			QOptIdx = -1; // trigger a new question
		} else {
			QOptIdx = (QOptIdx == -1) ? -1 : QOptIdx + 1; // Keep -1, or Proceed
															// with options ..
		}

		if (QOptIdx == -1 || QOptIdx == Quest.getQ().rounds) {
			myQQProfile = myQQProfileHandler.CurrentProfile;
			
			if (QQinit == 0 && QOptIdx == -1) { // A wrong non-special answer
				if(Quest.getQ().qType == QType.NOTSPECIAL)
					if(myQQProfile.getLevel()>0 || isDailyQuizRunning_shadow)
						myQQProfile.addIncorrect(CurrentPart);

			} else { // A correct answer
				if(QQinit == 0 && QOptIdx == Quest.getQ().rounds){
					if(myQQProfile.getLevel()>0 || isDailyQuizRunning_shadow ){
						if(Quest.getQ().qType == QType.NOTSPECIAL)
							myQQProfile.addCorrect(CurrentPart);
						else
							myQQProfile.addSpecial(Quest.getQ().qType.getScore());
					}
					// Display Correct answer
					setBackCard();
				}
			}

			if(QQinit==0){ // Need Card Flip if game not initialized
				AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);				
			}
			
			myQQProfileHandler.reLoadCurrentProfile(); // For first Question, optimize?
			
			// Check for DailyQuiz end
			if(Quest != null && myQQSession.isDailyQuizRunning 
					&& myQQSession.getDailyQuizQuestionnaire().getRemainingQuestions()<1)
				myQQSession.isDailyQuizRunning = false;
			
			//Take Action: Switch between DailyQuiz <--> FreeRunning Questionnaire
			if( Quest == null || isDailyQuizRunning_shadow != myQQSession.isDailyQuizRunning){
				isDailyQuizRunning_shadow = myQQSession.isDailyQuizRunning;
				if(isDailyQuizRunning_shadow){
					Quest = myQQSession.getDailyQuizQuestionnaire();
		        	((DailyQuizQuestionnaire)Quest).setPreDQCorrectAnswers(myQQProfile.getTotalCorrect());
					setScoreUIVisible(true);
					leftBar.setVisibility(View.VISIBLE);
				}else{ 
					if(Quest != null){
						countUpHandler.removeCallbacks(updateTimerMethod);
						tvCountUp.setVisibility(View.INVISIBLE);
						tvCountUpTenths.setVisibility(View.INVISIBLE);
						((DailyQuizQuestionnaire)Quest).postResults(myQQProfile, timeInMillies);
					}
					Quest = new QQQuestionaire(myQQProfile, q, myQQSession);
					setScoreUIVisible(myQQProfile.getLevel()!=0);
					leftBar.setVisibility(View.INVISIBLE);
				}
			}else
				Quest.createNextQ();
			
			if(isDailyQuizRunning_shadow){
				leftBar.setProgress(myQQSession.getDailyQuizQuestionnaire().getRemainingQuestions());
			}
			CurrentPart = Quest.getQ().currentPart;	
			
			if(QQUtils.QQDebug>0){
				qqLogger.debug("------" +Calendar.getInstance().getTimeInMillis()+"------");
				qqLogger.debug("@"+Quest.getQ().startIdx+" v="+Quest.getQ().validCount);
				for(int dd=0;dd<10;dd++){
					qqLogger.debug(Quest.getQ().op[dd][0]+"-"+Quest.getQ().op[dd][1]+"-"+Quest.getQ().op[dd][2]+"-"+Quest.getQ().op[dd][3]+"-"+Quest.getQ().op[dd][4]);					
				}
			}
			// Update profile after a new Question!
			lastSeed = Quest.getSeed();
			myQQProfile.setLastSeed(lastSeed);

			// Update the Score
			tvScore.setText(String.valueOf(myQQProfile.getScore()));

			myQQProfileHandler.saveProfile(myQQProfile); // TODO: Do I need to
															// save after each
															// question? On exit
															// only?

			// Show the Question!
			tvQ.setText(QQUtils.fixQ(q.txt(Quest.getQ().startIdx, Quest.getQ().qLen,QQUtils.QQTextFormat.AYAMARKS_BRACKETS_START)));
			QOptIdx = 0;
			
			//Show Score Up/Down
			if(Quest.getQ().qType == QType.NOTSPECIAL){
				tvScoreUp.setText(myQQProfile.getUpScore(Quest.getQ().currentPart));
				tvScoreDown.setText(myQQProfile.getDownScore(Quest.getQ().currentPart));
			} else {
				tvScoreUp.setText(String.valueOf(Quest.getQ().qType.getScore()));
				tvScoreDown.setText("-");				
			}
			
		}

		// Concat correct options to the Question!
		if (QOptIdx > 0)
			// I use 3 spaces with quran_me font, or a single space elsewhere
			tvQ.setText(QQUtils.fixQ(tvQ
					.getText()
					.toString()
					.concat(q.txt(Quest.getQ().startIdx + Quest.getQ().qLen + (QOptIdx - 1)
									* Quest.getQ().oLen, Quest.getQ().oLen, QQUtils.QQTextFormat.AYAMARKS_BRACKETS_ONLY) + "  "
							)));

		// Scramble options
		int[] scrambled = new int[5];
		scrambled = QQUtils.randperm(5);
		correct_choice = QQUtils.findIdx(scrambled, 0); // idx=1

		//Display Instructions
		tvInstructions.setText(Quest.getQ().qType.getInstructions());
		if(Quest.getQ().qType == QType.NOTSPECIAL)
			QQUtils.tvSetBackgroundFromDrawable(tvInstructions, R.drawable.tv_instruction_background);
		else
			QQUtils.tvSetBackgroundFromDrawable(tvInstructions, R.drawable.tv_instruction_special_background);

		
		// Display Options:
		String strTemp = new String();
		for (int j = 0; j < 5; j++) {
			if(Quest.getQ().qType==QType.NOTSPECIAL)
				strTemp = q.txt(Quest.getQ().op[QOptIdx][scrambled[j]], Quest.getQ().oLen, QQUtils.QQTextFormat.AYAMARKS_NONE);
			else{
				switch(Quest.getQ().qType){
				case SURANAME:
					strTemp = "  سورة  " + QQUtils.getSuraNameFromIdx(Quest.getQ().op[QOptIdx][scrambled[j]]);
					break;
				case SURAAYACOUNT:
					strTemp = " آيات السورة " + Quest.getQ().op[QOptIdx][scrambled[j]];
					break;
				case AYANUMBER:
					strTemp = " رقم الآية " + Quest.getQ().op[QOptIdx][scrambled[j]];
					break;
				default: 
					strTemp = "-";
					break;
				}
			}
			btnArray[j].setText(QQUtils.fixQ(strTemp));
		}
		updateOptionButtonsColor(correct_choice); //Update background Color
		
		if (level == 3) {
			// Start the timer
			startTimer(5);
			if (QOptIdx == 1) {
				// display(" [-] No more valid Motashabehat!");
			} else {
				// display([' -- ',num2str(validCount),' correct options
				// left!']); // TODO: Subtract done options
			}
		}else{ // Not level 3
			
		}

		QQinit = 0;

	}
}
