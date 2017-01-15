package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.pojos.Note;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by Ger on 18/07/2016.
 */
public class CreateNoteAsyncTask extends AsyncTask<String, Void, Note> {

    private Note note;
    private final GoogleMap mMap;


    public CreateNoteAsyncTask(GoogleMap gmap, LatLng latLng, String text)
    {
        this.mMap = gmap;

        note = new Note( );
        note.setText(text);
        note.setLatitude(latLng.latitude);
        note.setLongitude(latLng.longitude);

    }

    public CreateNoteAsyncTask(GoogleMap gmap, LatLng latLng)
    {
        this(gmap, latLng, " ");
    }


    protected Note doInBackground(String... urls) {
        try {
            final String url = Config.restUrl + "rest/addNote/";
            RestTemplate restTemplate = new RestTemplate();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("text", note.getText())
                    .queryParam("lat", note.getLatitude())
                    .queryParam("long", note.getLongitude());


            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Note note = restTemplate.getForObject(builder.build().encode().toUri(), Note.class);
            return note;
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
        mMap.addMarker(new MarkerOptions().position(newLatLng).title(noteFromBackend.getText()));
    }
}