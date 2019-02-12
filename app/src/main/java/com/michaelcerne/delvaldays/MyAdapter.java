package com.michaelcerne.delvaldays;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    MyAdapter(JSONArray myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            holder.summaryView.setText(mDataset.getJSONObject(position).getString("summary"));
            holder.dateView.setText(mDataset.getJSONObject(position).getString("dateFormatted"));
        } catch (JSONException e) {
            Log.e("JSON", "Couldn't obtain JSON data");
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout row;
        TextView summaryView;
        TextView dateView;

        // each data item is just a string in this case
        MyViewHolder(View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.row_constraint);
            summaryView = itemView.findViewById(R.id.summary);
            dateView = itemView.findViewById(R.id.date);
        }
    }
}