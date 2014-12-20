package net.quranquiz.model;

import java.util.Calendar;

import net.quranquiz.model.QQQuestionaire.QType;
import net.quranquiz.storage.QQDataBaseHelper;
import net.quranquiz.storage.QQProfile;
import net.quranquiz.storage.QQProfileHandler;
import net.quranquiz.ui.QQQuestionaireActivity;
import net.quranquiz.util.QQUtils;
import android.database.SQLException;

import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;

public class Model {
	private QQDataBaseHelper q;
	private QuestionnaireProvider Quest = null;
	private int QOptIdx = -1;
	private int QQinit = 1;
	// TODO: Grab the last seed from the loaded profile! (replace -1, level 1)
	private int level = 1;
	private int lastSeed = -1;
	private int correct_choice = 0;
	private int CurrentPart = 0;
	private QQProfileHandler myQQProfileHandler;
	private QQProfile myQQProfile;
	private QQSession myQQSession;
	long timeInMillies = 0L;
	private boolean isDailyQuizRunning_shadow=false;
	private QQModelEvent events = new QQModelEvent(this);
	
	private String _sQuestion, _sScoreUp, _sScoreDown, _sInstructions;
	private String[] _sOptions = new String[5];
	private int _iProgress;

	private final static Logger qqLogger = LoggerFactory.getLogger(QQQuestionaireActivity.class);


	public Model() {
		initDBHandle();
		initProfile();
		initSession();
		handleDifferentVersions();
		initLogger();
	}

	/**
	 * Initialize user profile. Resume existing or create a default one
	 */
	private void initSession() {
		myQQSession = new QQSession(myQQProfile, this);		
	}

	/**
	 * Initialize user profile. Load existing or create a default one
	 */
	private void initProfile() {
		myQQProfileHandler = new QQProfileHandler();
		myQQProfile = myQQProfileHandler.getProfile();
		//Load List Selector first time
		if(myQQProfile.getTotalQuesCount()==0){
			
			events.dispatchEvent(new QQModelEvent(QQEventType.UI_SHOW_STUDY_LIST),"");
/*			if(android.os.Build.VERSION.SDK_INT 
					>= android.os.Build.VERSION_CODES.HONEYCOMB)
				intentStudyList = new Intent(QQQuestionaireActivity.this,
					QQStudyListActivity.class);
			else
				intentStudyList = new Intent(QQQuestionaireActivity.this,
						QQStudyListCompatActivity.class);*/
			
			events.dispatchEvent(new QQModelEvent(QQEventType.UI_REFRESH), "");
			//TODO: Check?!
			//intentStudyList.putExtra("ProfileHandler", myQQProfileHandler);
			//startActivity(intentStudyList);
		}	
	}
	/**
	 * Initializes a user-side logger. All debug values are pushed to a 
	 * file at the user storage area. Logs are included according to their
	 * level and the required Levels.
	 */
	private void initLogger() {
		if(QQUtils.QQDebug>0){
			FileAppender appender = new FileAppender();
			/*
			String strQQLogFile = getFilesDir()+"/qq-logger.txt";
			File fhQQLogFile = new File(strQQLogFile);
			if(!fhQQLogFile.exists()){
				try {
					fhQQLogFile.createNewFile();
				} catch (IOException e) {}
			}
			appender.setFileName(strQQLogFile);
			*/
			appender.setAppend(true);
	        qqLogger.addAppender(appender);
	        qqLogger.setLevel(Level.DEBUG);
	        qqLogger.warn("Logger session started!");
		}
	}

	/**
	 * Initialize a handle for the needed SQLite3 database 
	 * for QuranQuiz to operate. Initially, the DB needs to get 
	 * uncompressed then its index built. A splash screen is displayed
	 * to demonstrate a demo screen while the initial DB preparation. 
	 */
	private void initDBHandle() {
		q = new QQDataBaseHelper();
		if (!q.checkDataBase()){
			events.dispatchEvent(new QQModelEvent(QQEventType.UI_STATUS_DB_UNZIPPING), "");
			try {
				q.createDataBase(); //slow!
			} catch (Exception sqle) {
			}
		}
		try {
			q.openDataBase();
			} 
		catch (SQLException sqle) {	}
		catch (Exception ioe) {
				//TODO: Implement
				//finish(); //destroy Questionnaire.
				return;
			}
	}
	
	/**
	 * Do tweaks for different android versions
	 */
	private void handleDifferentVersions() {
		//Remove Tashkeel at old android versions, bad arabic support!
		if(android.os.Build.VERSION.SDK_INT 
				>= android.os.Build.VERSION_CODES.HONEYCOMB)
			QQUtils.disableFixQ();			
	}
	
	/**
	 *  Just a public getter
	 * @return profile handler
	 */
	public QQProfileHandler getProfileHandler(){
		return myQQProfileHandler;
	}

	public void userAction(int selID) {

		if(!q.isOpen())//TODO: Who/When it gets closed?!
			q.openDataBase(); 
		
		if (QOptIdx >= 0 && correct_choice != selID) {// Wrong choice!!

			events.dispatchEvent(new QQModelEvent(QQEventType.UI_SHOW_ANSWER), "False");
			//setBackCard();
			QOptIdx = -1; // trigger a new question
		} else {
			QOptIdx = (QOptIdx == -1) ? -1 : QOptIdx + 1; // Keep -1, or Proceed
															// with options ..
		}

		if (QOptIdx == -1 || QOptIdx == Quest.getQ().rounds) {
			myQQProfile = myQQProfileHandler.getProfile();
			
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
					events.dispatchEvent(new QQModelEvent(QQEventType.UI_SHOW_ANSWER), "True");

					//setBackCard();
				}
			}

			//TODO: Check!??
	/*		if(QQinit==0){ // Need Card Flip if game not initialized
				AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);				
			}*/
			
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
					
		        	events.dispatchEvent(new QQModelEvent(QQEventType.UI_SESSION_START_DAILY_QUIZ), "");
		        	//setScoreUIVisible(true);
					//leftBar.setVisibility(View.VISIBLE);
				}else{ 
					if(Quest != null){
			        	events.dispatchEvent(new QQModelEvent(QQEventType.UI_SESSION_START_QUESTIONNAIRE), "");
/*
			        	countUpHandler.removeCallbacks(updateTimerMethod);
						tvCountUp.setVisibility(View.INVISIBLE);
						tvCountUpTenths.setVisibility(View.INVISIBLE);
*/						((DailyQuizQuestionnaire)Quest).postResults(myQQProfile, timeInMillies);
					}
					Quest = new QQQuestionaire(myQQProfile, q, myQQSession);
					
					//TODO: Fix
					//setScoreUIVisible(myQQProfile.getLevel()!=0);
					//leftBar.setVisibility(View.INVISIBLE);
				}
			}else
				Quest.createNextQ();
			
			if(isDailyQuizRunning_shadow){
	        	events.dispatchEvent(new QQModelEvent(QQEventType.UI_SESSION_START_DAILY_QUIZ), "");
				//leftBar.setProgress(myQQSession.getDailyQuizQuestionnaire().getRemainingQuestions());
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

			myQQProfileHandler.saveProfile(); // TODO: Do I need to
			// save after each
			// question? On exit
			// only?

			// Show the Question!
			_sQuestion = QQUtils.fixQ(q.txt(Quest.getQ().startIdx, Quest.getQ().qLen,QQUtils.QQTextFormat.AYAMARKS_BRACKETS_START)); 
			QOptIdx = 0;
			
			//Show Score Up/Down
			if(Quest.getQ().qType == QType.NOTSPECIAL){
				_sScoreUp 	= (String) myQQProfile.getUpScore(Quest.getQ().currentPart);
				_sScoreDown = (String) myQQProfile.getDownScore(Quest.getQ().currentPart);
			} else {
				_sScoreUp	= String.valueOf(Quest.getQ().qType.getScore());
				_sScoreDown = "-";				
			}
			
			// Update the Score UI
        	events.dispatchEvent(new QQModelEvent(QQEventType.UI_SCORE_UPDATE), "");
        	//tvScore.setText(String.valueOf(myQQProfile.getScore()));
        	
        	//TODO: Check!
			events.dispatchEvent(new QQModelEvent(QQEventType.UI_QUESTIONAIRE_UPDATE), "");

		}

		// Concat correct options to the Question!
		if (QOptIdx > 0)
			// I use 3 spaces with quran_me font, or a single space elsewhere
			_sQuestion = QQUtils.fixQ(_sQuestion.concat(q.txt(Quest.getQ().startIdx + Quest.getQ().qLen + (QOptIdx - 1)
									* Quest.getQ().oLen, Quest.getQ().oLen, QQUtils.QQTextFormat.AYAMARKS_BRACKETS_ONLY) + "  "));

		// Scramble options
		int[] scrambled = new int[5];
		scrambled = QQUtils.randperm(5);
		correct_choice = QQUtils.findIdx(scrambled, 0); // idx=1

		//Display Instructions
		_sInstructions = Quest.getQ().qType.getInstructions();
		
		events.dispatchEvent(new QQModelEvent(QQEventType.UI_QUESTION_TYPE_UPDATE),"");
		
		/*if(Quest.getQ().qType == QType.NOTSPECIAL)
			QQUtils.tvSetBackgroundFromDrawable(tvInstructions, R.drawable.tv_instruction_background);
		else
			QQUtils.tvSetBackgroundFromDrawable(tvInstructions, R.drawable.tv_instruction_special_background);
*/
		
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
			_sOptions[j] = QQUtils.fixQ(strTemp);
		}

		events.dispatchEvent(new QQModelEvent(QQEventType.UI_QUESTIONAIRE_UPDATE), String.valueOf(correct_choice));
		//TODO: Check!
		//updateOptionButtonsColor(correct_choice); //Update background Color
		
		if (level == 3) {
			// Start the timer
			//startTimer(5);
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
	
	public QQModelEvent getEventBus(){
		return events;
	}
	
	public int getProgress() {
		return _iProgress;
	}

	public void setProgress(int _iProgress) {
		this._iProgress = _iProgress;
	}
	public String getQuestion(){
		return _sQuestion;
	}
	public String getScore(){
		return String.valueOf(myQQProfile.getScore());
	}
	public String getScoreUp(){
		return _sScoreUp;
	}
	public String getScoreDown(){
		return _sScoreDown;
	}
	public String getInstructions(){
		return _sInstructions;
	}	
	public String[] getOptions(){
		return _sOptions;
	}
	public boolean isSpecialQuestion() {
		return (Quest.getQ().qType==QType.NOTSPECIAL)?false:true;
	} 
	/**
	 * Starts the model by creating the 
	 * first Question
	 */
	public void start() {
		
		if(myQQProfile.getTotalQuesCount()<2)
	    	events.dispatchEvent(new QQModelEvent(QQEventType.UI_SHOW_STUDY_LIST), "");

		userAction(-1);
	}

	public void close() {
		myQQProfileHandler.saveProfile();
		myQQSession.close();
		if (q != null)
			q.closeDatabase();
	}

	public void setSpecialQuestionEnabled(Boolean isEnabled) {
		myQQProfile.setSpecialEnabled(isEnabled);
	}
	public boolean getSpecialQuestionEnabled() {
		return myQQProfile.isSpecialEnabled();
	}

	public void reload() {
		myQQProfileHandler.reLoadCurrentProfile();
    	events.dispatchEvent(new QQModelEvent(QQEventType.UI_REFRESH), "");
	}

	public String getQuranUri() {
		return String.valueOf(QQUtils.getSuraIdx(Quest.getQ().startIdx)+1) +"/" +
				 q.ayaNumberOf(Quest.getQ().startIdx);
	}

	public void startDailyQuiz() {
    	myQQSession.reportDialogDisplayed();
    	myQQSession.isDailyQuizRunning = true;
        userAction(-1);
    	events.dispatchEvent(new QQModelEvent(QQEventType.UI_SESSION_START_DAILY_QUIZ), "");

	}

	public String getCorrectAnswer() {
		return "[" + "  سورة  "+ QQUtils.getSuraNameFromWordIdx(Quest.getQ().startIdx) + " - آياتها " + q.ayaCountOfSuraAt(Quest.getQ().startIdx)+ "] "+ "\n"
				+ QQUtils.fixQ(q.txt(Quest.getQ().startIdx, 12 * Quest.getQ().oLen + Quest.getQ().qLen,QQUtils.QQTextFormat.AYAMARKS_FULL))
				+ " ...";
	}

	public int getCorrectChoiceIndex() {
		return correct_choice;
	}	
}
