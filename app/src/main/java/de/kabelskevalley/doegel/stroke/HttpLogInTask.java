package de.kabelskevalley.doegel.stroke;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.LogIn_Data;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListner;

/**
 * Created by livestream on 12.04.2016.
 */
public class HttpLogInTask extends AsyncTask<LogIn_Data, LogIn_Data, User> {

    private OnHttpResultListner mListener;
    private LogIn_Data logIn_data;

    public HttpLogInTask(OnHttpResultListner listener, LogIn_Data logIn_data){
        super();
        this.logIn_data = logIn_data;
        mListener = listener;
    }

    @Override
    protected User doInBackground(LogIn_Data... params) {
        try {
            final String url = "http://chat.kabelskevalley.com:3000/login";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            User user = restTemplate.postForObject(url, logIn_data, User.class);

            return user;
        } catch (Exception e) {
            mListener.onError(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        mListener.onResult(user);
    }

}
