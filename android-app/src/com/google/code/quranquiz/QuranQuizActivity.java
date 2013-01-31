package com.google.code.quranquiz;


import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.code.quranquiz.R;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class QuranQuizActivity extends SherlockActivity implements android.view.View.OnClickListener {

	private TextView tv;
	private TextView tvScore;
	private ProgressBar bar;
	private CountDownTimer cdt;
    private Button[] btnArray;
    private AlertDialog.Builder correctAnswer;
    private ActionBar actionbar;
	private QQDataBaseHelper q;
	private QQQuestion Quest;
	private int QOptIdx=-1;
	private int QQinit=1;
	//TODO: Grab the last seed from the loaded profile! (replace -1, level 1)
	private int level = 1;
	private int lastSeed = -1;
	private int correct_choice=0;
	private int CurrentPart = 0;
	
	private QQProfileHandler myQQProfileHandler;
	private QQProfile myQQProfile;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater  inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.menu, (Menu) menu);
	    return true;
	}
	
	@Override
    protected void onStop(){
       super.onStop();
       //profileHandler.saveProfile(prof); // TODO:
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.Profile:     
	        	Intent intentStudyList = new Intent(QuranQuizActivity.this,
	        	QQStudyList.class);
	        	intentStudyList.putExtra("ProfileHandler", myQQProfileHandler);
	        	startActivity(intentStudyList);
                break;
	        case R.id.Settings:
	        	Intent intentPreferences = new Intent(QuranQuizActivity.this,
	        	QQPreferences.class);
	        	startActivity(intentPreferences);
	        	break;
	    }
	    return true;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		actionbar = getSupportActionBar();
		
		btnArray = new Button[5];
		btnArray[0] = (Button)findViewById(R.id.bOp1); 
		btnArray[1] = (Button)findViewById(R.id.bOp2); 
		btnArray[2] = (Button)findViewById(R.id.bOp3); 
		btnArray[3] = (Button)findViewById(R.id.bOp4); 
		btnArray[4] = (Button)findViewById(R.id.bOp5); 
		
		tvScore = (TextView) findViewById(R.id.Score);
		
		correctAnswer  = new AlertDialog.Builder(this);

		correctAnswer.setTitle("الاية المرادة هي");
		correctAnswer.setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            //dismiss the dialog  
	          }
	      });
		correctAnswer.setCancelable(true);
		


		
		myQQProfileHandler = new QQProfileHandler(this);
        
		q = new QQDataBaseHelper(this);
        try {
        	q.createDataBase();
 
        } catch(IOException ioe) {
        	throw new Error("Unable to create database");
        }
 
	 	try {
	 		q.openDataBase();
	 	} catch(SQLException sqle) {
	 		throw sqle;
	 	}
	 	
		Typeface othmanyFont = Typeface.createFromAsset(getAssets(), "fonts/me_quran.ttf");
		tv = (TextView) findViewById(R.id.textView1);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.bOp1);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.bOp2);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.bOp3);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.bOp4);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.bOp5);
		tv.setTypeface(othmanyFont);
		
		tv = (TextView) findViewById(R.id.textView1);
			
		Toast.makeText(this,"", Toast.LENGTH_LONG);
		
		// Make the first Question
		userAction(-1);
		
		// Set action Listener
		( (Button)findViewById(R.id.bOp1)).setOnClickListener(this);
		( (Button)findViewById(R.id.bOp2)).setOnClickListener(this);
		( (Button)findViewById(R.id.bOp3)).setOnClickListener(this);
		( (Button)findViewById(R.id.bOp4)).setOnClickListener(this);
		( (Button)findViewById(R.id.bOp5)).setOnClickListener(this);
		
	}

	@Override
	protected void onDestroy() {
		if(q != null)q.closeDatabase();
		super.onDestroy();
	}
	
	protected void onResume(){
		myQQProfileHandler.reLoadCurrentProfile();
		super.onResume();
	}
	
private void userAction(int selID) {
	
    if(QOptIdx >= 0 && correct_choice != selID){//Wrong choice!!
    	String tmp = new String("");
    	
    	// Vibrate for 300 milliseconds
    	Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
    	mVibrator.vibrate(300);
    	
        //Display Correct answer
    	tmp = "["+QQUtils.getSuraName(Quest.startIdx)+"] "+
    				q.txt(Quest.startIdx,12*Quest.oLen+Quest.qLen)+" ...";
		showCorrectAnswer(tmp);
		
        QOptIdx = -1; // trigger a new question
    }
    else{
    	QOptIdx = (QOptIdx==-1)?-1:QOptIdx +1; //Keep -1, or Proceed with options ..
    }
	
	if(QOptIdx == -1 || QOptIdx == 10){
		myQQProfile = myQQProfileHandler.getProfile();
		Quest = new QQQuestion(myQQProfile,q);
		CurrentPart = Quest.CurrentPart;
		
		if(QQinit == 0 && QOptIdx == -1){ // A wrong answer
			myQQProfile.addIncorrect(CurrentPart);

		}else{ // A correct answer
			//TODO: Bug: +1 with every new ?
			myQQProfile.addCorrect(CurrentPart);
		}
		
		//Update profile after a new Question!
		lastSeed = Quest.getSeed();
		myQQProfile.setLastSeed(lastSeed);

		// Update the Score
		tvScore.setText(String.valueOf(myQQProfile.getScore()));
		
		myQQProfileHandler.saveProfile(myQQProfile); //TODO: Do I need to save after each question? On exit only?
		
		// Show the Question!
		tv.setText(q.txt(Quest.startIdx,Quest.qLen));
		QOptIdx = 0;
	}
	
	// Concat correct options to the Question!
	if(QOptIdx>0)
		// I use 3 spaces with quran_me font, or a single space elsewhere
		tv.setText(tv.getText().toString().concat(
				"   " +
				q.txt(Quest.startIdx+Quest.qLen+(QOptIdx-1)*Quest.oLen,Quest.oLen)+
				"   "));

	//Scramble options
    int[] scrambled = new int[5];
    scrambled  = QQUtils.randperm(5);
    correct_choice = QQUtils.findIdx(scrambled,0); //idx=1
    
    //Display Options:
	String strTemp = new String();
	for(int j=0;j<5;j++){
		strTemp = q.txt(Quest.op[QOptIdx][scrambled[j]],Quest.oLen) ;
		btnArray[j].setText(strTemp);
	}
	
    if(level==3){
    	if(QOptIdx==1){
            //display(" [-] No more valid Motashabehat!");
        }else{
        	//display(['    -- ',num2str(validCount),' correct options left!']); // TODO: Subtract done options
        }
    }


	// Start the timer
	startTimer(5);
	
	QQinit = 0;

}

private void showCorrectAnswer(String tmp) {
	correctAnswer.setMessage(tmp);
	correctAnswer.create().show();	
}

public void onClick(View v) {
	int SelID=-2;

	switch(v.getId()){
	case R.id.bOp1:
		SelID=0;
		break;
	case R.id.bOp2:
		SelID=1;
		break;
	case R.id.bOp3:
		SelID=2;
		break;
	case R.id.bOp4:
		SelID=3;
		break;
	case R.id.bOp5:
		SelID=4;
		break;
	}
	if( SelID<0)
		return;
	
	userAction(SelID);	
}

private void startTimer(int fire){
	bar = (ProgressBar) findViewById(R.id.progressBar1);
    bar.setProgress(100);
    bar.setVisibility(View.VISIBLE);

    final int millis = fire * 1000; // milli seconds

    /** CountDownTimer starts with fire seconds and every onTick is 1 second */
    if (cdt != null)
    	cdt.cancel();
    cdt = new CountDownTimer(millis, 1000) { 
    	int cc=1;
    		public void onTick(long millisUntilFinished) {
    			bar.setProgress((1-cc*1000/millis)*100);
    			cc++;
        }

        public void onFinish() {
            // DO something when time is up
        	bar.setVisibility(View.INVISIBLE);
        }
    }.start();

}

@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    //setContentView(R.layout.main);

    // some work that needs to be done on orientation change
}

}