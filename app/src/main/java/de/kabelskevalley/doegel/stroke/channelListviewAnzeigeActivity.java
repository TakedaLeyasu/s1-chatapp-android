package de.kabelskevalley.doegel.stroke;

/**
 * Created by Cedric on 04.04.2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class channelListviewAnzeigeActivity extends{
}
@Override
public void onCreate(Bundle saved InstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.anzeige);

    Intent intent = getIntetn();
    ((TextView)(findViewById(R.id.textView1))).setText("Es wurde"+intent.getStringExtra("selected")+ "gewählt!");
}