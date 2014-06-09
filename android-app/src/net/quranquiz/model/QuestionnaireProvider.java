/**
 * 
 */
package net.quranquiz.model;


/**
 * An interface for all questionnaire providers, such as
 * free-running questionnaires, daily quiz, .. etc.
 * 
 * @author TELDEEB
 */
public interface QuestionnaireProvider {
	public QQQuestionObject getQ();
	public int getSeed();
	public void createNextQ();
}
