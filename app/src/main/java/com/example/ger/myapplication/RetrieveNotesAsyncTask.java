package com.example.ger.myapplication;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;

/**
 * Created by Ger on 18/07/2016.
 */
class RetrieveNotesAsyncTask extends AsyncTask<String, Void, Note[]> {

    private Exception exception;
    private final GoogleMap mMap;
    private final ArrayList<Marker> markers;
    private LatLng lastLatLng;

    public RetrieveNotesAsyncTask(GoogleMap gmap, ArrayList<Marker> currentMarkers, LatLng currentLatLang)
    {
        this.mMap = gmap;
        markers = currentMarkers;
        lastLatLng = currentLatLang;
    }


    protected Note[] doInBackground(String... urls) {
        try {

            final String url = Config.restUrl + "rest/getAllNotesWithinDistance/";
            RestTemplate restTemplate = new RestTemplate();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("lat", lastLatLng.latitude)
                    .queryParam("long", lastLatLng.longitude)
                    .queryParam("distance", 0.02);//might move distance server side


            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Note[] notes = restTemplate.getForObject(builder.build().encode().toUri(),  Note[].class);
            return notes;

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