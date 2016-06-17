package uk.co.matbooth.beardedtheory.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class StageListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Bundle arguments
    public static final String ARG_DAY = "day";

    // The day in milliseconds for which we are showing the stage list
    private long day;

    private StageListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = getArguments().getLong(ARG_DAY);
        adapter = new StageListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.list_fragment, container, false);
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] cols = new String[]{Schedule.Events.STAGE};
        String where = Schedule.Events.DAY + " = ?";
        String order = Schedule.Events.STAGE + " ASC";
        return new CursorLoader(getActivity(), Schedule.Events.DISTINCT_URI, cols, where, new String[]{Long.toString(day)}, order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<String> stages = new ArrayList<>();
        if (data != null) {
            while (data.moveToNext()) {
                stages.add(data.getString(data.getColumnIndex(Schedule.Events.STAGE)));
            }
        }
        adapter.setStages(stages);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setStages(null);
    }

    public static class StageListAdapter extends RecyclerView.Adapter<StageListAdapter.ViewHolder> {

        private final List<String> stages = new ArrayList<>();

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final TextView text;

            public ViewHolder(View view) {
                super(view);
                this.text = (TextView) view.findViewById(android.R.id.text1);
            }
        }

        public void setStages(final List<String> stages) {
            this.stages.clear();
            if (stages != null) {
                this.stages.addAll(stages);
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
            holder.text.setText(stages.get(position));
        }

        @Override
        public int getItemCount() {
            return stages.size();
        }
    }
}
