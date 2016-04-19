package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import de.kabelskevalley.doegel.stroke.entities.Channel;

public class HttpChannelTask extends AsyncTask<Void, Void, List<Channel>> {

    private OnHttpResultListener<List<Channel>> mListener;

    public HttpChannelTask(OnHttpResultListener<List<Channel>> listener){
        super();

        mListener = listener;
    }

    @Override
    protected List<Channel> doInBackground(Void... params) {
        try {
            final String url = "http://chat.kabelskevalley.com:3000/api/channels";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Channel[] channels = restTemplate.getForObject(url, Channel[].class);
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
