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

public class QuranQuizActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

	private TextView tv;
	private RadioGroup rgQQOptions;
	private QQDataBaseHelper q;
	private int i=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Typeface othmanyFont = Typeface.createFromAsset(getAssets(), "fonts/KacstQurn.ttf");
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
		
		rgQQOptions = (RadioGroup) findViewById(R.id.radioQQOptions);
		rgQQOptions.setOnCheckedChangeListener(this);
		
		
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
	i = i+selID+1;
	String strTemp = new String();
	for(int j=0;j<5;j++){
		strTemp = q.txt(i+j);
		((RadioButton)rgQQOptions.getChildAt(j)).setText(strTemp);
	}
	
}
}