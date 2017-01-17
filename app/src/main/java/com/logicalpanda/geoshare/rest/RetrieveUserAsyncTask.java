package com.logicalpanda.geoshare.rest;

import android.os.AsyncTask;

import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.enums.AsyncTaskType;
import com.logicalpanda.geoshare.interfaces.IHandleAsyncTaskPostExecute;
import com.logicalpanda.geoshare.other.Globals;
import com.logicalpanda.geoshare.pojos.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

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

            final String url = Config.restUrl + "rest/updateAndRetrieveUser/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<User> entity = new HttpEntity<>(Globals.instance().currentUser,headers);
            Globals.instance().currentUser = restTemplate.exchange(url, HttpMethod.POST, entity, User.class).getBody();

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