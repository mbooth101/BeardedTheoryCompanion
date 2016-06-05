package uk.co.matbooth.beardedtheory.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A content provider that provides access to the schedule database.
 */
public class ScheduleProvider extends ContentProvider {

    // Helper that allows us to interact with the database
    private ScheduleDatabaseHelper helper;

    // URI matcher and IDs needed for matching rules
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int EVENTS = 10;
    private static final int DAYS = 11;
    private static final int EVENT = 20;
    private static final int DAY = 21;

    // Configure the URI matching rules
    static {
        URI_MATCHER.addURI(Schedule.CONTENT_URI.getAuthority(), Schedule.Events.TABLE, EVENTS);
        URI_MATCHER.addURI(Schedule.CONTENT_URI.getAuthority(), Schedule.Days.TABLE, DAYS);
        URI_MATCHER.addURI(Schedule.CONTENT_URI.getAuthority(), Schedule.Events.TABLE + "/#", EVENT);
        URI_MATCHER.addURI(Schedule.CONTENT_URI.getAuthority(), Schedule.Days.TABLE + "/#", DAY);
    }

    @Override
    public boolean onCreate() {
        helper = new ScheduleDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String table;
        switch (URI_MATCHER.match(uri)) {
            case DAYS:
                table = Schedule.Days.TABLE;
                // Chronological order by default
                if (sortOrder == null) {
                    sortOrder = Schedule.Days.DAY + " ASC";
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        builder.setTables(table);
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues value) {
        throw new UnsupportedOperationException("Inserts not supported for URI: " + uri);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        int rows = 0;
        long lastId;
        try {
            db.beginTransaction();
            String table;
            switch (URI_MATCHER.match(uri)) {
                case EVENTS:
                    table = Schedule.Events.TABLE;
                    break;
                case DAYS:
                    table = Schedule.Days.TABLE;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            for (ContentValues value : values) {
                lastId = db.insert(table, null, value);
                if (lastId >= 0) {
                    rows++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (rows > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rows;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        int rows = 0;
        try {
            db.beginTransaction();
            String table;
            switch (URI_MATCHER.match(uri)) {
                case EVENTS:
                    table = Schedule.Events.TABLE;
                    break;
                case DAYS:
                    table = Schedule.Days.TABLE;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            rows = db.delete(table, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (rows > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Updates not supported for URI: " + uri);
    }
}
