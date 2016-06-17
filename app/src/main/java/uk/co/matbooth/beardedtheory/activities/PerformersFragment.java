package uk.co.matbooth.beardedtheory.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.matbooth.beardedtheory.R;
import uk.co.matbooth.beardedtheory.db.Schedule;

public class PerformersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private PerformerListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PerformerListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.list_fragment, container, false);
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setAdapter(adapter);

        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.main_tabs);
        tabs.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] cols = new String[]{Schedule.Events.PERFORMER};
        String order = Schedule.Events.PERFORMER;
        return new CursorLoader(getActivity(), Schedule.Events.DISTINCT_URI, cols, null, null, order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<String> list = new ArrayList<>();
        if (data != null) {
            while (data.moveToNext()) {
                list.add(data.getString(data.getColumnIndex(Schedule.Events.PERFORMER)));
            }
        }
        adapter.setPerformers(list);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setPerformers(null);
    }

    public static class PerformerListAdapter extends RecyclerView.Adapter<PerformerListAdapter.ViewHolder> {

        private final List<String> performers = new ArrayList<>();

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final TextView text;

            public ViewHolder(View itemView) {
                super(itemView);
                this.text = (TextView) itemView.findViewById(android.R.id.text1);
            }
        }

        public void setPerformers(final List<String> performers) {
            this.performers.clear();
            if (performers != null) {
                this.performers.addAll(performers);
            }
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(performers.get(position));
        }

        @Override
        public int getItemCount() {
            return performers.size();
        }
    }
}
