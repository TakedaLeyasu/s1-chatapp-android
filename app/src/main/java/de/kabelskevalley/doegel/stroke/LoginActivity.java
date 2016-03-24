package de.kabelskevalley.doegel.stroke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onButtonClick(View view)
    {

        if(view.getId()== R.id.Banmelden)
        {
            Intent i = new Intent(LoginActivity.this, ChannelListActivity.class);
            startActivity(i);
        }
    }
}
