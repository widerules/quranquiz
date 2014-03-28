package net.quranquiz;

import java.io.Serializable;

import net.quranquiz.QQQuestionaire.QType;

public class QQQuestionObject implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/******** Questions parameters to be set: Start **********/
	public int rounds; // How many rounds a question has: 10 for normal, 1 for special
	public int validCount; //Number of correct options at the first round
	public int[][] op ; // Holds all 5 options x 10 rounds
	public int startIdx; // Precise Position near seed, valid options
	public int qLen; // Number of words to display to start the Question
	public int oLen; // Number of words of each option
	public QType qType; // Question Type: NOTSPECIAL or <Special Type>
	/******** Questions parameters to be set: End **********/
	
	public QQQuestionObject(int rounds, int validCount, 
							int[][] op, int startIdx,   
							int qLen, int oLen, QType qType){
		this.rounds 	=  rounds;
		this.validCount = validCount;
		this.op 		= op;
		this.startIdx 	= startIdx;   
		this.qLen 		= qLen;
		this.oLen 		= oLen;
		this.qType 		= qType;
	}

}
