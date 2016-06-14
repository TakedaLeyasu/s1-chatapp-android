package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.entities.LogIn_Data;
import de.kabelskevalley.doegel.stroke.entities.User;

/**
 * Created by livestream on 12.04.2016.
 */
public class HttpLogInTask extends AsyncTask<LogIn_Data, LogIn_Data, User> {

    private OnHttpResultListener<User> mListener;
    private LogIn_Data logIn_data;

    public HttpLogInTask(OnHttpResultListener<User> listener, LogIn_Data logIn_data){
        super();
        this.logIn_data = logIn_data;
        mListener = listener;
    }

    @Override
    protected User doInBackground(LogIn_Data... params) {
        try {
            String url = logIn_data.hasToken()
                    ? Constants.BASE_URL + "/api/auth" // check auth token
                    : Constants.BASE_URL + "/api/login"; // do a full login

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            return restTemplate.postForObject(url, logIn_data, User.class);
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
