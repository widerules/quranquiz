/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz;

import java.util.List;
import java.util.Random;

public class QQQuestionaire {

	/******** Questions parameters to be set: Start **********/
	public int rounds; // How many rounds a question has: 10 for normal, 1 for special
	public int validCount; //Number of correct options at the first round
	public int[][] op = new int[10][5]; // Holds all 5 options x 10 rounds
	public int startIdx; // Precise Position near seed, valid options
	public int qLen; // Number of words to display to start the Question
	public int oLen; // Number of words of each option
	public QType qType; // Question Type: NOTSPECIAL or <Special Type>
	public int CurrentPart;
	/******** Questions parameters to be set: End **********/

	public QQQuestionObject qo;

	private int lastSeed; // Seed for the Question
	private int level; // User Level, currently
	private static QQDataBaseHelper q; // Reference to the DB
	private static QQSession session;  // Reference to the QQSession
	private Random rand;
	private QQSparseResult sparsed;
	private static int QLEN_EXTRA_LIMIT=2;

	public enum QType { 
		NOTSPECIAL, SURANAME, SURAAYACOUNT, MAKKI, AYANUMBER;
		public int getScore(){
			switch(this){
				case SURANAME: 		return 2;
				case SURAAYACOUNT: 	return 4;
				case MAKKI: 		return 2;
				case AYANUMBER:		return 7;
				default:			return 0;
			}
		}	
		public String getInstructions(){
			switch(this){
				case SURANAME: 		
					return QQApp.getContext().getResources().getString(R.string.txt_instruction_SURANAME);
				case SURAAYACOUNT: 	
					return QQApp.getContext().getResources().getString(R.string.txt_instruction_SURAAYACOUNT);
				case MAKKI: 		
					return QQApp.getContext().getResources().getString(R.string.txt_instruction_MAKKI);
				case AYANUMBER:		
					return QQApp.getContext().getResources().getString(R.string.txt_instruction_AYANUMBER);
				default:			
					return QQApp.getContext().getResources().getString(R.string.txt_instruction);
			}
		}
	};
		
	public QQQuestionaire(QQProfile prof, QQDataBaseHelper qdb, QQSession s) {
		// start tracing to "/sdcard/calc.trace"
		// Debug.startMethodTracing("QQ.trace");

		// resume from the last seed
		int previousSeed = prof.getLastSeed();
		rand = new Random(previousSeed);

		// Keep Reference of Level, Q
		level = prof.getLevel();
		if(qdb != null) q = qdb;
		if(s != null) session = s;
		
		createQ(prof);

		//android.util.Log.d("INFO", "QQ @" + startIdx + " type:" + qType.name());

		// stop tracing
		// Debug.stopMethodTracing();
	}

	public int getSeed() {
		return lastSeed;
	}
	
	public static QQQuestionObject createDefinedQuestion(int part) {
		QQProfile profileSinglePart;
		profileSinglePart = new QQProfile(null, new Random().nextInt(),
											QQUtils.getRandomLevel(),
											QQProfileHandler.getStudyPartFromIndex(part),
											QQScoreRecord.getInitScoreRecordPack(),
											0);
		QQQuestionaire tmp = new QQQuestionaire(profileSinglePart, null, null);
		return tmp.qo ;
	}
	
	private QQQuestionObject createQ(QQProfile prof) {
		if(prof.isSpecialEnabled() && selectSpecial()){
			createSpecialQ(prof);
		}else {
			createNormalQ(prof);
		}
		qo = new 	QQQuestionObject(rounds, validCount, op, startIdx, qLen, oLen, qType);
		return qo;
	}

	private boolean selectSpecial() {
		if(Math.random()>0.20) 
			return false;
		else
			return true;
	}
	
	private void createSpecialQ(QQProfile prof) {
		rounds = 1;

		if(Math.random()>0.5)
			qType = QType.SURANAME; 
		else if(Math.random()>0.3)
			qType = QType.SURAAYACOUNT;
		else
			qType = QType.AYANUMBER;
		
		switch(qType){
			case SURANAME: 
				createQSuraName(prof);
				break;
			case SURAAYACOUNT:
				createQSuraAyaCount(prof);
				break;
			case AYANUMBER:
				createQAyaNumber(prof);
				break;
			//TODO: Implement others	
			default:
				qType = QType.SURANAME;
				createQSuraName(prof);
				break;	
		}
	}
	
	private void createQSuraName(QQProfile prof) {
		do{
			sparsed = prof.getSparsePoint(rand.nextInt(prof
					.getTotalStudyLength()));
			lastSeed = sparsed.idx;
			CurrentPart = sparsed.part;
			// +1 to compensate the rand-gen integer [0-QuranWords-1]
			startIdx = getValidUniqueStartNear(lastSeed + 1);
		}while(!session.addIfNew(startIdx));

		validCount=1; //Number of correct options at the first round
		qLen=(level==1)?3:2;
		oLen=1;
		//Correct Answer:
		op[0][0] = QQUtils.getSuraIdx(startIdx);
		//Incorrect Answers		
		fillIncorrectRandomIdx(op[0][0],114);
	}

	private void createQSuraAyaCount(QQProfile prof) {
		do{
			sparsed = prof.getSparsePoint(rand.nextInt(prof
					.getTotalStudyLength()));
			lastSeed = sparsed.idx;
			CurrentPart = sparsed.part;
			// +1 to compensate the rand-gen integer [0-QuranWords-1]
			startIdx = getValidUniqueStartNear(lastSeed + 1);
		}while(!session.addIfNew(startIdx));

		validCount=1; //Number of correct options at the first round
		qLen=(level==1)?3:2;
		oLen=1;
		//Correct Answer:
		op[0][0] = q.ayaCountOfSuraAt(startIdx);
		//Incorrect Answers		
		fillIncorrectRandomNonZeroIdx(op[0][0],50);
	}

	private void createQAyaNumber(QQProfile prof) {
		do{
			sparsed = prof.getSparsePoint(rand.nextInt(prof
					.getTotalStudyLength()));
			lastSeed = sparsed.idx;
			CurrentPart = sparsed.part;
			// +1 to compensate the rand-gen integer [0-QuranWords-1]
			startIdx = getValidUniqueStartNear(lastSeed + 1);
		}while(!session.addIfNew(startIdx));
		
		validCount=1; //Number of correct options at the first round
		qLen=(level==1)?3:2;
		oLen=1;
		//Correct Answer:
		op[0][0] = q.ayaNumberOf(startIdx);
		//Incorrect Answers		
		fillIncorrectRandomNonZeroIdx(op[0][0],50);
	}
	
	private int getValidUniqueStartNear(int start) {
		// Search for a correct neighbor unique start
		int dir = 1; // search down = +1
		int limitHit = 1;
		int start_shadow;
		boolean srch_cond;
		while (limitHit > 0) {
			start_shadow = start;
			limitHit = 0;
			srch_cond = true;
			while (srch_cond) {
				start_shadow = start_shadow + dir;
				if (start_shadow == 0 || start_shadow == QQUtils.QuranWords - 1) {
					limitHit = 1;
					dir = -dir;
					break;
				}
				srch_cond = q.sim2cnt(start_shadow) > 0;
			}

			start = start_shadow;
		}
		return start;
	}

	private void createNormalQ(QQProfile prof) {
		rounds = 10;
		qType = QType.NOTSPECIAL;
		
		do{
			sparsed= prof.getSparsePoint(rand.nextInt(
										prof.getTotalStudyLength()));
			lastSeed = sparsed.idx;
			CurrentPart = sparsed.part;
	
			// +1 to compensate the rand-gen integer [0-QuranWords-1]
			startIdx = getValidStartNear(lastSeed + 1);
		}while(!session.addIfNew(startIdx));
		
		fillCorrectOptions();
		fillIncorrectOptions();
	}

	private void fillCorrectOptions() {
		// fill Correct Option Words @indx=1 (2,3,..validCount for higher
		// levels)
		List<Integer> tmp;
		int correct;
		op[0][0] = startIdx + qLen;
		for (int k = 1; k < 10; k++) {
			correct = op[k - 1][0] + oLen;
			if (correct>QQUtils.QuranWords)
				op[k][0] = correct - QQUtils.QuranWords;
			else
				op[k][0] = correct;
		}
		if (level > 1) {
			if (qLen == 1) { // A 2-word Question
				tmp = q.sim2idx(startIdx);
				for (int i = 1; i < validCount; i++)
					op[0][i] = tmp.get(i);
			} else { // A 3-word Question
				tmp = q.sim3idx(startIdx);
				for (int i = 1; i < validCount; i++)
					op[0][i] = tmp.get(i);
			}
			for (int k = 1; k < 10; k++)
				for (int j = 1; j < validCount; j++)
					op[k][j] = op[k - 1][j] + 1;
		}
	}

	private void fillIncorrectOptions() {
		// Get Valid Options @indx=2:5 (validCount+1:5 for higher levels)
		// Get the next words to similar-to-previous
		// Get unique options
		int last_correct, uniq_cnt;
		List<Integer> diffList;
		List<Integer> randList;

		for (int i = 0; i < 10; i++) {
			last_correct = op[i][0] - 1;

			// We want to remove redundant correct choices from the given
			// options, this is made by removing subset sim2 from sim1
			// then finding the next unique set of words
			diffList = q.uniqueSim1Not2Plus1(last_correct);

			uniq_cnt = diffList.size();

			int[] rnd_idx = new int[uniq_cnt];

			if (uniq_cnt > 3) {
				rnd_idx = QQUtils.randperm(uniq_cnt);
				for (int j = 1; j < 5; j++) {
					op[i][j] = diffList.get(rnd_idx[j - 1]);
				}
			} else{
				// We need Random unique and does not match correct
				randList = q.randomUnique4NotMatching(op[i][0]);			
				if (uniq_cnt > 0) {
					rnd_idx = QQUtils.randperm(uniq_cnt);
					for (int j = 1; j < uniq_cnt + 1; j++) {
						op[i][j] = diffList.get(rnd_idx[j - 1]);
					}
					for (int j = uniq_cnt + 1; j < 5; j++) {
						op[i][j] = randList.get(j-uniq_cnt-1);
					}
				} else { // uniq_cnt=0, all random options!
					for (int j = 1; j < 5; j++)
						op[i][j] = randList.get(j-uniq_cnt-1);				}
			}
		}
	}
	
	private void fillIncorrectRandomIdx(int correctIdx,int mod){
		int[] perm = new int[5];
		int[] rndIdx = {-1,1,0,5,-4};

		perm = QQUtils.randperm(5);
		// Adding QuranWords does not affect the %QuranWords, but eliminates -ve values
		int correctIdxPerm = mod + correctIdx - rndIdx[perm[0]];
		
		op[0][1] = (correctIdxPerm + rndIdx[perm[1]])%mod;
		op[0][2] = (correctIdxPerm + rndIdx[perm[2]])%mod;
		op[0][3] = (correctIdxPerm + rndIdx[perm[3]])%mod;
		op[0][4] = (correctIdxPerm + rndIdx[perm[4]])%mod;
	}

	private void fillIncorrectRandomNonZeroIdx(int correctIdx,int mod){
		int[] perm = new int[5];
		int[] rndIdx = {-1,1,0,5,-4};

		perm = QQUtils.randperm(5);
		// Adding QuranWords does not affect the %QuranWords, but eliminates -ve values
		int correctIdxPerm = mod + correctIdx - rndIdx[perm[0]];
		
		for(int i=1;i<5;i++)
			if((correctIdx+rndIdx[perm[i]])==rndIdx[perm[0]]){
				//only one may give a zero, fix and break
				rndIdx[i] = 2;
				break;
			}
				
		op[0][1] = (correctIdxPerm + rndIdx[perm[1]])%mod;
		op[0][2] = (correctIdxPerm + rndIdx[perm[2]])%mod;
		op[0][3] = (correctIdxPerm + rndIdx[perm[3]])%mod;
		op[0][4] = (correctIdxPerm + rndIdx[perm[4]])%mod;
	}
	
	private int getValidStartNear(int start) {
		// Search for a correct neighbor start according to level
		int dir = 1; // search down = +1
		int limitHit = 1, disp2, disp3;
		int start_shadow;
		int extraLength;
		boolean srch_cond;
		while (limitHit > 0) {
			start_shadow = start;
			limitHit = 0;
			srch_cond = true;
			while (srch_cond) {
				start_shadow = start_shadow + dir;
				if (start_shadow == 0 || start_shadow == QQUtils.QuranWords - 1) {
					limitHit = 1;
					dir = -dir;
					break;
				}
				if (level == 1) { // Get a non-motashabehat near selected index

					// Motashabehat found,continue!
					srch_cond = q.sim2cnt(start_shadow) > 1;
					validCount = 1; // \
					qLen = 3; // -|-> Default Constants for level-1
					oLen = 2;

				} else if (level == 2) {

					// Motashabehat found, srch_cond=false, continue!
					srch_cond = q.sim2cnt(start_shadow) > 1;
					if(!srch_cond){
						validCount = 1; // \
						qLen = 2; // -|-> Default Constants for level-2
						oLen = 1;
						extraLength = extraQLength(start_shadow, qLen);
						if(extraLength>-1){
							qLen +=extraLength;
							start_shadow -=extraLength;
						} else {
							// Too Long Motashabehat, cannot start within, non-unique answer
							srch_cond = true; 
						}
					}
				} else {
					// Search for a motashabehat near selected index
					// Specify # Words to display
					disp2 = 0;
					disp3 = 0;

					if (q.sim3cnt(start_shadow) < 5
							&& q.sim3cnt(start_shadow) > 0)
						disp3 = q.sim3cnt(start_shadow);

					if (q.sim2cnt(start_shadow) < 5
							&& q.sim2cnt(start_shadow) > 0)
						disp2 = q.sim2cnt(start_shadow);

					// Motashabehat not found,continue!
					srch_cond = (disp3 == 0 && disp2 == 0);

					if (srch_cond == false) { // Found!
						validCount = (disp2 > disp3) ? disp2 : disp3;// TODO:
																		// Check,
																		// +1
																		// caused
																		// bound
																		// excep
						qLen = (disp2 > disp3) ? 1 : 2;
					}
					oLen = 1;
				}
			}

			start = start_shadow;
		}
		return start;
	}

	private int extraQLength(int start, int qLen) {
		int extra=0;
		while((extra<QLEN_EXTRA_LIMIT) && (q.sim3cnt(--start)>0))
			extra ++;
		
		if(extra==QLEN_EXTRA_LIMIT)
			return -1;
		else if (extra==0)
			return 0;
		else
			return extra+1;
	}

}
