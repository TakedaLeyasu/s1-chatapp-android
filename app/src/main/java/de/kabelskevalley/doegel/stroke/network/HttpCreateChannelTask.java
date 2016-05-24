package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.User;

/**
 * Created by Hartmut on 24.05.2016.
 */
public class HttpCreateChannelTask  extends AsyncTask<Void, Void,Channel> {

    private OnHttpResultListener<Channel> mListener;
    private Channel mChannel;
    private String token;
    private HashMap<String,String> hashMap;

    public HttpCreateChannelTask(OnHttpResultListener<Channel> listener, Channel channel){
        super();
        mChannel = channel;
        mListener = listener;
        token = ((User) StorageHelper.getInstance().getObject("user",User.class)).getToken();

        hashMap = new HashMap<String,String>();
        hashMap.put("token",token);


    }

    @Override
    protected Channel doInBackground(Void... params) {
        try {
            final String url = Constants.BASE_URL + "/api/channels";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Channel channel = restTemplate.postForObject(url,mChannel,Channel.class, hashMap);
            return channel;
        } catch (Exception e) {
            mListener.onError(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Channel channel) {
        mListener.onResult(channel);
    }

}
