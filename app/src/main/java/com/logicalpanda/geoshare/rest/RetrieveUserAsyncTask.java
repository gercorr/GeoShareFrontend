package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.enums.AsyncTaskType;
import com.logicalpanda.geoshare.interfaces.IHandleAsyncTaskPostExecute;
import com.logicalpanda.geoshare.other.Globals;
import com.logicalpanda.geoshare.pojos.User;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by Ger on 18/07/2016.
 */
public class RetrieveUserAsyncTask extends AsyncTask<String, Void, User> {

    public final AsyncTaskType taskType = AsyncTaskType.RetrieveUser;

    private Exception exception;
    private IHandleAsyncTaskPostExecute mCallingActivity;

    public RetrieveUserAsyncTask(IHandleAsyncTaskPostExecute callingActivity)
    {
        mCallingActivity = callingActivity;
    }

    protected User doInBackground(String... urls) {
        try {

            User user = Globals.instance().currentUser;

            final String url = Config.restUrl + "rest/updateAndRetrieveUser/";
            RestTemplate restTemplate = new RestTemplate();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("id", user.getId())
                    .queryParam("nickname", user.getNickname())
                    .queryParam("google_instance_id", user.getGoogle_instance_id())
                    .queryParam("email_address", user.getEmail_address());


            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Globals.instance().currentUser = restTemplate.getForObject(builder.build().encode().toUri(), User.class);

            return Globals.instance().currentUser;

        } catch (Exception e) {
            this.exception = e;
            System.out.println(e.toString());
            return null;
        }
    }

    protected void onPostExecute(User feed) {
        mCallingActivity.onAsyncTaskPostExecute(taskType);
    }
}