package uk.co.matbooth.beardedtheory.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.matbooth.beardedtheory.R;

public class NowNextFragment extends Fragment {

    private NowNextAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NowNextAdapter(getChildFragmentManager(), getResources());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_fragment, container, false);
        ViewPager pager = (ViewPager) view.findViewById(R.id.main_pager);
        pager.setAdapter(adapter);
        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.main_tabs);
        tabs.setVisibility(View.VISIBLE);
        tabs.setupWithViewPager(pager);
        return view;
    }

    /**
     * Adapter to generate the now and next fragments.
     */
    private static class NowNextAdapter extends FragmentStatePagerAdapter {

        private final Resources resources;

        public NowNextAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            this.resources = resources;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new NowNextListFragment();
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    args.putBoolean(NowNextListFragment.ARG_NOW, true);
                    break;
                case 1:
                    args.putBoolean(NowNextListFragment.ARG_NOW, false);
                    break;
            }
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return resources.getString(R.string.now_tab);
                case 1:
                    return resources.getString(R.string.next_tab);
            }
            return null;
        }
    }
}
