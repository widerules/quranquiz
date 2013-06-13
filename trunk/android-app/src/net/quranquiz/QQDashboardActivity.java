/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;

public class QQDashboardActivity extends Activity {

	private QQProfileHandler myQQProfileHandler = null;
	private FeedbackDialog feedBackDialog;
	// Function to read the result from newly created activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 12345) {
			myQQProfileHandler = (QQProfileHandler) data.getExtras().get(
					"ProfileHandler");
		}
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    feedBackDialog.dismiss();
	} 
	
	@Override
	public void onBackPressed() {
	    feedBackDialog.dismiss();
		if (myQQProfileHandler != null) {
			Intent lastIntent = new Intent(QQDashboardActivity.this,
					QQLastScreenActivity.class);
			lastIntent.putExtra("ProfileHandler", myQQProfileHandler);
			startActivity(lastIntent);
		}
		finish();
	}

	  @Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance().activityStart(this);
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance().activityStop(this);
	  }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dashboard_layout);

		/**
		 * Creating all buttons instances
		 * */
		Button btnQQQuestionaire = (Button) findViewById(R.id.btnQQQuestionaire);
		Button btnSettings = (Button) findViewById(R.id.btnSettings);
		Button btnScoreHistory = (Button) findViewById(R.id.btnScoreHistory);
		Button btnInfo = (Button) findViewById(R.id.btnInfo);
		
		FeedbackSettings feedbackSettings = new FeedbackSettings();
		feedbackSettings.setBugLabel("مشكلة");
		feedbackSettings.setIdeaLabel("فكرة");
		feedbackSettings.setQuestionLabel("سؤال");
		feedbackSettings.setYourComments("اكتب رسالتك");
		feedbackSettings.setCancelButtonText("إلغاء");
		feedbackSettings.setSendButtonText("ارسال");
		feedbackSettings.setText("هل واجهتك متاعب مع اختبار القران؟ تريد توصيل سؤال أو فكرة؟");
		feedbackSettings.setTitle("شاركنا برأيك");
		feedbackSettings.setToast("شكرا جزيلا!");
		feedBackDialog = new FeedbackDialog(this,"AF-F8C9921F6163-17",feedbackSettings);
		
		/**
		 * Handling all button click events
		 * */
		btnQQQuestionaire.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						QQQuestionaireActivity.class);
				startActivityForResult(i, 12345);
			}
		});

		btnSettings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (myQQProfileHandler == null) {
					myQQProfileHandler = new QQProfileHandler(
							getApplicationContext());
					myQQProfileHandler.getProfile();
				}
				Intent intentStudyList = new Intent(QQDashboardActivity.this,
						QQStudyListActivity.class);
				intentStudyList.putExtra("ProfileHandler", myQQProfileHandler);
				startActivity(intentStudyList);
			}
		});

		// Listening Messages button click
		btnScoreHistory.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (myQQProfileHandler == null) {
					myQQProfileHandler = new QQProfileHandler(
							getApplicationContext());
					myQQProfileHandler.getProfile();
				}
				startActivity((new QQScoreChart(
						myQQProfileHandler.CurrentProfile))
						.execute(getApplicationContext()));
			}
		});
		
		btnInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(getApplicationContext(), "تحت التطوير", Toast.LENGTH_SHORT).show();
				feedBackDialog.show();
			}
		});
	}
}