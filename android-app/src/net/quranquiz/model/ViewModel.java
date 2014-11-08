package net.quranquiz.model;

import net.quranquiz.ui.QQQuestionaireActivity;
import android.util.Log;

/**
 * This class is the view model. Any UI class should bind to this model.
 * <ul>
 *  <li> UIView: Send User commands; ViewModel.setUserSelection(id)
 *  <li> UIView: Listens to model events
 *  <li> ViewModel: Hold all states of the UI, routes commands to model and throw events from model
 *  <li> Model: Implements logic, connects to DB, handles sessions and communicates with server.  
 * </ul>
 * @author TELDEEB
 *
 */
public class ViewModel implements QQModelEvent.Listener{

	private String 	question, correctAnswer;
	private String 	options[];
	private String	instructions;
	private String 	score, scoreUp, scoreDown;
	private int		progress;
	private int 	counter;
	private boolean showingScore;
	private boolean showingProgress;
	private boolean showingCounter;
	private boolean	showingQuestion;
	private boolean isSpecialQuestion;
	private int		level;
	private Model 	model;
	private QQQuestionaireActivity _activity = null;
	
	public ViewModel() {
		model 		= new Model();
		model.getEventBus().addEventListener(this);
		model.start();
		
	}
	/*TODO: Cannot rely on UI, define interface*/
	public ViewModel(QQQuestionaireActivity activity) {
		_activity 	= activity;
		model 		= new Model();
		model.getEventBus().addEventListener(this);
		model.start();
		
	}
	
	public boolean isBind(){
		return (_activity==null)?false:true;
	}
	public void bindUI(QQQuestionaireActivity activity){
		_activity 	= activity;		
	}
	
	@Override
	public void onQQUIEvent(QQModelEvent event, String message) {
		QQEventType src = (QQEventType) event.getSource();
		if(src.toString().contains("UI")){
			switch(src){
			case UI_REFRESH:
				Log.e("vm", "UnImplemented UI Event: "+src);
				break;
			case UI_QUESTIONAIRE_UPDATE:
				question 	= model.getQuestion();
				options 	= model.getOptions();
				_activity.vmSetQuestion(question);
				_activity.vmSetOptions(options, model.getCorrectChoiceIndex());
				break;
			case UI_QUESTION_TYPE_UPDATE:
				isSpecialQuestion = model.isSpecialQuestion();
				break;
			case UI_SHOW_ANSWER:
				_activity.vmShowCorrectAnswer(message.contains("T"));
				break;
			case UI_PROGRESS_UPDATE:
				Log.e("vm", "UnImplemented UI Event: "+src);
				break;
			case UI_SESSION_START_DAILY_QUIZ:
				_activity.vmStartDailyQuiz();
				break;
			case UI_SESSION_START_QUESTIONNAIRE:
				Log.e("vm", "UnImplemented UI Event: "+src);
				break;
			case UI_STATUS_DB_UNZIPPING:
				Log.e("vm", "UnImplemented UI Event: "+src);
				break;
			case UI_STATUS_DAILY_QUIZ_BUILDING:
				Log.e("vm", "UnImplemented UI Event: "+src);
				break;
			case UI_SHOW_STUDY_LIST:
				_activity.vmShowUsage();
				//TODO: Show Study list as well!
				break;
			case UI_DAILY_QUIZ_AVAILABLE:
				_activity.vmDailyQuizAvailable();
				break;
			case UI_DAILY_QUIZ_REPORT_AVAILABLE:
				Log.e("vm", "UnImplemented UI Event: "+src);
				break;
			case UI_SCORE_UPDATE:
				scoreUp 	= model.getScoreUp();
				scoreDown 	= model.getScoreDown();
				score		= model.getScore();
				break;
			    
			default:
				Log.e("vm", "Unhandled UI Event!");
				break;
			}
		}
	}
	@Override
	public void onQQGenericEvent(QQModelEvent event, String message) {
		// Auto-generated method stub
		// Not yet needed!
	}
	public void setUserSelection(int optionID){
		model.userAction(optionID);
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getCorrectAnswer() {
		correctAnswer = model.getCorrectAnswer();
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getScoreUp() {
		return scoreUp;
	}
	public void setScoreUp(String scoreUp) {
		this.scoreUp = scoreUp;
	}
	public String getScoreDown() {
		return scoreDown;
	}
	public void setScoreDown(String scoreDown) {
		this.scoreDown = scoreDown;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public boolean isShowingProgress() {
		return showingProgress;
	}
	public void setShowingProgress(boolean showingProgress) {
		this.showingProgress = showingProgress;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public boolean isShowingScore() {
		return showingScore;
	}
	public void setShowingScore(boolean showingScore) {
		this.showingScore = showingScore;
	}
	public boolean isShowingCounter() {
		return showingCounter;
	}
	public void setShowingCounter(boolean showingCounter) {
		this.showingCounter = showingCounter;
	}
	public boolean isShowingQuestion() {
		return showingQuestion;
	}
	public void setShowingQuestion(boolean showingQuestion) {
		this.showingQuestion = showingQuestion;
	}
	public boolean isSpecialQuestion() {
		return isSpecialQuestion;
	}
	public void setSpecialEnabled(Boolean isEnabled) {
		model.setSpecialQuestionEnabled(isEnabled);
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
		_activity.vmSetScoreVisiblity(level!=0); // Hide score for practicing
	}

	public void close() {
		model.close();		
	}

	public void reload() {
		model.reload();
	}

	public String getQuranUri() {
		return model.getQuranUri();
	}

	public void startDailyQuiz() {
		model.startDailyQuiz();
	}
}
