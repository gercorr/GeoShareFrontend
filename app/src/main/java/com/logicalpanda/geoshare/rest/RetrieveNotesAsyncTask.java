package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.other.LatLngForGrouping;
import com.logicalpanda.geoshare.pojos.Note;
import com.logicalpanda.geoshare.pojos.NotesRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RetrieveNotesAsyncTask extends AsyncTask<String, Void, Note[]> {

    private Exception exception;
    private final GoogleMap mMap;
    private final HashMap<LatLngForGrouping, ArrayList<Note>> notes;
    private NotesRequest notesRequest;

    public RetrieveNotesAsyncTask(GoogleMap gmap, HashMap<LatLngForGrouping, ArrayList<Note>> mAllCurrentNotes, LatLng currentLatLang, String nickname)
    {
        this.mMap = gmap;
        notes = mAllCurrentNotes;
        notesRequest = new NotesRequest();
        notesRequest.setDistance(Double.parseDouble(Config.distanceToRetrieve));
        notesRequest.setLatitude(currentLatLang.latitude);
        notesRequest.setLongitude(currentLatLang.longitude);
        notesRequest.setNickname(nickname);
    }


    protected Note[] doInBackground(String... urls) {
        try {


            final String url = Config.restUrl + "rest/getAllNotesWithinDistance/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NotesRequest> entity = new HttpEntity<>(notesRequest,headers);
            return restTemplate.exchange(url, HttpMethod.POST, entity, Note[].class).getBody();

        } catch (Exception e) {
            this.exception = e;
            System.out.println(e.toString());
            return new Note[0];
        }
    }

    protected void onPostExecute(Note[] feed) {
        // TODO: check this.exception

        mMap.clear();
        notes.clear();

        //Add to list
        for (Note note: feed) {
            LatLngForGrouping groupableLatLng = new LatLngForGrouping(note.getLatitude(),note.getLongitude());
            ArrayList<Note> currentList = notes.get(groupableLatLng);
            if(currentList == null){
                currentList = new ArrayList<Note>();
                notes.put(groupableLatLng, currentList);
            }
            currentList.add(note);
        }

        //add markers to map
        for(Map.Entry<LatLngForGrouping, ArrayList<Note>> entry : notes.entrySet()) {
            LatLngForGrouping key = entry.getKey();
            ArrayList<Note> value = entry.getValue();

            LatLng newLatLng = new LatLng(key.getLatitude(), key.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions().position(newLatLng);
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(value);
        }


    }

}