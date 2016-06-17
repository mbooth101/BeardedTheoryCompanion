package uk.co.matbooth.beardedtheory.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.matbooth.beardedtheory.BuildConfig;
import uk.co.matbooth.beardedtheory.R;
import uk.co.matbooth.beardedtheory.api.ClashFinderDownloader;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private static final String STATE_CURRENT_SECTION = "current_section";

    // The currently displayed section fragment
    private Section current;

    private final BroadcastReceiver scheduleDownloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(ClashFinderDownloader.ACTION_SCHEDULE_DOWNLOADED_RESULT, -1);
            String message;
            switch (result) {
                case -1:
                    message = getString(R.string.schedule_download_error);
                    break;
                case 0:
                    message = getString(R.string.schedule_download_uptodate);
                    break;
                default:
                    message = getResources().getQuantityString(R.plurals.schedule_download_complete, result, result);
            }
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Configure the navigation drawer toggle
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);

        // Add navigation listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Restore the currently viewed fragment
        if (savedInstanceState == null) {
            navigateToSection(Section.SCHEDULE);
        } else {
            current = Section.values()[savedInstanceState.getInt(STATE_CURRENT_SECTION)];
        }
        getSupportActionBar().setTitle(current.getTitleStringId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_SECTION, current.ordinal());
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(scheduleDownloadReceiver, new IntentFilter(ClashFinderDownloader.ACTION_SCHEDULE_DOWNLOADED));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(scheduleDownloadReceiver);

        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.option_item_refresh:
                new DownloadScheduleAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_item_schedule:
                navigateToSection(Section.SCHEDULE);
                break;
            case R.id.nav_item_bookmarks:
                navigateToSection(Section.BOOKMARKS);
                break;
            case R.id.nav_item_now_next:
                navigateToSection(Section.NOWNEXT);
                break;
            case R.id.nav_item_performers:
                navigateToSection(Section.PERFORMERS);
                break;
            case R.id.nav_item_settings:
                break;
            case R.id.nav_item_about:
                new AboutDialogFragment().show(getSupportFragmentManager(), "about");
                break;
        }
        return true;
    }

    private void navigateToSection(@NonNull final Section section) {
        if (section != current) {
            // Replace old section fragment with newly selected one
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (current != null) {
                Fragment f = fm.findFragmentByTag(current.name());
                if (f != null) {
                    ft.remove(f);
                }
            }
            Fragment f = Fragment.instantiate(this, section.getFragmentClass());
            ft.add(R.id.main_content, f, section.name());
            ft.commit();
            current = section;
        }
        getSupportActionBar().setTitle(current.getTitleStringId());
    }

    private static class DownloadScheduleAsyncTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        public DownloadScheduleAsyncTask(final Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(Void... args) {
            ClashFinderDownloader.downloadSchedule(context);
            return null;
        }
    }

    /**
     * A noddy fragment to show an "about" dialog.
     */
    public static class AboutDialogFragment extends AppCompatDialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String title = String.format("%1$s %2$s", getString(R.string.app_name), BuildConfig.VERSION_NAME);
            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getResources().getText(R.string.about_text))
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }

        @Override
        public void onStart() {
            super.onStart();
            // Make links clickable; must be called after the dialog is shown
            ((TextView) getDialog().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
