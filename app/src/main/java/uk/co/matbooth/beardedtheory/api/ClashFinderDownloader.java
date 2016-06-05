package uk.co.matbooth.beardedtheory.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import uk.co.matbooth.beardedtheory.db.Schedule;

public class ClashFinderDownloader {

    // Preferences
    private static final String PREFS_FILE = "downloader_prefs";
    private static final String LAST_MODIFIED_TAG_PREF = "last_modified_tag";

    // Intent actions
    public static final String ACTION_SCHEDULE_DOWNLOADED = "uk.co.matbooth.beardedtheory.action.SCHEDULE_DOWNLOADED";
    public static final String ACTION_SCHEDULE_DOWNLOADED_RESULT = "uk.co.matbooth.beardedtheory.action.extra.SCHEDULE_DOWNLOADED_RESULT";

    private static final int TIMEOUT = 10000;
    private static final String URL = "http://clashfinder.com/data/event/beardedtheory2016.xml";

    private static final Lock scheduleLock = new ReentrantLock();

    public static void downloadSchedule(Context context) {
        if (!scheduleLock.tryLock()) {
            // If a download is already in progress, return immediately
            return;
        }
        int result = -1;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);

            // Try not to download anything if nothing has changed upstream
            String lastModifiedTag = getLastModifiedTag(context);
            if (lastModifiedTag != null) {
                connection.addRequestProperty("If-Modified-Since", lastModifiedTag);
            }
            connection.connect();
            lastModifiedTag = connection.getHeaderField("Last-Modified");

            // Check HTTP response code
            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                if (code == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    // Schedule has not changed, nothing to do
                    result = 0;
                    return;
                } else {
                    // There was some error fetching the schedule
                    throw new IOException("Error fetching schedule, server responded with: " + code);
                }
            }

            InputStream in = null;
            try {
                // Schedule was sent, lets parse it
                in = new BufferedInputStream(connection.getInputStream());
                ClashFinderParser parser = new ClashFinderParser();
                parser.parse(in);
                result = parser.getEvents().size();

                // Store schedule in database
                Schedule.Events.remove(context);
                Schedule.Days.remove(context);
                Schedule.Events.add(context, parser.getEvents());
                Schedule.Days.add(context, parser.getDays());

                // Set schedules's last modified tag
                SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
                prefs.edit().putString(LAST_MODIFIED_TAG_PREF, lastModifiedTag).apply();
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_SCHEDULE_DOWNLOADED).putExtra(ACTION_SCHEDULE_DOWNLOADED_RESULT, result));
            scheduleLock.unlock();
        }
    }

    /**
     * Utility to return the time that the upstream schedule was last modified. For use in HTTP headers.
     *
     * @return A formatted date/time string or null if not available.
     */
    public static String getLastModifiedTag(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getString(LAST_MODIFIED_TAG_PREF, null);
    }
}
