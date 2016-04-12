package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.LogIn_Data;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListner;

public class LoginActivity extends AppCompatActivity implements OnHttpResultListner{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }
    private User user;

    public void logIn(View view)
    {
        EditText name = (EditText)findViewById(R.id.editText_N);
        EditText password = (EditText)findViewById(R.id.editText_P);

        if(user_is_known(name.getText().toString(),password.getText().toString())==true)    //Wenn User bekannt ist
        {
            Intent intent = new Intent(this, ChannelListActivity.class);                            //Lade neue Activity
            startActivity(intent);
        }
        else
        {
            //add stuff here
        }


    }

    private boolean user_is_known (String name, String password)    //Check ob User vorhanden ist
    {
        LogIn_Data logIn_data = new LogIn_Data(name, password);

        new HttpLogInTask(this,logIn_data).execute();

        if(user != null)
        {
            return true;
        }
        return false;
        

    }


    public void register(View view)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        //add stuff here
    }
    @Override
    public void onResult(List<Channel> channels) {

    }

    @Override
    public void onResult(User user) {
        try {
            Log.i("User: ", user.getName() +"  "+user.getToken());
            this.user = user;
        }
        catch (Exception e){
            this.user = null;
        }
    }

    @Override
    public void onError(Exception e) {
        Log.e("MainActivity", e.getMessage(), e);
    }
}
