package net.quranquiz.model;

public enum QQEventType {

    /**
     * Refresh all UI elements
     */
    UI_REFRESH("Refresh all UI elements"),
    /**
     * Update question text and 5 options
     */
    UI_QUESTIONAIRE_UPDATE("Update question text and 5 options"),
    /**
     * Instruction text changed, occurs on new Questions
     */
    UI_QUESTION_TYPE_UPDATE("Update question instructions/type"),
    /**
     * Either bad answer or completed question, show correct answer
     */
    UI_SHOW_ANSWER("Either bad answer or completed question, show correct answer"),
    /**
     * Update Progress Bar
     */
    UI_PROGRESS_UPDATE("Update Progress Bar"),
    /**
     * Start Daily Quiz
     */
    UI_SESSION_START_DAILY_QUIZ("Start Daily Quiz"),
    /**
     * Stop Daily quiz, return to questionnaire
     */
    UI_SESSION_START_QUESTIONNAIRE("Stop Daily quiz, return to questionnaire"),
    /**
     * DB is being prepared
     */
    UI_STATUS_DB_UNZIPPING("DB is being prepared"),
    /**
     * Daily Quiz is being prepared
     */
    UI_STATUS_DAILY_QUIZ_BUILDING("Daily Quiz is being prepared"),
    /**
     * Initially, users should select study items
     */
    UI_SHOW_STUDY_LIST("Initially, users should select study items"),
    /**
     * Inform user of available daily quiz
     */
    UI_DAILY_QUIZ_AVAILABLE("Inform user of available daily quiz"),
    /**
     * Inform user of available daily quiz report
     */
    UI_DAILY_QUIZ_REPORT_AVAILABLE("Inform user of available daily quiz report"),
    /**
     * Update user score
     */
    UI_SCORE_UPDATE("Update user score");
 
    @SuppressWarnings("unused")
	private String description;
    private QQEventType(String description){
        this.description = description;
    }
}
