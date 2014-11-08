/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.ui;

import net.quranquiz.R;
import net.quranquiz.model.ViewModel;
import net.quranquiz.util.QQApp;
import net.quranquiz.util.QQUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewAnimator;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

public class QQQuestionaireActivity extends SherlockFragmentActivity implements
		android.view.View.OnClickListener, OnNavigationListener {
	
	private View ll_background;
    private ViewAnimator viewAnimator;
	private TextView tvQ;
	private TextView tvScore;
	private TextView tvScoreUp;
	private TextView tvScoreDown;
	private TextView tvSymScore;
	private TextView tvBack;
	private TextView tvInstructions;
	private Button btnBack;
	private Button btnBackReview;
	private ProgressBar bar;
	private TextView tvCountUp;
	private TextView tvCountUpTenths;
	public VerticalProgressBar leftBar;
	private ImageView ivSymScoreUp;
	private ImageView ivSymScoreDown;
	private Button[] btnArray;
	private ActionBar actionbar;
	private String quranReviewUri = "1/1"; // Sura/Aya
	private AlertDialog.Builder builder;
	private long startTime = 0L;
	private Handler countUpHandler = new Handler();
	long timeInMillies = 0L;
	private ViewModel vm;
	
	public void onClick(View v) {
		int SelID = -2;
		switch (v.getId()) {
			case R.id.bOp1:			SelID = 0;	break;
			case R.id.bOp2:			SelID = 1;	break;
			case R.id.bOp3:			SelID = 2;	break;
			case R.id.bOp4:			SelID = 3;	break;
			case R.id.bOp5:			SelID = 4;	break;
		}
		if (SelID < 0)
			return;
		vm.setUserSelection(SelID);
	}

	 private Runnable updateTimerMethod = new Runnable() {

		  public void run() {
		   timeInMillies = SystemClock.uptimeMillis() - startTime;

		   int seconds = (int) (timeInMillies / 1000);
		   int minutes = seconds / 60;
		   seconds = seconds % 60;
		   int tenths = (int) ((timeInMillies % 1000)/100);
		   tvCountUp.setText(String.format("%02d", minutes) + ":"
		     + String.format("%02d", seconds));
		   tvCountUpTenths.setText("."
		     + String.format("%01d", tenths));
		   countUpHandler.postDelayed(this, 100);
		  }
	 };
	 
	public void vmShowUsage(){
		Thread splashTread = new Thread() {
            public void run() {
                try {
            		Intent instructionIntent = new Intent(QQQuestionaireActivity.this,
            				QQInstructionsActivity.class);
            		startActivity(instructionIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        splashTread.start();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// some work that needs to be done on orientation change
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.questionaire_layout);
				
		initUI();

		vm = new ViewModel(this);

		if(android.os.Build.VERSION.SDK_INT 
				>= android.os.Build.VERSION_CODES.HONEYCOMB)
			QQUtils.disableFixQ();	
	}

	/**
	 * Initialize User-Interface components, inflate all needed resources
	 * and register all needed listeners. This includes the action bar
	 * and the navigation list.
	 */
	private void initUI() {

		actionbar 		= getSupportActionBar();
		ll_background 	= (View)findViewById(R.id.ll_back);
	    viewAnimator 	= (ViewAnimator)findViewById(R.id.view_flipper);
	    
		bar 			= (ProgressBar) findViewById(R.id.dailyQuizProgress);
		tvCountUp		= (TextView) findViewById(R.id.dailyProgressCountUp);
		tvCountUpTenths	= (TextView) findViewById(R.id.dailyProgressCountUpTenths);	
		bar.setVisibility(View.INVISIBLE); 
		tvCountUp.setVisibility(View.INVISIBLE);
		tvCountUpTenths.setVisibility(View.INVISIBLE);

		leftBar = (VerticalProgressBar) findViewById(R.id.verticalBarLeft);
		leftBar.setProgress(0);
		leftBar.setVisibility(View.INVISIBLE);
		
		btnArray = new Button[5];
		btnArray[0] = (Button) findViewById(R.id.bOp1);
		btnArray[1] = (Button) findViewById(R.id.bOp2);
		btnArray[2] = (Button) findViewById(R.id.bOp3);
		btnArray[3] = (Button) findViewById(R.id.bOp4);
		btnArray[4] = (Button) findViewById(R.id.bOp5);

		tvInstructions 	= (TextView) findViewById(R.id.tvInstruction);
		tvScore 		= (TextView) findViewById(R.id.Score);
		tvScoreUp 		= (TextView) findViewById(R.id.tvScoreUp);
		tvScoreDown 	= (TextView) findViewById(R.id.tvScoreDown);
		tvSymScore 		= (TextView) findViewById(R.id.tvScore);
		ivSymScoreUp 	= (ImageView) findViewById(R.id.tvUpSym);
		ivSymScoreDown 	= (ImageView) findViewById(R.id.tvDownSym);
		tvBack  		= (TextView) findViewById(R.id.tvBack);
		btnBack 		= (Button) findViewById(R.id.btnBack); 
		btnBackReview 	= (Button) findViewById(R.id.btnBackReview); 
		
		Typeface tfQQFont = Typeface.createFromAsset(getAssets(),
				"fonts/me_quran.ttf"); //amiri-quran | roboto-regular
		tvBack.setTypeface(tfQQFont);
		btnBack.setBackgroundResource(R.drawable.qqoptionbutton_correct);
		btnBackReview.setBackgroundResource(R.drawable.qqoptionbutton_correct);
		tvQ = (TextView) findViewById(R.id.textView1);
		tvQ.setTypeface(tfQQFont);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			tvQ.setMovementMethod(new ScrollingMovementMethod()); 
			tvQ.setSelected(true);	
		}

		//Inflate the view containing the SpecialQuestion Toggle Button
        View vwToggler = LayoutInflater.from(this).inflate(R.layout.special_toggle_view, new LinearLayout(getBaseContext()));
        final ToggleButton tbSpecialQ = (ToggleButton)vwToggler.findViewById(R.id.SpecialQToggler);
        tbSpecialQ.setChecked(true); // Default true, not from profile!
        tbSpecialQ.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Boolean isEnabled = tbSpecialQ.isChecked();
            	vm.setSpecialEnabled(isEnabled);
            	if(isEnabled)
            		Toast.makeText(getApplicationContext(), "ØªÙ… ØªØ´ØºÙŠÙ„ Ø§Ù„Ø£Ø³Ø¦Ù„Ø© Ø§Ù„Ø®Ø§ØµØ©", Toast.LENGTH_SHORT).show();
            	else
            		Toast.makeText(getApplicationContext(), "ØªÙ… Ø¥ÙŠÙ‚Ø§Ù� Ø§Ù„Ø§Ø³Ø¦Ù„Ø© Ø§Ù„Ø®Ø§ØµØ©", Toast.LENGTH_SHORT).show();
            }
        });

        //Attach to the action bar
        getSupportActionBar().setCustomView(vwToggler);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
		
		for(int i=0;i<5;i++){
			btnArray[i].setTypeface(tfQQFont);
			btnArray[i].setOnClickListener(this);
		}
				
		btnBack.setOnClickListener(
				new OnClickListener(){
					public void onClick(View arg0) {
			            AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);						
					}
		});
		
		btnBackReview.setOnClickListener(
			new OnClickListener(){
				public void onClick(View arg0) {
		    		Intent quranViewer = new Intent(Intent.ACTION_VIEW, Uri.parse("quran://"+quranReviewUri)); 

		    		 if (getPackageManager().queryIntentActivities(quranViewer, 0).size() > 0){
		    	    		startActivity(quranViewer); 
		    		 } else {
		    			 //Prompt user to install a "quran://" app handler
		    			 AlertDialog.Builder installQViewerDialogBuilder = new AlertDialog.Builder(QQQuestionaireActivity.this);
		    			 installQViewerDialogBuilder.setTitle(QQApp.getContext().getResources().getString(R.string.installQViewer_title))
	    						.setMessage(QQApp.getContext().getResources().getString(R.string.installQViewer_msg))
	    						.setCancelable(true)
	    						.setPositiveButton(QQApp.getContext().getResources().getString(R.string.installQViewer_install),
	    							new DialogInterface.OnClickListener() {
	    							public void onClick(DialogInterface dialog,int id) {
	    								// Redirect to install QuranAndroid
	    								Intent intent = new Intent(Intent.ACTION_VIEW); 
	    								intent.setData(Uri.parse("market://details?id=com.quran.labs.androidquran")); 
	    								startActivity(intent);
	    							} })
	    						.setNegativeButton(QQApp.getContext().getResources().getString(R.string.txt_no),
	    							new DialogInterface.OnClickListener() {
	    							public void onClick(DialogInterface dialog,int id) {
	    								dialog.cancel();
	    							} });
		    		 
						// create alert dialog and show it
						AlertDialog installQViewerDialog = installQViewerDialogBuilder.create();
						installQViewerDialog.show();
		    		 }
				}
		});		
		
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource
				(actionbar.getThemedContext(),
				 R.array.userLevels, 
				 R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionbar.setListNavigationCallbacks(list, this);
		
		//TODO: Check proper event handle {NPE}
		//actionbar.setSelectedNavigationItem(vm.getLevel());
		
		// configure the SlidingMenu
		if(QQUtils.QQDebug == 2){
			SlidingMenu menu = new SlidingMenu(this);
	
	        menu.setMode(SlidingMenu.LEFT_RIGHT);
			menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			//menu.setShadowWidthRes(R.dimen.shadow_width);
			menu.setShadowDrawable(R.drawable.shadow);
			//menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			menu.setFadeDegree(0.35f);
			menu.setSecondaryShadowDrawable(R.drawable.shadowright);
            
			menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
			menu.setMenu(R.layout.lastscreen_layout);
			menu.setSecondaryMenu(R.layout.lastscreen_layout);

			FragmentManager fmanager = getSupportFragmentManager();
            Fragment fragment = fmanager.findFragmentById(R.id.map);
            SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;

            //http://stackoverflow.com/questions/14047257/how-do-i-know-the-map-is-ready-to-get-used-when-using-the-supportmapfragment
            // Returns NULL!
            GoogleMap mMap = supportmapfragment.getMap();
            mMap.getUiSettings().setZoomControlsEnabled(false);
            //mMap.setOnMapClickListener(this);
            //mMap.setOnInfoWindowClickListener(this);
            //mMap.setOnMarkerClickListener(this);
            mMap.setMyLocationEnabled(true);
            //mMap.setOnMyLocationChangeListener(this);
            //mMap.setOnMyLocationButtonClickListener(this);
			
			//getFragmentManager().beginTransaction()
	        //.replace(R.layout.lastscreen_layout, new QQStudyListSideFragment().getTargetFragment())
	        //.commit();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * When a user presses the back button, his profile is saved
	 * and a handle is passed back.
	 */
	@Override
	public void onBackPressed() {
			vm.close();
			finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { //TODO: Clean, unused
		switch (item.getItemId()) {
		case R.id.Profile:
			Intent intentStudyList = new Intent(QQQuestionaireActivity.this,
					QQStudyListActivity.class);
			//TODO: Implement
			//intentStudyList.putExtra("ProfileHandler", myQQProfileHandler);
			startActivity(intentStudyList);
			break;
		case R.id.Settings:
			Intent intentPreferences = new Intent(QQQuestionaireActivity.this,
					QQPreferencesActivity.class);
			startActivity(intentPreferences);
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		vm.reload();
		super.onResume();
	}

	@Override
	public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	protected void onStop() {
		super.onStop();
		vm.close();
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

	private void updateOptionButtonsColor(int CorrectIdx){
		for(int i=0;i<5;i++){
			if(i==CorrectIdx)
				((Button)btnArray[i]).setBackgroundResource(R.drawable.qqoptionbutton_correct);
			else
				((Button)btnArray[i]).setBackgroundResource(R.drawable.qqoptionbutton_wrong);
		}
	}

	private void setBackCard() {
		tvBack.setText(vm.getCorrectAnswer());
		quranReviewUri = vm.getQuranUri();		
	}


	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		vm.setLevel(itemPosition);
		return true;
	}
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	            //Yes button clicked
	        	vm.startDailyQuiz();
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	        	//TODO: Implement??
	        	//myQQSession.reportDialogDisplayed();
	            break;
	        }
	    }
	};

	public void vmShowCorrectAnswer(boolean isCorrectAnswer){
		setBackCard();
		if(!isCorrectAnswer){
			Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			mVibrator.vibrate(300);
		}
		AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);
	}
	
	public void vmSetProgressVisiblity(boolean visibility){
		if(visibility)
			leftBar.setVisibility(View.VISIBLE);
		else
			leftBar.setVisibility(View.INVISIBLE);
	}
	public void vmStopCounter(){
		countUpHandler.removeCallbacks(updateTimerMethod);
		tvCountUp.setVisibility(View.INVISIBLE);
		tvCountUpTenths.setVisibility(View.INVISIBLE);
	}
	public void vmStartCounter(){
		//TODO
	}
	
	public void vmSetProgress(int p){
		leftBar.setMax(100);
		leftBar.setProgress(p);
	}
	
	public void vmSetScore(String s){
		tvScore.setText(s);
	}
	public void vmSetScoreUp(String s){
		tvScoreUp.setText(s);
	}
	public void vmSetScoreDown(String s){
		tvScoreDown.setText(s);
	}
	public void vmSetQuestion(String s){
		tvQ.setText(s);
	}
	public void vmSetInstructions(String s){
		tvInstructions.setText(s);
		if(!vm.isSpecialQuestion())
			QQUtils.tvSetBackgroundFromDrawable(tvInstructions, R.drawable.tv_instruction_background);
		else
			QQUtils.tvSetBackgroundFromDrawable(tvInstructions, R.drawable.tv_instruction_special_background);

	}
	
	public void vmSetOptions(String[] options, int correct_choice) {
		// Display Options:
		for (int j = 0; j < 5; j++) {
			btnArray[j].setText(QQUtils.fixQ(options[j]));
		}
		updateOptionButtonsColor(correct_choice); //Update background Color
	}
	
	public void vmDailyQuizAvailable() {
		builder = new AlertDialog.Builder(this);
		builder.setMessage(			getResources().getString(R.string.daily_dialogue_ask))
				.setPositiveButton(	getResources().getString(R.string.txt_ok), dialogClickListener)
				.setNegativeButton(	getResources().getString(R.string.daily_dialogue_later), dialogClickListener)
				.setTitle(			getResources().getString(R.string.daily_dialogue_title))
				.setCancelable(false)
				.show();		
	}
	
	public void vmSetScoreVisiblity(boolean visibile){
		if(!visibile){
			tvScore.setVisibility(View.INVISIBLE);
			tvScoreUp.setVisibility(View.INVISIBLE);
			tvScoreDown.setVisibility(View.INVISIBLE);
			tvSymScore.setVisibility(View.INVISIBLE);
			ivSymScoreUp.setVisibility(View.INVISIBLE);
			ivSymScoreDown.setVisibility(View.INVISIBLE);
		}else{
			tvScore.setVisibility(View.VISIBLE);
			tvScoreUp.setVisibility(View.VISIBLE);
			tvScoreDown.setVisibility(View.VISIBLE);	
			tvSymScore.setVisibility(View.VISIBLE);
			ivSymScoreUp.setVisibility(View.VISIBLE);
			ivSymScoreDown.setVisibility(View.VISIBLE);
		}
	}

	public void vmStartDailyQuiz() {
    	/*
    	AlphaAnimation  glowing_anim= new AlphaAnimation(1, 0);
    	glowing_anim.setDuration(1500); 
    	glowing_anim.setInterpolator(new LinearInterpolator()); 
    	glowing_anim.setRepeatCount(AlphaAnimation.INFINITE);
    	glowing_anim.setRepeatMode(Animation.REVERSE);
    	
    	ll_background.setBackgroundResource(R.drawable.timed_background_100);
    	ll_background.startAnimation(glowing_anim);
    	*/
    	
    	ll_background.setBackgroundResource(R.drawable.animation_background);
    	((AnimationDrawable) ll_background.getBackground()).start();
    	
    	tvCountUp.setVisibility(View.VISIBLE);
    	tvCountUpTenths.setVisibility(View.VISIBLE);
    	
    	startTime = SystemClock.uptimeMillis();
        countUpHandler.postDelayed(updateTimerMethod, 0);
 	   	leftBar.setVisibility(ProgressBar.VISIBLE);
 	   	leftBar.setMax(QQUtils.DAILYQUIZ_QPERPART_COUNT);
        leftBar.setProgress(QQUtils.DAILYQUIZ_QPERPART_COUNT);
	}
}