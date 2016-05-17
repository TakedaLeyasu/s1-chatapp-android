package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.User;

public class ProfileActivity extends AppCompatActivity {

    private ImageLoader imageLoader;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance

        StorageHelper.Init(this, "stroke");
        user =(User) StorageHelper.getInstance().getObject("user", User.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profil");
        toolbar.setSubtitle(user.getName());
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        TextView textView = (TextView)findViewById(R.id.profile_name);
        Button th_button = (Button)findViewById(R.id.th_button);

        textView.setText(user.getName());

        if(user.getThumbnail()!=null)
        {
            ImageView imageView = (ImageView)findViewById(R.id.profile_image);
            EditText editText = (EditText)findViewById(R.id.th_editText);
            imageLoader.displayImage(user.getThumbnail(),imageView);
            editText.setText(user.getThumbnail());
        }

        th_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_thumbnail();
            }
        });

    }

    private void change_thumbnail()
    {
        ImageView imageView = (ImageView)findViewById(R.id.profile_image);
        EditText editText = (EditText)findViewById(R.id.th_editText);

        imageLoader.displayImage(editText.getText().toString(), imageView);

        user.setThumbnail(editText.getText().toString());
        StorageHelper.getInstance().storeObject("user", user);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ChannelListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
