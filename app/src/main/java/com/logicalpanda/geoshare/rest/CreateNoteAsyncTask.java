package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.other.LatLngForGrouping;
import com.logicalpanda.geoshare.pojos.Note;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateNoteAsyncTask extends AsyncTask<String, Void, Note> {

    private Note note;
    private final GoogleMap mMap;
    private final HashMap<LatLngForGrouping, ArrayList<Note>> notes;


    public CreateNoteAsyncTask(GoogleMap gmap, HashMap<LatLngForGrouping, ArrayList<Note>> allCurrentNotes, Note noteToSend)
    {
        this.mMap = gmap;
        note = noteToSend;
        notes = allCurrentNotes;
    }

    protected Note doInBackground(String... urls) {
        try {
            final String url = Config.restUrl + "rest/addNote/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Note> entity = new HttpEntity<>(note,headers);
            return restTemplate.exchange(url, HttpMethod.POST, entity, Note.class).getBody();

        } catch (Exception e) {
            System.out.println(e.toString());
            return new Note();
        }
    }

    protected void onPostExecute(Note noteFromBackend) {
        LatLngForGrouping groupableLatLng = new LatLngForGrouping(noteFromBackend.getLatitude(),noteFromBackend.getLongitude());
        ArrayList<Note> currentList = notes.get(groupableLatLng);
        if(currentList == null){
            currentList = new ArrayList<Note>();
            notes.put(groupableLatLng, currentList);
            LatLng newLatLng = new LatLng(groupableLatLng.getLatitude(), groupableLatLng.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(newLatLng);
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(currentList);
        }
        currentList.add(noteFromBackend);
    }
}