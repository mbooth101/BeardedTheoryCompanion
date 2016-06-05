package uk.co.matbooth.beardedtheory.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.matbooth.beardedtheory.R;
import uk.co.matbooth.beardedtheory.model.Day;

public class StageListFragment extends Fragment {

    // Bundle arguments
    public static final String ARG_DAY = "day";

    // The day for which we are showing the stage list
    private Day day;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = getArguments().getParcelable(ARG_DAY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView)inflater.inflate(R.layout.list_fragment, container, false);
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setAdapter(new SimpleStringRecyclerViewAdapter(getContext()));
        return view;
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private String[] mValues = new String[]{"stage a", "stage b", "stage c", "stage d"};

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public String getValueAt(int position) {
            return mValues[position];
        }

        public SimpleStringRecyclerViewAdapter(Context context) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mValues[position];
            holder.mTextView.setText(mValues[position]);
        }

        @Override
        public int getItemCount() {
            return mValues.length;
        }
    }
}
