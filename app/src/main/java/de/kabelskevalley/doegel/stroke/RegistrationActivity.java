package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.LogIn_Data;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.HttpRegistrationTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;


public class RegistrationActivity extends AppCompatActivity implements OnHttpResultListener<User> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button registration_button = (Button)findViewById(R.id.registration_button);
        registration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_profile(v);
            }
        });
    }

    public void create_profile(View view)
    {
        String name = ((EditText)findViewById(R.id.editText_NewName)).getText().toString();
        String password1 = ((EditText)findViewById(R.id.editText_p1)).getText().toString();
        String password2 = ((EditText)findViewById(R.id.editText_p2)).getText().toString();

        if(check_registrationData(password1,password2,name)) {
            LogIn_Data logIn_data = new LogIn_Data(name, password1);
            new HttpRegistrationTask(this, logIn_data).execute();
        }


    }

    @Override
    public void onResult(User user) {
        StorageHelper.getInstance()
                .storeObject("user", user);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Lade neue Activity
                Intent intent = new Intent(getBaseContext(), ChannelListActivity.class);
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

    public boolean check_registrationData(String password1, String password2, String name) {


        if (password1.equals(password2)&&name!=null&&password1.length()>3) {

            return true;
        }
        Toast.makeText(getApplicationContext(),"Passwörter stimmen nicht überein, sind zu kurz oder der Name ist nicht vorhanden. \n\n Such dir was aus;)",Toast.LENGTH_LONG).show();
        return  false;

    }



}
