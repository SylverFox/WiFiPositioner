package nl.utwente.wifipositioner;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

/**
 * Created by Joris on 22/05/2014.
 */
public class SQLiteDatabaseHandle extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CaptureDatabase";
    private static final int DATABASE_VERSION = 5;

    // tables
    private static final String TABLE_CAPTUREDATA = "capturedata";

    // columns
    private static final String KEY_ID = "id";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_GPS = "gps";
    private static final String KEY_MAC = "mac";
    private static final String KEY_RSSI = "rssi";

    public SQLiteDatabaseHandle(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CAPTUREDATA + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                KEY_GPS + " TEXT, " +
                KEY_MAC + " TEXT, " +
                KEY_RSSI + " TEXT" +
                ")";
        db.execSQL(query);
        Log.d("SQLiteDatabase","Database created: "+db.getPath());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DELETE FROM " + TABLE_CAPTUREDATA + " WHERE 1";
        db.execSQL(query);
        Log.d("SQLiteDatabase","Database upgraded: "+db.getPath());
    }

    public void addRecord(Location location,String mac, String rssi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GPS,location.getLatitude()+","+location.getLongitude());
        values.put(KEY_MAC,mac);
        values.put(KEY_RSSI,rssi);

        db.insert(TABLE_CAPTUREDATA,null,values);
        db.close();
    }

    public void resetDB() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_CAPTUREDATA + " WHERE 1";
        db.execSQL(query);
        Log.d("SQLiteDatabase","Database emptied");
    }
}
