/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
/**
 * 
 */
package net.quranquiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class QQInstructionsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.instructions_layout);
		ImageView instructions = (ImageView) findViewById(R.id.ivInstructions);
		instructions.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
		    }
		});
	}
}
