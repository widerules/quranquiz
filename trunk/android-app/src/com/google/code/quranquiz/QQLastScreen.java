package com.google.code.quranquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

public class QQLastScreen extends SherlockActivity {

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
        TextView text = new TextView(this);
        text.setText(conclusionMessage + ExtraInfo);
        setContentView(text); 
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