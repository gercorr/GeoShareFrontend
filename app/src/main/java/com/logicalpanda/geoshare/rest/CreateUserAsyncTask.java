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

public class CreateUserAsyncTask extends AsyncTask<String, Void, User> {

    public final AsyncTaskType taskType = AsyncTaskType.CreateUser;

    private Exception exception;
    private IHandleAsyncTaskPostExecute mCallingActivity;

    public CreateUserAsyncTask(IHandleAsyncTaskPostExecute callingActivity)
    {
        mCallingActivity = callingActivity;
    }

    protected User doInBackground(String... urls) {
        try {

            final String url = Config.restUrl + "rest/createUser/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<User> entity = new HttpEntity<>(Globals.getCurrentUser(),headers);
            User currentUser = restTemplate.exchange(url, HttpMethod.POST, entity, User.class).getBody();
            Globals.setCurrentUser(currentUser);

            return Globals.getCurrentUser();

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