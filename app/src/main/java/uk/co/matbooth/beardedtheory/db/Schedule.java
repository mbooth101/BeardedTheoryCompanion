package uk.co.matbooth.beardedtheory.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import uk.co.matbooth.beardedtheory.model.Event;

/**
 * Interface into the schedule provider.
 */
public final class Schedule {

    // Namespaced authority for the schedule data provider
    private static final String AUTHORITY = "uk.co.matbooth.beardedtheory.schedule.provider";

    /**
     * The content:// style URL for the schedule provider
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Interface into the schedule provider's events table.
     */
    public static final class Events implements BaseColumns {

        /**
         * Name of the events table.
         */
        public static final String TABLE = "events";

        /**
         * The content:// style URL for the events table.
         */
        public static final Uri CONTENT_URI = Schedule.CONTENT_URI.buildUpon().appendPath(TABLE).build();

        /**
         * The content:// style URL that yields distinct results for the events table.
         */
        public static final Uri DISTINCT_URI = Schedule.Events.CONTENT_URI.buildUpon().appendPath("DISTINCT").build();

        /**
         * Day column, indicates the day that the event is scheduled to take place in millis since the epoch.
         * <p>Type: INTEGER</p>
         */
        public static final String DAY = "day";

        /**
         * Stage column, contains the name of the stage at which the event will take place.
         * <p>Type: TEXT</p>
         */
        public static final String STAGE = "stage";

        /**
         * Performer column, contains the name of the performer.
         * <p>Type: TEXT</p>
         */
        public static final String PERFORMER = "performer";

        /**
         * Start time column, time the event is scheduled to begin in millis since the epoch.
         * <p>Type: INTEGER</p>
         */
        public static final String START_TIME = "start_time";

        /**
         * End time column, time the event is scheduled to finish in millis since the epoch.
         * <p>Type: INTEGER</p>
         */
        public static final String END_TIME = "end_time";

        /**
         * Adds all the given events to the database.
         *
         * @param context an application context
         * @param events  a list of events to add to the schedule
         * @return the number of events that were added
         */
        public static int add(@NonNull final Context context, @NonNull final List<Event> events) {
            final ContentResolver cr = context.getContentResolver();
            final List<ContentValues> rows = new ArrayList<>(events.size());
            for (Event event : events) {
                ContentValues values = new ContentValues();
                values.put(DAY, event.getDay().getTime());
                values.put(STAGE, event.getStage().getName());
                values.put(PERFORMER, event.getPerformer().getName());
                values.put(START_TIME, event.getStartTime().getTime());
                values.put(END_TIME, event.getEndTime().getTime());
                rows.add(values);
            }
            return cr.bulkInsert(Events.CONTENT_URI, rows.toArray(new ContentValues[rows.size()]));
        }

        /**
         * Removes all event records from the database.
         *
         * @param context an application context
         * @return the number of events that were deleted
         */
        public static int remove(@NonNull final Context context) {
            final ContentResolver cr = context.getContentResolver();
            return cr.delete(Events.CONTENT_URI, "1", null);
        }
    }
}
