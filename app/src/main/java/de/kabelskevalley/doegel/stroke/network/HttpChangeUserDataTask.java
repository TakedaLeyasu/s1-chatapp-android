package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.entities.User;

/**
 * Created by Hartmut on 06.06.2016.
 */
public class HttpChangeUserDataTask extends AsyncTask<User, User, User> {

    private OnHttpResultListener<User> mListener;
    private User user;
    private HashMap<String,String> mHashMap;

    public HttpChangeUserDataTask(OnHttpResultListener<User> listener, User user){
        super();
        this.user = user;
        mListener = listener;

        String token = user.getToken();
        mHashMap = new HashMap<>();
        mHashMap.put("token",token);
    }

    @Override
    protected User doInBackground(User... params) {
        try {
            final String url = Constants.BASE_URL + "/api/profile?token={token}";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            user = restTemplate.postForObject(url, user, User.class, mHashMap);
            return user;
        }
        catch (Exception e) {
            mListener.onError(e);
    }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        if (user.hasToken())
            mListener.onResult(user);
        else
            mListener.onError(new Exception());
    }

}
