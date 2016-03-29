package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

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
        //add stuff here
        return true;
    }

    public void register(View view)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        //add stuff here
    }
}
