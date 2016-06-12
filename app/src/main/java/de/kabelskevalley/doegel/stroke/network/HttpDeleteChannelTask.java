package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

import de.kabelskevalley.doegel.stroke.Constants;
import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.User;

/**
 * Created by Hartmut on 12.06.2016.
 */
public class HttpDeleteChannelTask extends AsyncTask<Void, Void, List<Channel>> {

    private OnHttpResultListener<List<Channel>> mListener;
    private HashMap<String, String> mHashMap;
    private List<Channel> channels;
    Channel mChannel;

    public HttpDeleteChannelTask(OnHttpResultListener<List<Channel>> listener,Channel channel, List<Channel> channels){
        super();
        mListener = listener;
        String token = ((User) StorageHelper.getInstance().getObject("user",User.class)).getToken();
        mHashMap = new HashMap<>();
        mHashMap.put("token", token);
        mHashMap.put("id",channel.getId());
        this.channels = channels;
        this.mChannel = channel;
    }

    @Override
    protected List<Channel> doInBackground(Void... params) {
        try {
            final String url = Constants.BASE_URL + "/api/channels/{id}?token={token}";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.delete(url,mHashMap);
            channels.remove(mChannel);
            return channels;

        } catch (Exception e) {
            mListener.onError(e);
        }
        return channels;
    }

    @Override
    protected void onPostExecute(List<Channel> channels) {
        mListener.onResult(channels);
    }

}
