package com.google.code.quranquiz;

import java.util.List;
import java.util.Random;

public class QQQuestion {
	/**
	 * @uml.property  name="op" multiplicity="(0 -1)" dimension="2"
	 */
	public int[][] op = new int[10][5];	// Holds all options for the current Question
	/**
	 * @uml.property  name="startIdx"
	 */
	public int startIdx;		// Precise Position near seed, valid options
	/**
	 * @uml.property  name="validCount"
	 */
	public int validCount;
	/**
	 * @uml.property  name="qLen"
	 */
	public int qLen;					// Length of the Question
	
	/**
	 * @uml.property  name="lastSeed"
	 */
	private int lastSeed;				// Seed for the Question
	/**
	 * @uml.property  name="level"
	 */
	private int level;					// User Level, currently
	/**
	 * @uml.property  name="q"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private QQDataBaseHelper q;			// Reference to the DB
	/**
	 * @uml.property  name="rand"
	 */
	private Random rand;				// Random Generator for the next Question
	
	public QQQuestion(int previousSeed, int userlevel, QQDataBaseHelper qdb){
		
		// If no seed, -ve, in case of a newly created profile
		// Then generate a new random starting point
		if(previousSeed < 0)
			rand = new Random();
		else // resume from the last seed
			rand = new Random(previousSeed);
		
		// Keep Reference of Level, Q
		level = userlevel;
		q = qdb;
		createQ();

	}
	
	private void createQ() {
		lastSeed = rand.nextInt(77797);
		
		// +1 to compensate the rand-gen integer [0-77796]
		startIdx = getValidStartNear(lastSeed+1); 
		
		fillCorrectOptions();
		
		fillIncorrectOptions();
		
	}	

	private int getValidStartNear(int start) {
	    // Search for a correct neighbor start according to level
	    int dir=1; // search down = +1
	    int limitHit=1,disp2,disp3;
	    int start_shadow;
	    boolean srch_cond;
	    while (limitHit>0){
	        start_shadow = start;
	        limitHit=0;
	        srch_cond = true;
	        while (srch_cond){
	            start_shadow = start_shadow+dir;
	            if (start_shadow == 0 || start_shadow == 77798){
	                limitHit=1;
	                dir = -dir;
	                break;
	            }
	            if (level==1){ // Get a non-motashabehat near selected index
	                // Motashabehat found,continue!
	                srch_cond=q.sim2cnt(start_shadow) >1; //TODO: check: Is 0 better? 
	                validCount = 1;	// \
	                qLen   = 3;		// -|-> Default Constants for level-1
	            } else {
	                // Search for a motashabehat near selected index
	                // Specify # Words to display
	                disp2=0;disp3=0;  
	                
	                if(q.sim3cnt(start_shadow)<5 && q.sim3cnt(start_shadow)>0)
	                    disp3=q.sim3cnt(start_shadow);
	                
	                if(q.sim2cnt(start_shadow) <5 && q.sim2cnt(start_shadow)>0)
	                    disp2=q.sim2cnt(start_shadow);
	                
	                // Motashabehat not found,continue!            
	                srch_cond = (disp3==0  && disp2==0);
	                
	                if (srch_cond == false){ // Found!
	                	validCount 	= (disp2>disp3)? disp2+1: disp3+1;// Count start-position as well
	                	qLen		= (disp2>disp3)? 1:2;
	                }
	            } 
	    	}

	        start = start_shadow;       
	    }
	    return start;
	}
	
	private void fillCorrectOptions() {
	    // fill Correct Option Words @indx=1 (2,3,..validCount for higher levels)
		List<Integer> tmp;
	    op[0][0]=startIdx+qLen;
	    for(int k=1;k<10;k++){
	    	op[k][0] = op[k-1][0]+1;
	    }
	    if (level>1){
	        if (qLen==1){ // A 2-word Question
	        	tmp = q.sim2idx(startIdx);
	        	for(int i=1;i<validCount;i++)
	        		op[0][i] = tmp.get(i);
	        }
	        else{  // A 3-word Question
	        	tmp = q.sim3idx(startIdx);
	        	for(int i=1;i<validCount;i++)
	        		op[0][i] = tmp.get(i);
	        	}
    	    for(int k=1;k<10;k++)
    	    	for(int j=1;j<validCount;j++)
    	    		op[k][j]=op[k-1][j]+1;
	    }		
	}
	
	private void fillIncorrectOptions() {    
	    // Get Valid Options @indx=2:5 (validCount+1:5 for higher levels)
	    // Get the next words to similar-to-previous  
	    // Get unique options
		int last_correct,uniq_cnt;
		List<Integer> diffList;
		
	    for(int i=0;i<10;i++){
	        last_correct = op[i][0]-1;
	        
	        // We want to remove redundant correct choices from the given
	        // options, this is made by removing subset sim2 from sim1
	        diffList =q.sim1idx(last_correct);
	        diffList.removeAll(q.sim2idx(last_correct));
	        
	        // '+1' means the next word
	        diffList = q.uniqueWordsList(diffList);
	        diffList = QQUtils.ListPlus(diffList,1);
	        uniq_cnt= diffList.size();
	        
	        int[] rnd_idx = new int[uniq_cnt];
	        Random randg = new Random();
	        
	        if(uniq_cnt > 3){
	            rnd_idx = QQUtils.randperm(uniq_cnt);
	            for(int j=1;j<5;j++){
	            	op[i][j] = diffList.get(rnd_idx[j-1]);
	            }
	        }
	        else if (uniq_cnt > 0){
	            rnd_idx = QQUtils.randperm(uniq_cnt);
	            for(int j=1;j<uniq_cnt+1;j++){
	            	op[i][j] = diffList.get(rnd_idx[j-1]);
	            }
	            for(int j=uniq_cnt+1;j<5;j++){
	            	op[i][j] = randg.nextInt(77799);
	            }
	        }
	        else{ // uniq_cnt=0, all random options!
	        	for(int j=1;j<5;j++)
	            	op[i][j] = randg.nextInt(77799);
	        }
	    }		
	}

}
