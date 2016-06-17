package uk.co.matbooth.beardedtheory.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.matbooth.beardedtheory.R;

public class BookmarksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.list_fragment, container, false);
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));

        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.main_tabs);
        tabs.setVisibility(View.GONE);
        return view;
    }
}
