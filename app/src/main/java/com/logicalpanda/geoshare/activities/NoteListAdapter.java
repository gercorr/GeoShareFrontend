package com.logicalpanda.geoshare.activities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logicalpanda.geoshare.R;
import com.logicalpanda.geoshare.pojos.Note;

/**
 * Created by Ger on 25/01/2017.
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteHolder> {
    private Note[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class NoteHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;

        public NoteHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
        }
    }

    public NoteListAdapter(Note[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_element, parent, false);

        NoteHolder vh = new NoteHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.txtHeader.setText(mDataset[position].getUser().getNickname());
        holder.txtFooter.setText(mDataset[position].getText());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}