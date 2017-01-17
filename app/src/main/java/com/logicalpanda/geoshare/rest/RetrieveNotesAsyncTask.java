package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.pojos.Note;
import com.logicalpanda.geoshare.pojos.NotesRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class RetrieveNotesAsyncTask extends AsyncTask<String, Void, Note[]> {

    private Exception exception;
    private final GoogleMap mMap;
    private final ArrayList<Marker> markers;
    private NotesRequest notesRequest;

    public RetrieveNotesAsyncTask(GoogleMap gmap, ArrayList<Marker> currentMarkers, LatLng currentLatLang)
    {
        this.mMap = gmap;
        markers = currentMarkers;
        notesRequest = new NotesRequest();
        notesRequest.setDistance(0.2);
        notesRequest.setLatitude(currentLatLang.latitude);
        notesRequest.setLongitude(currentLatLang.longitude);
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

        //cleardown old markers (except user)
        for (Marker marker : markers)
        {
            marker.remove();
        }

        for (Note note: feed) {

            LatLng newLatLng = new LatLng(note.getLatitude(), note.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(newLatLng).title(note.getText()));
            markers.add(marker);

        }

    }
}