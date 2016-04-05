package de.kabelskevalley.doegel.stroke;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewChannelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_channel);
    }

    public void create_Item (View view)
    {
        EditText editText = (EditText)findViewById(R.id.editText5);
        EditText editText2 = (EditText)findViewById(R.id.editText6);

        String content = editText.getText().toString();
        String details = editText2.getText().toString();

        if(content.length()==0)
        {
            content = "unnamed Channel";
        }
        ChannelContent.addItem(content,details);
        Intent intent = new Intent(this, ChannelListActivity.class);
        startActivity(intent);

    }
}
