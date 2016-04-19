package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import java.util.List;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.LogIn_Data;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.HttpLogInTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;
import de.kabelskevalley.doegel.stroke.network.SocketHelper;

public class LoginActivity extends AppCompatActivity implements OnHttpResultListener<User> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        StorageHelper.Init(this, "stroke");
    }

    public void logIn(View view)
    {
        EditText name = (EditText)findViewById(R.id.editText_N);
        EditText password = (EditText)findViewById(R.id.editText_P);

        LogIn_Data logIn_data = new LogIn_Data(name.getText().toString(), password.getText().toString());
        new HttpLogInTask(this,logIn_data).execute();
    }

    public void register(View view)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        //add stuff here
    }

    @Override
    public void onResult(User user) {
        StorageHelper.getInstance()
                .storeObject("user", user);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Lade neue Activity
                Intent intent = new Intent(getApplicationContext(), ChannelListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onError(Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Server Probleme", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
