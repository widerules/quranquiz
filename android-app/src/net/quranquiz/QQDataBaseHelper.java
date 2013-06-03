/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class QQDataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	public static String DB_PATH = "/data/data/net.quranquiz/databases/";
	public static String DB_NAME = "qq.sqlite";
	
    // 1 -> 2 add q.txtsym column
    private static final int DB_VERSION = 2;	
	private SQLiteDatabase myDataBase;

	private final Context myContext;

	public static int fileLength = 0;

	public QQDataBaseHelper(Context context) {

		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
	}

	/**
	 * Check if the database already exist to avoid re-downloading the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	public boolean checkDataBase() {
		
		SQLiteDatabase checkDB = null;
		String myDBFile = DB_PATH + DB_NAME;
		int v = -1;
		
		try {
			checkDB = SQLiteDatabase.openDatabase(myDBFile, null,
					SQLiteDatabase.OPEN_READONLY);
			v = checkDB.getVersion();
		} catch (SQLiteException e) {
		}

		if (checkDB != null) {
			checkDB.close();
		}
		if(v>-1 && v< DB_VERSION){
			File f = new File(DB_PATH+DB_NAME);
			if(f.exists())
				f.delete();
			f = new File(DB_PATH+DB_NAME+"-journal");
			if(f.exists())
				f.delete();
			return false;
		}else{
			return checkDB != null ? true : false;
		}
	}

	public void closeDatabase() {
		if (myDataBase!=null)
			if( myDataBase.isOpen())
				myDataBase.close();
        super.close();
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * @throws Exception 
	 * */
	public void createDataBase() throws Exception {

		try {
			// By calling this method an empty database will be created into the
			// default system path
			// of the application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			Toast.makeText(myContext, "جاري فتح قاعدة البيانات للمرة الاولى", Toast.LENGTH_LONG).show();
			prepareDataBase();
		} catch (Exception e) {
			Toast.makeText(
					this.myContext,
					"مساحة التخزين غير كافية لفك قاعدة البيانات. ازل بعض الملفات وحاول لاحقا",
					Toast.LENGTH_LONG).show();
			throw e;
		}
	}

	private void prepareDataBase() throws Exception {
	     new File(DB_PATH).mkdirs();
	     File db = new File(DB_PATH, DB_NAME);
	     if(!db.exists())
	    	 db.createNewFile();
	     
		 InputStream is = myContext.getResources().openRawResource(R.raw.qq_noidx_sqlite);
	      //Open the empty db as the output stream
	     OutputStream myOutput = new FileOutputStream(db.getAbsolutePath());
	     ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
	      try {
	          while ((zis.getNextEntry()) != null) {
	              ByteArrayOutputStream baos = new ByteArrayOutputStream();
	              byte[] buffer = new byte[1024];
	              int count;
	              while ((count = zis.read(buffer)) != -1) {
	                  baos.write(buffer, 0, count);
	              }
	              baos.writeTo(myOutput);	 
	          }
	      } finally {
	          zis.close();
	          myOutput.flush();
	          myOutput.close();
	          is.close();
	      }
	      
		 SQLiteDatabase myDataBase =
		 SQLiteDatabase.openDatabase(DB_PATH+DB_NAME, null,SQLiteDatabase.OPEN_READWRITE);
		 if(myDataBase != null){
			 myDataBase.execSQL("CREATE INDEX Q_TXT_INDEX ON q (txt ASC);");
			 myDataBase.setVersion(DB_VERSION);
			 myDataBase.close(); // Close the READWRITE session.
		 }
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	public List<Integer> sim1idx(int idx) {

		List<Integer> ids = new ArrayList<Integer>();
		if (myDataBase != null) {
			Cursor cur = myDataBase.rawQuery(
					"select _id from q where txt=(select txt from q where _id="
							+ idx + ") and _id !=" + idx, null);
			if (cur.moveToFirst()) {
				do {
					ids.add(cur.getInt(0));
				} while (cur.moveToNext());
				cur.close();
			}
		}
		return ids;
	}

	public int sim2cnt(int idx) {
		int s = 0;

		if (myDataBase != null) {
			Cursor cur = myDataBase.rawQuery("select sim2 from q where _id="
					+ idx, null);
			if (cur.moveToFirst()) {
				s = cur.getInt(0);
				cur.close();
			}
		}
		return s;
	}

	public List<Integer> sim2idx(int idx) {

		List<Integer> ids = new ArrayList<Integer>();
		ids.clear();

		if (myDataBase != null) {
			Cursor cur = myDataBase
					.rawQuery(
							"select q1._id from q q1 join q q2 where q1._id+1=q2._id "
									+ "and q1._id in (select _id from q where txt=(select txt from q where _id="
									+ idx
									+ ") and _id !="
									+ idx
									+ ") "
									+ "and q2._id in (select _id from q where txt=(select txt from q where _id="
									+ (idx + 1) + ") and _id !=" + (idx + 1)
									+ ")", null);
			if (cur.moveToFirst()) {
				do {
					ids.add(cur.getInt(0));
				} while (cur.moveToNext());
				cur.close();
			}
		}
		return ids;
	}

	public int sim3cnt(int idx) {
		int s = 0;

		if (myDataBase != null) {
			Cursor cur = myDataBase.rawQuery("select sim3 from q where _id="
					+ idx, null);
			if (cur.moveToFirst()) {
				s = cur.getInt(0);
				cur.close();
			}
		}
		return s;
	}

	public List<Integer> sim3idx(int idx) {

		List<Integer> ids = new ArrayList<Integer>();
		ids.clear();

		if (myDataBase != null) {
			Cursor cur = myDataBase
					.rawQuery(
							"select q1._id from q q1 join q q2 join q q3 where q1._id+1=q2._id and q1._id+2=q3._id "
									+ "and q1._id in (select _id from q where txt=(select txt from q where _id="
									+ (idx)
									+ ") and _id !="
									+ (idx)
									+ ")"
									+ "and q2._id in (select _id from q where txt=(select txt from q where _id="
									+ (idx + 1)
									+ ") and _id !="
									+ (idx + 1)
									+ ")"
									+ "and q3._id in (select _id from q where txt=(select txt from q where _id="
									+ (idx + 2)
									+ ") and _id !="
									+ (idx + 2)
									+ ")", null);
			if (cur.moveToFirst()) {
				do {
					ids.add(cur.getInt(0));
				} while (cur.moveToNext());
				cur.close();
			}
		}
		return ids;
	}

	/* QQ Adaptors set */
	public String txt(int idx) {
		String s = new String("");

		if (myDataBase != null) {
			Cursor cur = myDataBase.rawQuery("select txtsym from q where _id="
					+ idx, null);
			if (cur.moveToFirst()) {
				s = cur.getString(0);
				cur.close();
			}
		}
		return s;
	}

	public String txt(int idx, int len) {
		String s = new String("");

		if (myDataBase != null) {
			Cursor cur = myDataBase.rawQuery("select txtsym from q where _id>"
					+ (idx - 1) + " and _id<" + (idx + len), null);
			if (cur.moveToFirst()) {
				do {
					s = s + "   " + 
							cur.getString(0);
				} while (cur.moveToNext());
				cur.close();
			}
		}
		return s;
	}

	public List<Integer> uniqueSim1Not2Plus1(int idx) {
		List<Integer> ids = new ArrayList<Integer>();
		ids.clear();

		if (myDataBase != null) {
			Cursor cur = myDataBase
					.rawQuery(
							"select min(_id) from q where _id in (select _id+1 from q where "
									+ "_id in (select _id from q where txt=(select txt from q where _id="
									+ idx
									+ ") and _id !="
									+ idx
									+ ")"
									+ "and _id not in (select q1._id from q q1 join q q2 where q1._id+1=q2._id "
									+ "and q1._id in (select _id from q where txt=(select txt from q where _id="
									+ idx
									+ ") and _id !="
									+ idx
									+ ") "
									+ "and q2._id in (select _id from q where txt=(select txt from q where _id="
									+ (idx + 1) + ") and _id !=" + (idx + 1)
									+ ")) " + ") group by txt", null);
			if (cur.moveToFirst()) {
				do {
					ids.add(cur.getInt(0));
				} while (cur.moveToNext());
				cur.close();
			}
		}
		return ids;
	}

	public List<Integer> randomUnique4NotMatching(int idx) {

		List<Integer> ids = new ArrayList<Integer>();
		if (myDataBase != null) {
			//TODO: Slow table creation: Any optimization?
			myDataBase.execSQL("CREATE TEMP TABLE IF NOT EXISTS xy as select _id,txt from q order by random() limit 200");
			Cursor cur = myDataBase.rawQuery(
					"select q1._id from xy q1 inner join xy q2 on q2.txt = q1.txt "+
					"group by q1._id,q1.txt having q1._id = min(q2._id) "+
					"and q1.txt !=(select txt from q where _id="+idx+") order by random() limit 4",
					null);
			if (cur.moveToFirst()) {
				do {
					ids.add(cur.getInt(0));
				} while (cur.moveToNext());
				cur.close();
			}
		}
		return ids;
	}

}