package uk.co.matbooth.beardedtheory.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.matbooth.beardedtheory.R;
import uk.co.matbooth.beardedtheory.db.Schedule;

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private DaysAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DaysAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewPager pager = (ViewPager) inflater.inflate(R.layout.pager_fragment, container, false);
        pager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.main_tabs);
        tabs.setVisibility(View.VISIBLE);
        tabs.setupWithViewPager(pager);
        return pager;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] cols = new String[]{Schedule.Events.DAY};
        return new CursorLoader(getActivity(), Schedule.Events.DISTINCT_URI, cols, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Long> days = new ArrayList<>();
        if (data != null) {
            while (data.moveToNext()) {
                days.add(data.getLong(data.getColumnIndex(Schedule.Events.DAY)));
            }
        }
        adapter.setDays(days);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setDays(null);
    }

    /**
     * Adapter to generate a stage list fragment for each day.
     */
    private static class DaysAdapter extends FragmentStatePagerAdapter {

        private final List<Long> days = new ArrayList<>();

        public DaysAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setDays(final List<Long> days) {
            this.days.clear();
            if (days != null) {
                this.days.addAll(days);
            }
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new StageListFragment();
            Bundle args = new Bundle();
            args.putLong(StageListFragment.ARG_DAY, days.get(position));
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return days.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            long date = days.get(position);
            // It's pretty amazing that these suffixes are not built into the formatter...
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date);
            String suffix;
            switch (cal.get(Calendar.DAY_OF_MONTH) % 10) {
                case 1:
                    suffix = "st";
                    break;
                case 2:
                    suffix = "nd";
                    break;
                case 3:
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
                    break;
            }
            if (cal.get(Calendar.DAY_OF_MONTH) >= 11 && cal.get(Calendar.DAY_OF_MONTH) <= 13) {
                suffix = "th";
            }
            return String.format(Locale.UK, "%1$ta %1$te", new Date(date)) + suffix;
        }
    }
}