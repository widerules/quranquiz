package com.google.code.quranquiz;


import java.io.IOException;

import android.app.Activity;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.code.quranquiz.R;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuranQuizActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

	private TextView tv;
	private RadioGroup rgQQOptions;
	private QQDataBaseHelper q;
	private QQQuestion Quest;
	private int QOptIdx=-1;
	//TODO: Grab the last seed from the loaded profile! (replace -1, level 1)
	private int level = 1;
	private int lastSeed = -1;
	private int correct_choice=0;
	
	private Toast correctAnswerToast;
	private QQProfileHandler profileHandler;
	private QQProfile myQQProfile;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
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
	        case R.id.Profile:     Toast.makeText(this, "Going to Profile!", Toast.LENGTH_LONG).show();
	                            break;
	        case R.id.Settings:     Toast.makeText(this, "Edit Settings!", Toast.LENGTH_LONG).show();
	                            break;
	    }
	    return true;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		profileHandler = new QQProfileHandler(this);
        
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
		tv = (TextView) findViewById(R.id.radioOp1);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.radioOp2);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.radioOp3);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.radioOp4);
		tv.setTypeface(othmanyFont);
		tv = (TextView) findViewById(R.id.radioOp5);
		tv.setTypeface(othmanyFont);
		
		tv = (TextView) findViewById(R.id.textView1);
		
		rgQQOptions = (RadioGroup) findViewById(R.id.radioQQOptions);
		
		//Prepare the Toast for displaying correct answers
		correctAnswerToast = Toast.makeText(this,"", Toast.LENGTH_LONG);
		
		// Make the first Question
		userAction(-1);
		// Set action Listener
		rgQQOptions.setOnCheckedChangeListener(this);

	}

	@Override
	protected void onDestroy() {
		if(q != null)q.closeDatabase();
		super.onDestroy();
	}
	
	public void onCheckedChanged(RadioGroup rg, int CheckedID) {
		int SelID=-2;

		switch(CheckedID){
		case R.id.radioOp1:
			SelID=0;
			break;
		case R.id.radioOp2:
			SelID=1;
			break;
		case R.id.radioOp3:
			SelID=2;
			break;
		case R.id.radioOp4:
			SelID=3;
			break;
		case R.id.radioOp5:
			SelID=4;
			break;
		}
		if( SelID<0)
			return;
		
		userAction(SelID);
		//rgQQOptions.clearCheck();
		((RadioButton)rgQQOptions.getChildAt(SelID)).setChecked(false);
	}

private void userAction(int selID) {
	
	// Cancel any previously showing toasts
	correctAnswerToast.cancel();
	
    if(QOptIdx >= 0 && correct_choice != selID){// Check if wrong choice
    	String tmp = new String("");
        //Display Correct answer
    	tmp = "["+QQUtils.getSuraName(Quest.startIdx)+"] "+q.txt(Quest.startIdx,10+Quest.qLen);
		correctAnswerToast.setText(tmp);
    	for(int i=0;i<2;i++){ //TODO: Fix duration!
    		correctAnswerToast.show();
		}
        QOptIdx = -1; // trigger a new question
    }
    else{
    	QOptIdx = (QOptIdx==-1)?-1:QOptIdx +1; //Proceed with options ..
    }
	
	if(QOptIdx == -1 || QOptIdx == 10){
		myQQProfile = profileHandler.getProfile();
		Quest = new QQQuestion(myQQProfile,q);
		
		//Update profile after a new Question!
		lastSeed = Quest.getSeed();
		
		myQQProfile.setLastSeed(lastSeed);
		myQQProfile.setQuesCount(myQQProfile.getQuesCount()+1);
		//TODO: Update the score !!
		
		profileHandler.saveProfile(myQQProfile); //TODO: Do I need to save after each question? On exit only?
		
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
		((RadioButton)rgQQOptions.getChildAt(j)).setText(strTemp);
	}
	
    if(level==2){
        //display(['    -- ',num2str(validCount),' correct options left!']); % TODO: Subtract done options
    }
    else if(level==3 && QOptIdx==1){
        //display('  [-] No more valid Motashabehat!');
    }

}
}