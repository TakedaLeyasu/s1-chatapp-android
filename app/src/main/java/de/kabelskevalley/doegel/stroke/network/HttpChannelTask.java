package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.User;

public class HttpChannelTask extends AsyncTask<Void, Void, List<Channel>> {

    private OnHttpResultListener<List<Channel>> mListener;
    private HashMap<String, String> mHashMap;

    public HttpChannelTask(OnHttpResultListener<List<Channel>> listener){
        super();

        mListener = listener;

        String token = ((User) StorageHelper.getInstance().getObject("user",User.class)).getToken();

        mHashMap = new HashMap<>();
        mHashMap.put("token", token);
    }

    @Override
    protected List<Channel> doInBackground(Void... params) {
        try {
            final String url = Constants.BASE_URL + "/api/channels?token={token}";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Channel[] channels = restTemplate.getForObject(url, Channel[].class, mHashMap);
            return Arrays.asList(channels);
        } catch (Exception e) {
            mListener.onError(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Channel> channels) {
        mListener.onResult(channels);
    }

}
