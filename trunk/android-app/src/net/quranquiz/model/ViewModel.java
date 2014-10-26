package net.quranquiz.model;

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
	private Model 	model;
	
	public ViewModel() {
		model = new Model();
		model.getEventBus().addEventListener(this);
		model.start();
	}
	
	@Override
	public void onQQUIEvent(QQModelEvent event, String message) {
		Log.d("vm", "Got Event:"+event.toString() + " - " + message);
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
}
