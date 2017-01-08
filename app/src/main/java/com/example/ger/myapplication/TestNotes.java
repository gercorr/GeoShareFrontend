package com.example.ger.myapplication;

import android.os.AsyncTask;
import android.view.View;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by Ger on 08/01/2017.
 */
class TestNotesAsyncTask extends AsyncTask<String, Void, Note> {

    private Exception exception;
    private View[] mViewsToDisplay;
    private View[] mViewsToHide;


    public TestNotesAsyncTask(View[] viewsToDisplay, View[] viewsToHide)
    {
        mViewsToDisplay = viewsToDisplay;
        mViewsToHide = viewsToHide;
    }

    protected Note doInBackground(String... urls) {
        try {

            final String url = Config.restUrl + "rest/getTestNote/";
            RestTemplate restTemplate = new RestTemplate();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Note note = restTemplate.getForObject(builder.build().encode().toUri(),  Note.class);
            return note;

        } catch (Exception e) {
            this.exception = e;
            System.out.println(e.toString());
            return null;
        }
    }

    protected void onPostExecute(Note feed) {

        for (View viewToDisplay : mViewsToDisplay) {
            viewToDisplay.setVisibility(View.VISIBLE);
        }
        for (View viewToHide : mViewsToHide) {
            viewToHide.setVisibility(View.GONE);
        }
    }
}