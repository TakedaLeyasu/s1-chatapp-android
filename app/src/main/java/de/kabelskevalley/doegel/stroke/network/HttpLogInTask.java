package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

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
            final String url = "http://chat.kabelskevalley.com:3000/api/login";
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
        if (user != null)
            mListener.onResult(user);
    }

}
