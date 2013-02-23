package com.google.code.quranquiz;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class QQDownloader extends AsyncTask<String, Integer, String> {

	// The Android's default system path of QQ database.
	private static String DB_PATH = "/data/data/com.google.code.quranquiz/databases/";
	private static String DB_NAME = "qq.sqlite";
	/**
	 * @uml.property name="outFileName"
	 */
	String outFileName = DB_PATH + DB_NAME;
	private static int fileLength = 0;
	/**
	 * @uml.property name="mProgressDialog"
	 * @uml.associationEnd
	 */
	private ProgressDialog mProgressDialog;

	@Override
	protected String doInBackground(String... url) {
		int count;
		try {
			URL myUrl = new URL(url[0]);
			URLConnection conexion = myUrl.openConnection();
			conexion.connect();
			// this will show a tipical 0-100% progress bar
			fileLength = conexion.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(myUrl.openStream());
			OutputStream output = new FileOutputStream(outFileName);

			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				publishProgress((int) (total * 100 / fileLength));
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
			fileLength = -1;

			// TODO Create the index (performance 10X)
			// SQLiteDatabase myDataBase =
			// SQLiteDatabase.openDatabase(outFileName, null,
			// SQLiteDatabase.OPEN_READWRITE);
			// if(myDataBase != null){
			// myDataBase.execSQL("CREATE INDEX Q_TXT_INDEX ON q (txt ASC);");
			// myDataBase.close(); // Close the READWRITE session. Other
			// sessions are read-only
			// }

		} catch (Exception e) {
		}
		return null;
	}

	protected void onPreExecute(Context... contexts) {
		mProgressDialog = new ProgressDialog(contexts[0]);
		mProgressDialog.setMessage("جاري تنزيل الملف الابتدائي");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}

	public void onProgressUpdate(String... args) {
		// here you will have to update the progressbar
		// with something like
		mProgressDialog.setProgress(Integer.parseInt(args[0]));
	}

}