package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.enums.AsyncTaskType;
import com.logicalpanda.geoshare.interfaces.IHandleAsyncTaskPostExecute;
import com.logicalpanda.geoshare.pojos.Note;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by Ger on 08/01/2017.
 */
public class TestNotesAsyncTask extends AsyncTask<String, Void, Note> {

    public final AsyncTaskType taskType = AsyncTaskType.TestNotes;

    private Exception exception;
    private IHandleAsyncTaskPostExecute mCallingActivity;


    public TestNotesAsyncTask(IHandleAsyncTaskPostExecute callingActivity)
    {
        mCallingActivity = callingActivity;
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
        mCallingActivity.onAsyncTaskPostExecute(taskType);
    }
}