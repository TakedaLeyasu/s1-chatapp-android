package de.kabelskevalley.doegel.stroke;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Adapterview.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cedric on 04.04.2016.
 */
public class CannelListviewActivity<T> extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    List valueList = new ArrayList<String>();
    for(
    int i = 0;
    i<10;i++)

    {
        valueList.add(„value“ + i);
    }

    ListAdapter adapter = new ArrayAdapter<T>(getApplicationContext(), android.R.layout.simple_list_item_1, valueList);
    final ListView lv = (ListView) findViewById(R.id.listView1);

    lv.setAdapter(adapter);
    lv.setOnItemClickListener(new

    OnItemClickListener() {
        @Override
        public void onItemClick (AdapterView < ? > arg0, View arg1,int arg2, long arg3)
        {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() +“.channelListviewAnzeigeActivity“)
            ;
            intent.putExtra(„selected“, lv.getAdapter().getItem(arg2).toString());
            startActivity(intent);
        }
    }

    );

    setContentView(R.layout.anzeige);

    Intent intent = getIntent();

    ((TextView)(

    findViewById(R.id.textView1)

    )).

    setText(„Es wurde Kanal„+intent.getStringExtra(„selected“)

    + “gewählt!“);
}
