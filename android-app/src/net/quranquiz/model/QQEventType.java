package net.quranquiz.model;

public enum QQEventType {

    UI_REFRESH("Refresh all"),
    UI_QUESTIONAIRE_UPDATE("Update question text and 5 options"),
    UI_QUESTION_TYPE_UPDATE("Update question instructions/type"),
    UI_SHOW_ANSWER("Either bad answer or completed question, show correct answer"),
    UI_PROGRESS_UPDATE("Update Progress"),
    UI_SESSION_START_DAILY_QUIZ("Start Daily Quiz"),
    UI_SESSION_START_QUESTIONNAIRE("Stop Daily quiz, return to questionnaire"),
    UI_STATUS_DB_UNZIPPING("DB is begin prepared"),
    UI_STATUS_DAILY_QUIZ_BUILDING("Daily Quiz is begin prepared"),
    UI_SHOW_STUDY_LIST("Initially, users should select study items"),
    UI_DAILY_QUIZ_AVAILABLE("Inform user of available daily quiz"),
    UI_DAILY_QUIZ_REPORT_AVAILABLE("Inform user of available daily quiz report"),
    UI_SCORE_UPDATE("Update user score");
 
    @SuppressWarnings("unused")
	private String description;
    private QQEventType(String description){
        this.description = description;
    }
}
