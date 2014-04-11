package net.quranquiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
	
	private Vector<Integer> vQStart;
	private QQProfile prof;
	private QQQuestionaireActivity activity;
	private static boolean blockRecursiveDailyQuizChecks = false;
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
	
	public QQSession(QQProfile profile, QQQuestionaireActivity QQQActivity){
		prof				= profile;
		activity			= QQQActivity;
		isDailyQuizReady 	= false;
		isDailyQuizRunning 	= false;
		dailyQuiz		 	= null;
		dailyQuizState		= -2;
		vQStart = new Vector<Integer>();
	}
	
	public boolean addIfNew(int idx){
		if(!blockRecursiveDailyQuizChecks)
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
			case -2:
				dailyQuizState = -1;
				new CheckAsyncQQDailyQuiz().execute(prof);		
				break;
			case 0:
				if(!blockRecursiveDailyQuizChecks){
					blockRecursiveDailyQuizChecks = true;
					dailyQuiz = new QQDailyQuiz();
					new SetAsyncQQDailyQuiz().execute(prof);	
				}
				break;			
			case 1:
				new GetAsyncQQDailyQuiz().execute(prof);		
				break;	
			case 2:
				break;
			default: break;
			}
		}else{
			activity.closeContextMenu();
			activity.askDailyQuiz();
			dailyQuizState = 2;
		}
		//Toast
		Toast.makeText(activity, "State ="+dailyQuizState,Toast.LENGTH_SHORT).show();
		Log.d("DailyQuiz", "State ="+dailyQuizState);
	}

	private class CheckAsyncQQDailyQuiz extends AsyncTask<QQProfile, Void, String> {

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
	        	}
	        	/**
	        	 * HACK for testing
	        	 */ dailyQuizState=0;
	        	checkDailyQuiz();
	        }

	        @Override
	        protected void onPreExecute() {        }

	        @Override
	        protected void onProgressUpdate(Void... values) {        }
	   } 
	
   private class GetAsyncQQDailyQuiz extends AsyncTask<QQProfile, Void, String> {

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
    		//TODO: Parse to fill up "dailyQuiz"
        	//dailyQuiz =
        	isDailyQuizReady = true;
        	checkDailyQuiz();
        }

        @Override
        protected void onPreExecute() {        }

        @Override
        protected void onProgressUpdate(Void... values) {        }
   } 

   private class SetAsyncQQDailyQuiz extends AsyncTask<QQProfile, Void, String> {

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
   				"http://post.quranquiz.net/setDailyQuiz.php");
   		try {
   			// Add user data
   			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(10);
   			nameValuePairs.add(new BasicNameValuePair("uid_gl", ids[0]));
   			nameValuePairs.add(new BasicNameValuePair("uid_fb", ids[1]));
   			nameValuePairs.add(new BasicNameValuePair("uid_tw", ids[2]));
   			nameValuePairs.add(new BasicNameValuePair("uid_ap", ids[3]));
   			nameValuePairs.add(new BasicNameValuePair("uid_ot", ids[4]));
   			//TODO: Add the object!
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
    	   isDailyQuizReady = true;
    	   checkDailyQuiz();
    	   //TODO: Set the object
       }

       @Override
       protected void onPreExecute() {        }

       @Override
       protected void onProgressUpdate(Void... values) {        }
  }

public void reportDialogDisplayed() {
	dailyQuizState = 2;
} 

}
