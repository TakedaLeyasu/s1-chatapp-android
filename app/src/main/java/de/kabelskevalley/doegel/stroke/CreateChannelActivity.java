package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.network.HttpCreateChannelTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;

public class CreateChannelActivity extends AppCompatActivity{

    private ImageView imageView;
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
                if(thumbnail.isEmpty() || !(thumbnail.toString().contains(".jpg")^ thumbnail.toString().contains(".png")))
                    thumbnail=null;

                if(!name.isEmpty()) {
                    Channel channel = new Channel(name, thumbnail);
                    new HttpCreateChannelTask(mChannelListener, channel).execute();
                }
            }
        });
        imageView = (ImageView)findViewById(R.id.imageView_newChannelPicture);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(getDrawable(R.drawable.channel_picture));
        }
        EditText editText = (EditText)findViewById(R.id.e_ChannelThumbnail);
        editText.addTextChangedListener(textWatcher);
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.toString().contains("jpg")||s.toString().contains("png"))
            ImageLoader.getInstance().displayImage(s.toString(),imageView);
            else
            {
                imageView.setImageResource(R.drawable.channel_picture);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
