package net.quranquiz.model;

public enum QQEventType {

    UI_REFRESH("Refresh all [UI]"),
    UI_QUESTIONAIRE_UPDATE("Update question text and 5 options [UI]"),
    UI_QUESTION_TYPE_UPDATE("Update question instructions/type [UI]"),
    UI_SHOW_ANSWER("Either bad answer or completed question, show correct answer [UI]"),
    UI_START_TIMER("Start count-up timer [UI]"),
    UI_END_TIMER("Stop timer [UI]"),
    UI_PROGRESS_UPDATE("Update Progress [UI]"),
    UI_SESSION_START_DAILY_QUIZ("Start Daily Quiz [UI]"),
    UI_SESSION_START_QUESTIONNAIRE("Stop Daily quiz, return to questionnaire [UI]"),
    UI_STATUS_DB_UNZIPPING("DB is begin prepared [UI]"),
    UI_STATUS_DAILY_QUIZ_BUILDING("Daily Quiz is begin prepared [UI]"),
    UI_SHOW_STUDY_LIST("Initially, users should select study items [UI]"),
    UI_DAILY_QUIZ_AVAILABLE("Inform user of available daily quiz [UI]"),
    UI_DAILY_QUIZ_REPORT_AVAILABLE("Inform user of available daily quiz report [UI]"),
    UI_SCORE_UPDATE("Update user score [UI]");
 
    @SuppressWarnings("unused")
	private String description;
    private QQEventType(String description){
        this.description = description;
    }
}
