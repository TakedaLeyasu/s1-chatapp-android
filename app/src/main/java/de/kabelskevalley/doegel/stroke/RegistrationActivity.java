package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.LogInData;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.HttpRegistrationTask;
import de.kabelskevalley.doegel.stroke.network.OnHttpResultListener;


public class RegistrationActivity extends AppCompatActivity implements OnHttpResultListener<User> {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button registrationButton = (Button)findViewById(R.id.registration_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_profile(v);
            }
        });
        imageView = (ImageView)findViewById(R.id.imageView_registerPicture);
        EditText editText = (EditText)findViewById(R.id.editText_thumbnail);
        editText.addTextChangedListener(textWatcher);
    }

    public void create_profile(View view)
    {
        String name = ((EditText)findViewById(R.id.editText_NewName)).getText().toString();
        String thumbnail =  ((EditText)findViewById(R.id.editText_thumbnail)).getText().toString();
        String password1 = ((EditText)findViewById(R.id.editText_p1)).getText().toString();
        String password2 = ((EditText)findViewById(R.id.editText_p2)).getText().toString();

        if(check_registrationData(password1,password2,name)) {
            LogInData logIn_data;
            if(thumbnail.isEmpty())
                logIn_data = new LogInData(name, password1);
            else
                logIn_data = new LogInData(name, password1,thumbnail);



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


        if (password1.equals(password2) && name!=null && password1.length()>3 && !password1.contains(" ") && !name.contains(" ")) {
            return true;
        }
        Toast.makeText(getApplicationContext(),"Passwörter stimmen nicht überein, sind zu kurz, haben Leerzeichen enthalten oder der Name ist nicht vorhanden oooooder enthält auch Leerzeichen. \n\n Such dir was aus;)",Toast.LENGTH_LONG).show();
        return  false;

    }

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
