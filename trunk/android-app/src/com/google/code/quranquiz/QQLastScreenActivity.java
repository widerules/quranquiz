package com.google.code.quranquiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

public class QQLastScreenActivity extends SherlockActivity {

	private TextView tv;
	private QQProfileHandler ProfileHandler;
    private String conclusionMessage;
    private String ExtraInfo="\n\n" +
    				"يمكنك مشاركة اصدقاءك عن طريق احد الطرق بالقائمة العلوية،"+
    				" فالمشاركة تشجع التنافس في الطاعة كما انها تساعد على نشر البرنامج";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
       //Get the passed QQProfile
        ProfileHandler = (QQProfileHandler) getIntent().getSerializableExtra("ProfileHandler");
        conclusionMessage = getMessageFromProfile(ProfileHandler.CurrentProfile);
        tv = new TextView(this);
        tv.setText(conclusionMessage + ExtraInfo);
        setContentView(tv); 
        postAnonymousData(ProfileHandler.CurrentProfile);
    }
    
    private void postAnonymousData(QQProfile currentProfile) {
    	String uid, score, juz2, qcount, avglevel, md5;
    	
    	uid = currentProfile.getuid();
    	score = String.valueOf(currentProfile.getScore());
    	juz2 = String.valueOf((double)(currentProfile.getTotalStudyLength()*300/QQUtils.QuranWords)/10);
    	qcount = String.valueOf(currentProfile.getTotalQuesCount());
    	avglevel = String.valueOf(currentProfile.getAvgLevel());
    	md5 = QQUtils.md5("QQ-"+ uid+"-"+ score+"-"+ juz2+"-"+ qcount+"-"+ avglevel);
    	
    	// Create a new HttpClient and Post Header
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost("http://quranquiz.net/updateUserData.php");

    	try {
    	// Add user data
    	List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(6);
    	nameValuePairs.add(new BasicNameValuePair("u", uid));
    	nameValuePairs.add(new BasicNameValuePair("s", score));
    	nameValuePairs.add(new BasicNameValuePair("j", juz2));
    	nameValuePairs.add(new BasicNameValuePair("q", qcount));
    	nameValuePairs.add(new BasicNameValuePair("l", avglevel));
    	nameValuePairs.add(new BasicNameValuePair("m", md5));
    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    	// Execute HTTP Post Request
    	HttpResponse res = httpclient.execute(httppost);
    	tv.append(EntityUtils.toString(res.getEntity()));
    	
    	} catch (ClientProtocolException e) {
    	// TODO Auto-generated catch block
    	} catch (IOException e) {
    	// TODO Auto-generated catch block
    	}
    	
	}

	private String getMessageFromProfile(QQProfile currentProfile) {
    	String msg;
    	msg = "لقد حصلت على " + currentProfile.getScore() + "  نقطة في #اختبار_القران";
    	return msg;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your menu.
        getSupportMenuInflater().inflate(R.menu.share_action_provider, menu);

        // Set file with share history to the provider and set the share intent.
        MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        // Note that you can set/change the intent any time,
        // say when the user has selected an image.
        actionProvider.setShareIntent(createShareIntent());

        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, conclusionMessage + " http://code.google.com/p/quranquiz");
        return shareIntent;
    }
}