package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.entities.LogInData;
import de.kabelskevalley.doegel.stroke.entities.User;

/**
 * Created by livestream on 12.04.2016.
 */
public class HttpLogInTask extends AsyncTask<LogInData, LogInData, User> {

    private OnHttpResultListener<User> mListener;
    private LogInData logInData;

    public HttpLogInTask(OnHttpResultListener<User> listener, LogInData logIn_data){
        super();
        this.logInData = logIn_data;
        mListener = listener;
    }

    @Override
    protected User doInBackground(LogInData... params) {
        try {
            String url = logInData.hasToken()
                    ? Constants.BASE_URL + "/api/auth" // check auth token
                    : Constants.BASE_URL + "/api/login"; // do a full login

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            return restTemplate.postForObject(url, logInData, User.class);
        } catch (Exception e) {
            mListener.onError(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        if (user != null)
            mListener.onResult(user);
    }

}
