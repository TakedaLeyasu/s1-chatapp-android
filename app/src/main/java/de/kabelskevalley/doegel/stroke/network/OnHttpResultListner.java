package de.kabelskevalley.doegel.stroke.network;


import android.view.View;
import android.widget.EditText;

import java.util.List;

import de.kabelskevalley.doegel.stroke.R;
import de.kabelskevalley.doegel.stroke.entities.Channel;

public interface OnHttpResultListner {

    void onResult(List<Channel> channels);

    void onError(Exception e);
}
