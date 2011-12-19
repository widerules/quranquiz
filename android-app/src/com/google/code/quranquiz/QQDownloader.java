package com.google.code.quranquiz;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;


public class QQDownloader extends AsyncTask<String, Integer, String>{
    
	//The Android's default system path of QQ database.
    private static String DB_PATH = "/data/data/com.google.code.quranquiz/databases/";
    private static String DB_NAME = "qq.sqlite";
	String outFileName = DB_PATH + DB_NAME;
	private static int fileLength = 0;
	
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
                publishProgress((int)(total*100/fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            fileLength=-1;
            
        } catch (Exception e) { }
        return null;
    }
}