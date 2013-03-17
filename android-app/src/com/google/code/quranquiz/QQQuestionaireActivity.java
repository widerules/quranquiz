package com.google.code.quranquiz;

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
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

public class QQQuestionaireActivity extends SherlockActivity implements
		android.view.View.OnClickListener {
	
    private ViewAnimator viewAnimator;
	private TextView tv;
	private TextView tvScore;
	private ProgressBar bar;
	private CountDownTimer cdt;
	private Button[] btnArray;
	private AlertDialog.Builder correctAnswer;
	private ActionBar actionbar;
	private QQDataBaseHelper q;
	private QQQuestion Quest;
	private int QOptIdx = -1;
	private int QQinit = 1;
	// TODO: Grab the last seed from the loaded profile! (replace -1, level 1)
	private int level = 1;
	private int lastSeed = -1;
	private int correct_choice = 0;
	private int CurrentPart = 0;

	private QQProfileHandler myQQProfileHandler;
	private QQProfile myQQProfile;

	public void onClick(View v) {
		int SelID = -2;

		switch (v.getId()) {
		case R.id.bOp1:
			SelID = 0;
			break;
		case R.id.bOp2:
			SelID = 1;
			break;
		case R.id.bOp3:
			SelID = 2;
			break;
		case R.id.bOp4:
			SelID = 3;
			break;
		case R.id.bOp5:
			SelID = 4;
			break;
		}
		if (SelID < 0)
			return;

		userAction(SelID);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// setContentView(R.layout.main);

		// some work that needs to be done on orientation change
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.questionaire_layout);
		actionbar = getSupportActionBar();
	    viewAnimator = (ViewAnimator)this.findViewById(R.id.view_flipper);

		btnArray = new Button[5];
		btnArray[0] = (Button) findViewById(R.id.bOp1);
		btnArray[1] = (Button) findViewById(R.id.bOp2);
		btnArray[2] = (Button) findViewById(R.id.bOp3);
		btnArray[3] = (Button) findViewById(R.id.bOp4);
		btnArray[4] = (Button) findViewById(R.id.bOp5);

		tvScore = (TextView) findViewById(R.id.Score);

		correctAnswer = new AlertDialog.Builder(this);

		correctAnswer.setTitle("الاية المرادة هي");
		correctAnswer.setPositiveButton("حسنا",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// dismiss the dialog
					}
				});
		correctAnswer.setCancelable(true);

		myQQProfileHandler = new QQProfileHandler(this);

		q = new QQDataBaseHelper(this);
		try {
			q.createDataBase();
			
			try {
				q.openDataBase();
			} catch (SQLException sqle) {
			}

		} catch (Exception ioe) {
			finish(); //Cannot download DB, destroy Questionnaire.
			return;
		}

		Typeface othmanyFont = Typeface.createFromAsset(getAssets(),
				"fonts/amiri-quran.ttf");
		
		tv = (TextView) findViewById(R.id.textView1);
		tv.setTypeface(othmanyFont);
		tv.setMovementMethod(new ScrollingMovementMethod()); 
		tv.setSelected(true);
		
		for(int i=0;i<5;i++){
			btnArray[i].setTypeface(othmanyFont);
			btnArray[i].setOnClickListener(this);
		}
		// Make the first Question
		userAction(-1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		if (q != null)
			q.closeDatabase();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent i = new Intent();
			i.putExtra("ProfileHandler", myQQProfileHandler);
			setResult(12345, i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Profile:
			Intent intentStudyList = new Intent(QQQuestionaireActivity.this,
					QQStudyListActivity.class);
			intentStudyList.putExtra("ProfileHandler", myQQProfileHandler);
			startActivity(intentStudyList);
			break;
		case R.id.Settings:
			Intent intentPreferences = new Intent(QQQuestionaireActivity.this,
					QQPreferences.class);
			startActivity(intentPreferences);
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		myQQProfileHandler.reLoadCurrentProfile();
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// profileHandler.saveProfile(prof); // TODO:
	}

	private void showCorrectAnswer(String tmp) {
		correctAnswer.setMessage(tmp);
		correctAnswer.create().show();
	}

	private void updateOptionButtonsColor(int CorrectIdx){
		for(int i=0;i<5;i++){
			if(i==CorrectIdx)
				((Button)btnArray[i]).setBackgroundResource(R.drawable.qqoptionbutton_correct);
			else
				((Button)btnArray[i]).setBackgroundResource(R.drawable.qqoptionbutton_wrong);
		}
	}
	private void startTimer(int fire) {
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setProgress(100);
		bar.setVisibility(View.VISIBLE);

		final int millis = fire * 1000; // milli seconds

		/** CountDownTimer starts with fire seconds and every onTick is 1 second */
		if (cdt != null)
			cdt.cancel();
		cdt = new CountDownTimer(millis, 1000) {
			int cc = 1;

			@Override
			public void onFinish() {
				// DO something when time is up
				bar.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				bar.setProgress((1 - cc * 1000 / millis) * 100);
				cc++;
			}
		}.start();

	}

	private void userAction(int selID) {

		if (QOptIdx >= 0 && correct_choice != selID) {// Wrong choice!!
			String tmp = new String("");

			// Vibrate for 300 milliseconds
			Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			mVibrator.vibrate(300);

			// Display Correct answer
			tmp = "[" + QQUtils.getSuraName(Quest.startIdx) + "] "
					+ q.txt(Quest.startIdx, 12 * Quest.oLen + Quest.qLen)
					+ " ...";
			showCorrectAnswer(tmp);

			QOptIdx = -1; // trigger a new question
		} else {
			QOptIdx = (QOptIdx == -1) ? -1 : QOptIdx + 1; // Keep -1, or Proceed
															// with options ..
		}

		if (QOptIdx == -1 || QOptIdx == 10) {
			myQQProfile = myQQProfileHandler.getProfile();
			
			if (QQinit == 0 && QOptIdx == -1) { // A wrong answer
				myQQProfile.addIncorrect(CurrentPart);

			} else { // A correct answer
				if(QOptIdx == 10)
					myQQProfile.addCorrect(CurrentPart);
			}

			Quest = new QQQuestion(myQQProfile, q);
			CurrentPart = Quest.CurrentPart;
			
			// Update profile after a new Question!
			lastSeed = Quest.getSeed();
			myQQProfile.setLastSeed(lastSeed);

			// Update the Score
			tvScore.setText(String.valueOf(myQQProfile.getScore()));

			myQQProfileHandler.saveProfile(myQQProfile); // TODO: Do I need to
															// save after each
															// question? On exit
															// only?

            AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);
            AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);

			// Show the Question!
			tv.setText(q.txt(Quest.startIdx, Quest.qLen));
			QOptIdx = 0;
		}

		// Concat correct options to the Question!
		if (QOptIdx > 0)
			// I use 3 spaces with quran_me font, or a single space elsewhere
			tv.setText(tv
					.getText()
					.toString()
					.concat(""//"   "
							+ q.txt(Quest.startIdx + Quest.qLen + (QOptIdx - 1)
									* Quest.oLen, Quest.oLen)// + "   "
							));

		// Scramble options
		int[] scrambled = new int[5];
		scrambled = QQUtils.randperm(5);
		correct_choice = QQUtils.findIdx(scrambled, 0); // idx=1

		// Display Options:
		String strTemp = new String();
		for (int j = 0; j < 5; j++) {
			strTemp = q.txt(Quest.op[QOptIdx][scrambled[j]], Quest.oLen);
			btnArray[j].setText(strTemp);
		}
		updateOptionButtonsColor(correct_choice); //Update background Color
		
		if (level == 3) {
			// Start the timer
			startTimer(5);
			if (QOptIdx == 1) {
				// display(" [-] No more valid Motashabehat!");
			} else {
				// display([' -- ',num2str(validCount),' correct options
				// left!']); // TODO: Subtract done options
			}
		}

		QQinit = 0;

	}

}