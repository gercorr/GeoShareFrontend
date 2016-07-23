package com.example.ger.myapplication;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Ger on 18/07/2016.
 */
class RetrieveNotesAsyncTask extends AsyncTask<String, Void, Note[]> {

    private Exception exception;
    private final GoogleMap mMap;
    public RetrieveNotesAsyncTask(GoogleMap gmap)
    {
        this.mMap = gmap;
    }


    protected Note[] doInBackground(String... urls) {
        try {
            final String url = "http://192.168.0.25:8080/rest/getAllNotes";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Note[] notes = restTemplate.getForObject(url, Note[].class);
            return notes;
        } catch (Exception e) {
            this.exception = e;
            System.out.println(e.toString());
            return new Note[0];
        }
    }

    protected void onPostExecute(Note[] feed) {
        // TODO: check this.exception
        // TODO: do something with the feed

        for (Note note: feed) {

            LatLng newLatLng = new LatLng(note.getLatitude(), note.getLongitude());
            //MarkerOptions marker = new MarkerOptions();
            //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromFile("C:\\Android\\placeholder.bmp");
            //marker.icon(bitmap);
            mMap.addMarker(new MarkerOptions().position(newLatLng).title(note.getText()));
        }

    }
}