package com.google.code.quranquiz;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class QQDataBaseHelper extends SQLiteOpenHelper{
 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.google.code.quranquiz/databases/";
    private static String DB_NAME = "qq.sqlite";
    private static String DB_DOWNLOAD = "http://quranquiz.googlecode.com/files/qq-v1.sqlite";
    
    private SQLiteDatabase myDataBase; 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public QQDataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    		Toast.makeText(this.myContext, "Database found!", Toast.LENGTH_LONG).show();
    	}else{
 
    		//By calling this method an empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	downloadDataBase(myContext);
			Toast.makeText(this.myContext, "Database Copied!", Toast.LENGTH_LONG).show();
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-downloading the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Downloads your database.
     * */
    private void downloadDataBase(Context myContext) {
    	
    	ProgressDialog mProgressDialog = new ProgressDialog(myContext);
    	mProgressDialog.setMessage("جاري تنزيل الملف الابتدائي");
    	mProgressDialog.setIndeterminate(false);
    	mProgressDialog.setMax(100);
    	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	QQDownloader downloadFile = new QQDownloader();
    	//mProgressDialog.show();
    	downloadFile.execute(DB_DOWNLOAD);
		
    }
 
    public void openDataBase() throws SQLException{
    	 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	
    	// Do bla ..
    	
    	myDataBase.close();
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
 
}