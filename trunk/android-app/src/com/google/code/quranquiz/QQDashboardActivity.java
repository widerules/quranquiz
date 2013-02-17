package com.google.code.quranquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class QQDashboardActivity extends Activity {
	
	private QQProfileHandler myQQProfileHandler=null;

    
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
             
        /**
         * Handling all button click events
         * */
        
        btnQQQuestionaire.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), QQQuestionaireActivity.class);
				startActivityForResult(i,12345);
			}
		});
        
        btnSettings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(getApplicationContext(), "TODO: Open Settings!", Toast.LENGTH_SHORT).show();
			}
		});
        
        // Listening Messages button click
        btnScoreHistory.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				Toast.makeText(getApplicationContext(), "TODO: Open History!", Toast.LENGTH_SHORT).show();
			}
		});       
     }
    
 // Function to read the result from newly created activity
    @Override
        protected void onActivityResult(int requestCode,
                                         int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == 12345){
            	myQQProfileHandler = (QQProfileHandler)data.getExtras().get("ProfileHandler");
            }
     
        }
	@Override
	public void onBackPressed() {
		if(myQQProfileHandler != null){
			   Intent lastIntent = new Intent(QQDashboardActivity.this, QQLastScreenActivity.class);
			   lastIntent.putExtra("ProfileHandler", myQQProfileHandler);
			   startActivity(lastIntent);	
		}
	   finish();
	}
}
