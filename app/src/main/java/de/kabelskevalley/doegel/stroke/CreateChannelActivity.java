package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.network.HttpCreateChannelTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;

public class CreateChannelActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        Button button = (Button)findViewById(R.id.b_createChannel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)findViewById(R.id.e_ChannelName)).getText().toString();
                String thumbnail = ((EditText)findViewById(R.id.e_ChannelThumbnail)).getText().toString();

                Channel channel = new Channel(name,thumbnail);
                new HttpCreateChannelTask(mChannelListener,channel).execute();
            }
        });
    }

    private OnHttpResultListener mChannelListener = new OnHttpResultListener<Channel>() {
        @Override
        public void onResult(Channel channel) {
            Intent intent = new Intent(getBaseContext(), ChannelListActivity.class);
            startActivity(intent);
        }

        @Override
        public void onError(Exception e) {
            Log.e("CreateChannelActivity", e.getMessage(), e);
        }
    };
}
