package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.LogIn_Data;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.HttpLogInTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;

public class LoginActivity extends AppCompatActivity implements OnHttpResultListener<User> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StorageHelper.Init(this, "stroke");
        User user =(User) StorageHelper.getInstance().getObject("user",User.class);

       if(user!=null)
        {
            Log.i("  Token ", user.getToken());
            new HttpLogInTask(this, new LogIn_Data(user.getToken())).execute();
        }
        else {
           Log.i("  Token ", "null");
        }
        setContentView(R.layout.activity_log_in);
    }

    public void logIn(View view)
    {
        String name = ((EditText)findViewById(R.id.editText_N)).getText().toString();
        String password = ((EditText)findViewById(R.id.editText_P)).getText().toString();

        name = name.replaceAll(" ","");

        LogIn_Data logIn_data = new LogIn_Data(name, password);
        new HttpLogInTask(this,logIn_data).execute();
    }

    public void register(View view)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
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
                setContentView(R.layout.activity_log_in);
                Toast.makeText(getApplicationContext(), "Benutzerdaten sind nicht korrekt!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
