package uk.co.matbooth.beardedtheory.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Takes care of initial creation of and upgrading the schedule database.
 */
public class ScheduleDatabaseHelper extends SQLiteOpenHelper {

    // Database details
    private static final String DATABASE_NAME = "schedule.sqlite";
    private static final int DATABASE_VERSION = 1;

    public ScheduleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + Schedule.Events.TABLE + " (" +
                Schedule.Events._ID + " INTEGER PRIMARY KEY, " +
                Schedule.Events.DAY + " INTEGER, " +
                Schedule.Events.STAGE + " TEXT NOT NULL COLLATE NOCASE, " +
                Schedule.Events.PERFORMER + " TEXT NOT NULL COLLATE NOCASE, " +
                Schedule.Events.START_TIME + " INTEGER, " +
                Schedule.Events.END_TIME + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Do nothing yet
    }
}
