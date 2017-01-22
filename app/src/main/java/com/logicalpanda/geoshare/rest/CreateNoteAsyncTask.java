package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.pojos.Note;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class CreateNoteAsyncTask extends AsyncTask<String, Void, Note> {

    private Note note;
    private final GoogleMap mMap;


    public CreateNoteAsyncTask(GoogleMap gmap, Note noteToSend)
    {
        this.mMap = gmap;
        note = noteToSend;
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
        // TODO: check this.exception
        // TODO: do something with the feed


        LatLng newLatLng = new LatLng(noteFromBackend.getLatitude(), noteFromBackend.getLongitude());
        //MarkerOptions marker = new MarkerOptions();
        //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromFile("C:\\Android\\placeholder.bmp");
        //marker.icon(bitmap);
        mMap.addMarker(new MarkerOptions().position(newLatLng).title(noteFromBackend.getUser().getNickname()).snippet(noteFromBackend.getText()));
    }
}