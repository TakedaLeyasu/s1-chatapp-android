package de.kabelskevalley.doegel.stroke.network;


import android.view.View;
import android.widget.EditText;

import java.util.List;

import de.kabelskevalley.doegel.stroke.R;
import de.kabelskevalley.doegel.stroke.entities.Channel;
import de.kabelskevalley.doegel.stroke.entities.User;

public interface OnHttpResultListner {

    void onResult(List<Channel> channels);

    void onResult(User user);

    void onError(Exception e);
}
