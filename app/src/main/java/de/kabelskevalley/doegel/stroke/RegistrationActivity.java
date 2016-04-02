package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void create_profile(View view)
    {
        //add stuff here
        Intent intent = new Intent(this,ChannelListActivity.class);
        startActivity(intent);
    }
}
