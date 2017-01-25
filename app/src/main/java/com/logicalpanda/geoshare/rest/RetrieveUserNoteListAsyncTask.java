package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.logicalpanda.geoshare.activities.NoteListAdapter;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.other.Globals;
import com.logicalpanda.geoshare.pojos.Note;
import com.logicalpanda.geoshare.pojos.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class RetrieveUserNoteListAsyncTask extends AsyncTask<String, Void, Note[]> {

    private final RecyclerView mRecyclerView;

    public RetrieveUserNoteListAsyncTask(RecyclerView recyclerView)
    {
        mRecyclerView = recyclerView;
    }


    protected Note[] doInBackground(String... urls) {
        try {


            final String url = Config.restUrl + "rest/getAllNotesForUser/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<User> entity = new HttpEntity<>(Globals.getCurrentUser(),headers);
            return restTemplate.exchange(url, HttpMethod.POST, entity, Note[].class).getBody();

        } catch (Exception e) {
            System.out.println(e.toString());
            return new Note[0];
        }
    }

    protected void onPostExecute(Note[] feed) {

        NoteListAdapter mAdapter = new NoteListAdapter(feed);
        mRecyclerView.setAdapter(mAdapter);

    }
}