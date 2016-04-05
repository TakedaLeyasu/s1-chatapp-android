package de.kabelskevalley.doegel.stroke.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import de.kabelskevalley.doegel.stroke.R;
import de.kabelskevalley.doegel.stroke.entities.Channel;

public class HttpRequestTask extends AsyncTask<Void, Void, List<Channel>> {

    private OnHttpResultListner mListener;

    public HttpRequestTask(OnHttpResultListner listener){
        super();

        mListener = listener;
    }

    @Override
    protected List<Channel> doInBackground(Void... params) {
        try {
            final String url = "http://chat.kabelskevalley.com:3000/channels";
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
