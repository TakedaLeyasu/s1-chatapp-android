package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.entities.LogInData;
import de.kabelskevalley.doegel.stroke.entities.User;

/**
 * Created by Hartmut on 26.04.2016.
 */
public class HttpRegistrationTask extends AsyncTask<LogInData, LogInData, User> {

    private OnHttpResultListener<User> mListener;
    private LogInData registration_Data;

    public HttpRegistrationTask(OnHttpResultListener<User> listener, LogInData logIn_data){
        super();
        this.registration_Data = logIn_data;
        mListener = listener;
    }

    @Override
    protected User doInBackground(LogInData... params) {
        try {
            final String url = Constants.BASE_URL + "/api/register";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            User user = restTemplate.postForObject(url, registration_Data, User.class);

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
