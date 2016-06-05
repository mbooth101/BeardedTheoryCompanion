package uk.co.matbooth.beardedtheory.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class NowNextListFragment extends Fragment {

    // Bundle arguments
    public static final String ARG_NOW = "now";

    // Showing events that are happening now or next
    private boolean now;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        now = getArguments().getBoolean(ARG_NOW);
    }
}
