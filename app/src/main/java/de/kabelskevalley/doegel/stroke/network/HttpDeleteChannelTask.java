package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.User;

/**
 * Created by Hartmut on 12.06.2016.
 */
public class HttpDeleteChannelTask extends AsyncTask<Void, Void, Void> {

    private OnHttpResultListener<Boolean> mListener;
    private HashMap<String, String> mHashMap;
    Channel mChannel;

    public HttpDeleteChannelTask(OnHttpResultListener<Boolean> listener,Channel channel){
        super();
        mListener = listener;
        String token = ((User) StorageHelper.getInstance().getObject("user",User.class)).getToken();
        mHashMap = new HashMap<>();
        mHashMap.put("token", token);
        mHashMap.put("id", URLEncoder.encode(channel.getId()));

        this.mChannel = channel;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            final String url = Constants.BASE_URL + "/api/channels/{id}?token={token}";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.delete(url,mHashMap);
        } catch (Exception e) {
            mListener.onError(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        mListener.onResult(true);
    }
}
