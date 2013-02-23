package com.google.code.quranquiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class QQDataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.google.code.quranquiz/databases/";
	private static String DB_NAME = "qq.sqlite";
	private static String DB_DOWNLOAD = "http://quranquiz.googlecode.com/files/qq-v1.sqlite";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	public static int fileLength = 0;

	public QQDataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Check if the database already exist to avoid re-downloading the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	public void closeDatabase() {
		myDataBase.close();

	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
			Toast.makeText(this.myContext, "Database found!", Toast.LENGTH_LONG)
					.show();
		} else {

			// By calling this method an empty database will be created into the
			// default system path
			// of the application so we are gonna be able to overwrite that
			// database with our database.
			try {
				this.getReadableDatabase();
				downloadDataBase(myContext);
			} catch (Error e) {
				Toast.makeText(
						this.myContext,
						"Failed to create/download database. Please check your internet connection and try again!",
						Toast.LENGTH_LONG).show();
			}
			Toast.makeText(this.myContext, "Database Downloaded!",
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Downloads your database.
	 * */
	private void downloadDataBase(Context myContext) {

		QQDownloader downloadFile = new QQDownloader();
		// mProgressDialog.show();
		downloadFile.execute(DB_DOWNLOAD);
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
			Cursor cur = myDataBase.rawQuery("select txt from q where _id="
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
			Cursor cur = myDataBase.rawQuery("select txt from q where _id>"
					+ (idx - 1) + " and _id<" + (idx + len), null);
			if (cur.moveToFirst()) {
				do {
					s = s + "   " + cur.getString(0);
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

}