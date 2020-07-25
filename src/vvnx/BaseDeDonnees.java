package vvnx.locgatt;

import android.content.Context;
import android.util.Log;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import android.content.ContentValues;


import java.io.File;
import android.os.Environment;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


//sqlite3 /data/data/vvnx.locgatt/databases/loc.db "select datetime(FIXTIME, 'unixepoch', 'localtime'), LAT, LONG, ACC from loc;"


public class BaseDeDonnees extends SQLiteOpenHelper {
	
	private static final String TAG = "LocGatt";

    private static final String DATABASE_NAME = "loc.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_BDD_MAIN = "CREATE TABLE loc (ID INTEGER PRIMARY KEY AUTOINCREMENT, FIXTIME INTEGER NOT NULL, LAT REAL NOT NULL, LONG REAL NOT NULL, ACC REAL NOT NULL)";


    private SQLiteDatabase bdd;


    public BaseDeDonnees(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    public void logFix(long fixtime, double lat, double lng, float acc){
		bdd = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("FIXTIME", fixtime/1000);
		values.put("LAT", lat);
		values.put("LONG", lng);
		values.put("ACC", acc);
		bdd.insert("loc", null, values);
	}
	

	
	

	
	public void deleteAll(){
			bdd = this.getWritableDatabase();
			bdd.delete("loc", null, null);

		}
	
	
	
	

	
	
}
