package com.logicalpanda.geoshare.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.model.Marker;
import com.logicalpanda.geoshare.R;
import com.logicalpanda.geoshare.other.Globals;
import com.logicalpanda.geoshare.pojos.Note;
import com.logicalpanda.geoshare.rest.RetrieveUserNoteListAsyncTask;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ger on 25/01/2017.
 */

public class NoteListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        menu.findItem(R.id.action_list).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_refresh).setVisible(false);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Marker currentMarker = Globals.getCurrentMarker();
        if(currentMarker != null)
        {
            ArrayList<Note> notesArrayList = (ArrayList<Note>)currentMarker.getTag();
            Collections.sort(notesArrayList, new Note());
            Note [] notesArray = notesArrayList.toArray(new Note[notesArrayList.size()]);
            NoteListAdapter mAdapter = new NoteListAdapter(notesArray);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            new RetrieveUserNoteListAsyncTask(mRecyclerView).execute();
        }
        Globals.setCurrentMarker(null);
    }
}