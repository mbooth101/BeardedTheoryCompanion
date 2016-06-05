package uk.co.matbooth.beardedtheory.activities;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import uk.co.matbooth.beardedtheory.R;

enum Section {
    SCHEDULE(ScheduleFragment.class, R.string.nav_schedule),
    BOOKMARKS(BookmarksFragment.class, R.string.nav_bookmarks),
    NOWNEXT(NowNextFragment.class, R.string.nav_now_next),
    PERFORMERS(PerformersFragment.class, R.string.nav_performers);

    private final String fragmentClass;
    private final int titleStringId;

    Section(Class<? extends Fragment> fragmentClass, @StringRes int titleStringId) {
        this.fragmentClass = fragmentClass.getName();
        this.titleStringId = titleStringId;
    }

    public String getFragmentClass() {
        return fragmentClass;
    }

    @StringRes
    public int getTitleStringId() {
        return titleStringId;
    }
}
