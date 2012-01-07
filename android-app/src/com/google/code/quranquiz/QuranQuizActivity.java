package com.google.code.quranquiz;


import java.io.IOException;

import android.app.Activity;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.code.quranquiz.R;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
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
	 	
		Typeface othmanyFont = Typeface.createFromAsset(getAssets(), "fonts/KacstQurn.ttf");
		tv = (TextView) findViewById(R.id.textView1);
		tv.setTypeface(othmanyFont);
		tv.setText(q.txt(5, 3));
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
	//TODO: Grab the last seed from the loaded profile! (replace -1, level 1)
	int level = 1;
	int lastSeed = -1;
	
	if(QOptIdx == -1 || QOptIdx == 10){
		Quest = new QQQuestion(lastSeed,level,q); 
		
		// Show the Question!
		tv.setText(tv.getText().toString().concat(q.txt(Quest.startIdx,Quest.qLen)));
		QOptIdx = 0;
	}
	
	// Concat correct options to the Question!
	if(QOptIdx>0)
		tv.setText(tv.getText().toString().concat(q.txt(Quest.startIdx+Quest.qLen+QOptIdx-1)+ ' '));
	
    //Scramble options
    int[] scrambled = new int[5];
    scrambled  = QQUtils.randperm(5);
    int correct_choice = QQUtils.findIdx(scrambled,1); //idx=1
    
    //Display Options:
	String strTemp = new String();
	for(int j=0;j<5;j++){
		strTemp = q.txt(Quest.op[QOptIdx][j]) ;
		((RadioButton)rgQQOptions.getChildAt(j)).setText(strTemp);
	}
	
    if(level==2){
        //display(['    -- ',num2str(validCount),' correct options left!']); % TODO: Subtract done options
    }
    else if(level==3 && QOptIdx==1){
        //display('  [-] No more valid Motashabehat!');
    }
    

        // Check if wrong choice
        if(correct_choice != selID){
            //Display Correct answer
    		Toast.makeText(this, q.txt(Quest.startIdx,10), Toast.LENGTH_LONG).show();
            QOptIdx = -1;
        }


	QOptIdx = QOptIdx +1;
}
}