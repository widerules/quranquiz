/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mirror.android.os.AsyncTask;
import net.quranquiz.storage.QQProfile;
import net.quranquiz.util.QQUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * This session holds a vector of current questions to 
 * disable redundant questions within a session.
 * It also checks for daily quiz games.
 * 
 * @author tarek.eldeeb@gmail.com
 */
public class QQSession {
	public boolean isDailyQuizReady;
	public boolean isDailyQuizRunning;
	public QQDailyQuiz dailyQuiz;
	public int createdParts;
	
	private CheckAsyncQQDailyQuiz casqqdq;
	private SetAsyncQQDailyQuiz sasqqdq;
	private GetAsyncQQDailyQuiz gasqqdq;
	private Vector<Integer> vQStart;
	private QQProfile prof;
	private Model _model;
	private static boolean blockRecursiveDailyQuizChecks = false;
	private DailyQuizQuestionnaire dailyQuizQuestionnaire = null;
	
	/**
	 * Daily Quiz state directs an internal state machine
	 * to communicate properly with the server according to
	 * the following states:	\n
	 * -2	: Nothing done yet 						>> Start Checking server					\n
	 * -1 	: Currently Checking					>> wait. 									\n
	 *  0 	: Has No/Invalid object of DailyQuiz 	>> Create + Post Object	[isDailyQuizReady ?]\n
	 *  1	: Has a valid object of DailyQuiz 		>> Get Object	[isDailyQuizReady ?]		\n
	 *  2	: Dialog displayed to user [isDailyQuizRunning ?]
	 */
	private int dailyQuizState;
	private int retries = 0;
	
	public QQSession(QQProfile profile, Model model){
		prof				= profile;
		_model				= model;
		isDailyQuizReady 	= false;
		isDailyQuizRunning 	= false;
		dailyQuiz		 	= null;
		dailyQuizState		= -2;
		vQStart = new Vector<Integer>();
	}
	
	public DailyQuizQuestionnaire getDailyQuizQuestionnaire(){
		if(dailyQuizQuestionnaire == null)
			this.dailyQuizQuestionnaire = new DailyQuizQuestionnaire(this.dailyQuiz, prof);
		return dailyQuizQuestionnaire;
	}
	
	public boolean addIfNew(int idx){
		if(!blockRecursiveDailyQuizChecks && (retries++)<3)
			checkDailyQuiz();
		
		if(vQStart.contains(Integer.valueOf(idx))){
			return false;
		}else if(vQStart.isEmpty()){
			vQStart.add(Integer.valueOf(idx));
			return true;
		}else{
			/**
			 * If not found, and not the first question,
			 * then test for too close one and reject it
			 */
			for(int i=0;i<vQStart.size();i++)
				if(Math.abs(vQStart.elementAt(i)-idx)<10)
					return false;
			
			vQStart.add(Integer.valueOf(idx));				
			return true;
		}
	}

	private void checkDailyQuiz() {
		if(!isDailyQuizReady){
			switch(dailyQuizState){
			case -2: //No info yet
				dailyQuizState = -1;
				casqqdq = new CheckAsyncQQDailyQuiz();
				casqqdq.execute(prof);		
				break;
			case 0: //Server has out-dated object, create 1 for him
				if(!blockRecursiveDailyQuizChecks){
					blockRecursiveDailyQuizChecks = true;
					sasqqdq = new SetAsyncQQDailyQuiz();
					//sasqqdq.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
					sasqqdq.execute();
				}
				break;			
			case 1: //Server has a valid object, get it
				gasqqdq = new GetAsyncQQDailyQuiz();
				gasqqdq.execute(prof);
				break;	
			case 2:
				break;
			default: break;
			}
		}else{
			
			//TODO: Implement
			//activity.closeContextMenu();
			
			if(dailyQuizState!=2) //Ask only once! FIXME: Better implementation?
				//_model.askDailyQuiz();
		   		_model.getEventBus().dispatchEvent(new QQModelEvent(QQEventType.UI_DAILY_QUIZ_AVAILABLE),"");

			dailyQuizState = 2;
		}
		Log.d("DailyQuiz", "State ="+dailyQuizState);
	}

	class CheckAsyncQQDailyQuiz extends AsyncTask<QQProfile, Void, String> {

	        @Override
	        protected String doInBackground(QQProfile... params) {
	    		String uid, md5;
	    		String[] ids;
	    		String response="";
	    		QQProfile currentProfile = params[0];
	    		/******************* Entail User Data ***************/
	    		uid = currentProfile.getuid();
	    		ids = uid.split("\\+");
	    		md5 = QQUtils.md5(QQUtils.QQ_MD5_KEY + ids[0] + "-" + ids[1] + "-" + ids[2] + "-"
	    							+ ids[3] + "-" + ids[4]);

	    		// Create a new HttpClient and Post Header
	    		HttpClient httpclient = new DefaultHttpClient();
	    		HttpPost httppost = new HttpPost(
	    				"http://post.quranquiz.net/checkDailyQuiz.php");
	    		try {
	    			// Add user data
	    			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(10);
	    			nameValuePairs.add(new BasicNameValuePair("uid_gl", ids[0]));
	    			nameValuePairs.add(new BasicNameValuePair("uid_fb", ids[1]));
	    			nameValuePairs.add(new BasicNameValuePair("uid_tw", ids[2]));
	    			nameValuePairs.add(new BasicNameValuePair("uid_ap", ids[3]));
	    			nameValuePairs.add(new BasicNameValuePair("uid_ot", ids[4]));
	    			nameValuePairs.add(new BasicNameValuePair("m", md5));
	    			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    			// Execute HTTP Post Request
	    			HttpResponse res = httpclient.execute(httppost);
	    			response += EntityUtils.toString(res.getEntity()); 
	    		} catch (ClientProtocolException e) {
	    		} catch (IOException e) {
	    		}
	       		Log.d("DailyQuiz", " Check DQ Response :: "+ response);
	    		return response;
	        }      

	        @Override
	        protected void onPostExecute(String result) {
	        	int checkResponse;
	        	
	        	try{
	        		checkResponse = Integer.valueOf(result);
	        	}
	        	catch (Exception e){ 
	        		checkResponse = -1;
	        	}
	        	
	        	if(checkResponse == 0 || checkResponse ==1){
	        		dailyQuizState = checkResponse;
	        	}else{
	        		//Weird response!
	        		dailyQuizState = -2;
	        		
	        		/*Stop communicating with server after 3 retries*/
	        		if((retries++)>3)dailyQuizState = 0;
	        	}
	        	checkDailyQuiz();
	        }

	        @Override
	        protected void onPreExecute() {        }

	        @Override
	        protected void onProgressUpdate(Void... values) {        }
	   } 
	
   class GetAsyncQQDailyQuiz extends AsyncTask<QQProfile, Void, String> {

        @Override
        protected String doInBackground(QQProfile... params) {
    		String uid, md5;
    		String[] ids;
    		String response="";
    		QQProfile currentProfile = params[0];
    		/******************* Entail User Data ***************/
    		uid = currentProfile.getuid();
    		ids = uid.split("\\+");
    		md5 = QQUtils.md5(QQUtils.QQ_MD5_KEY + ids[0] + "-" + ids[1] + "-" + ids[2] + "-"
    							+ ids[3] + "-" + ids[4]);

    		// Create a new HttpClient and Post Header
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost(
    				"http://post.quranquiz.net/getDailyQuiz.php");
    		try {
    			// Add user data
    			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(10);
    			nameValuePairs.add(new BasicNameValuePair("uid_gl", ids[0]));
    			nameValuePairs.add(new BasicNameValuePair("uid_fb", ids[1]));
    			nameValuePairs.add(new BasicNameValuePair("uid_tw", ids[2]));
    			nameValuePairs.add(new BasicNameValuePair("uid_ap", ids[3]));
    			nameValuePairs.add(new BasicNameValuePair("uid_ot", ids[4]));
    			nameValuePairs.add(new BasicNameValuePair("m", md5));
    			
    			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    			// Execute HTTP Post Request
    			HttpResponse res = httpclient.execute(httppost);
    			response += EntityUtils.toString(res.getEntity()); 
    		} catch (ClientProtocolException e) {
    		} catch (IOException e) {
    		}
    		return response;
        }      

        @Override
        protected void onPostExecute(String result) {
        	try {
				dailyQuiz = (QQDailyQuiz) QQUtils.fromString64(result);
				Log.d("DailyQuiz", "Got quiz with Quesitons: "+dailyQuiz.questionsCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	isDailyQuizReady = true;
        	checkDailyQuiz();
        }

        @Override
        protected void onPreExecute() {        }

        @Override
        protected void onProgressUpdate(Void... values) {        }
   } 

   class SetAsyncQQDailyQuiz extends AsyncTask<Void, Integer, String> {

       @Override
       protected String doInBackground(Void... params) {
   		String uid, md5;
   		String[] ids;
   		String response="";
   		QQProfile currentProfile = prof;
   		dailyQuiz = new QQDailyQuiz(this); //Very Slow!
   		String sdq = null;
   		/******************* Entail User Data ***************/
   		uid = currentProfile.getuid();
   		ids = uid.split("\\+");
   		md5 = QQUtils.md5(QQUtils.QQ_MD5_KEY + ids[0] + "-" + ids[1] + "-" + ids[2] + "-"
   							+ ids[3] + "-" + ids[4]);

   		// Create a new HttpClient and Post Header
   		HttpClient httpclient = new DefaultHttpClient();
   		HttpPost httppost = new HttpPost(
   				"http://post.quranquiz.net/setDailyQuiz.php");
   		try {
   			// Add user data
   			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(10);
   			nameValuePairs.add(new BasicNameValuePair("uid_gl", ids[0]));
   			nameValuePairs.add(new BasicNameValuePair("uid_fb", ids[1]));
   			nameValuePairs.add(new BasicNameValuePair("uid_tw", ids[2]));
   			nameValuePairs.add(new BasicNameValuePair("uid_ap", ids[3]));
   			nameValuePairs.add(new BasicNameValuePair("uid_ot", ids[4]));
   			nameValuePairs.add(new BasicNameValuePair("m", md5));
   			
			sdq = new String("");
   			sdq = QQUtils.toString64(dailyQuiz);
   	   		Log.d("DailyQuiz", "From DQ with number: "+dailyQuiz.questionsCount);
   	   		Log.d("DailyQuiz", "returning String64 of length: "+sdq.length());

   			nameValuePairs.add(new BasicNameValuePair("obj", sdq));
   			
   			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

   			// Execute HTTP Post Request
   			HttpResponse res = httpclient.execute(httppost);
   			response += EntityUtils.toString(res.getEntity()); 
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		
   		//TODO: Validate response
   		Log.d("DailyQuiz", response);

   		return sdq;
       }      

       @Override
       protected void onPostExecute(String sdq) {
			try {
		    	dailyQuiz = (QQDailyQuiz)QQUtils.fromString64(sdq);
			} catch (Exception e) {
				e.printStackTrace();
			}
			isDailyQuizReady = true;
			checkDailyQuiz();
			Log.d("DailyQuiz", "Set quiz with Quesitons: "+dailyQuiz.questionsCount);
       }

       @Override
       protected void onPreExecute() {
   		_model.getEventBus().dispatchEvent(new QQModelEvent(QQEventType.UI_STATUS_DAILY_QUIZ_BUILDING), "");

    	   /*Toast.makeText(activity,QQApp.getContext().getResources().getString(R.string.daily_dialogue_building),Toast.LENGTH_LONG).show();
    	   _model.setVisibility(ProgressBar.VISIBLE);
    	   _model.leftBar.setMax(QQUtils.DAILYQUIZ_PARTS_COUNT);*/
       }

       @Override
       protected void onProgressUpdate(Integer... values) {
    	   _model.setProgress(values[0]);
       }
       
       public void triggerUpdateProgress(int i){
    	   publishProgress(i);
       }
  }

	public void reportDialogDisplayed() {
		dailyQuizState = 2;
	}
	
	/**
	 * Close all background ASyncTasks
	 */
	public void close() {
		if(casqqdq != null)
			casqqdq.cancel(true);
		if(sasqqdq != null)
			sasqqdq.cancel(true);
		if(gasqqdq != null)
			gasqqdq.cancel(true);	
	}

}
