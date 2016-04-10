package de.kabelskevalley.doegel.stroke;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Channel detail screen.
 * This fragment is either contained in a {@link ChannelListActivity}
 * in two-pane mode (on tablets) or a {@link ChannelDetailActivity}
 * on handsets.
 */
public class ChannelDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_NAME = "item_name";

    /**
     * The root view of the current fragment, we keep a reference to find
     * subviews.
     */
    private View mRootView;

    /**
     * The socket which we connect to. It enables us to listen to all
     * events our server broadcasts.
     */
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://chat.kabelskevalley.com:3000/");
        } catch (URISyntaxException e) {}
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChannelDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {


                if (appBarLayout != null) {
                    if(getArguments().containsKey(ARG_ITEM_NAME))
                        appBarLayout.setTitle(getArguments().getCharSequence(ARG_ITEM_NAME));   //AppBar zeigt Channel Namen an
                    else
                        appBarLayout.setTitle(getArguments().getCharSequence("Stroke! chat "));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.channel_detail, container, false);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mRootView.findViewById(R.id.message_send).setOnClickListener(onSendClicked);

        mSocket.on("chat message", onNewMessage);
        mSocket.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        mRootView.findViewById(R.id.message_send).setOnClickListener(null);

        mSocket.disconnect();
        mSocket.off("chat message", onNewMessage);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = args[0].toString();
                    //((TextView) mRootView.findViewById(R.id.channel_detail)).setText(message);
                    message_list.add(message);
                    show_messages();
                }
            });
        }
    };

    private View.OnClickListener onSendClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText message = ((EditText) mRootView.findViewById(R.id.message_text));
            mSocket.emit("chat message", message.getText());


            message.setText("");
        }
    };

    private List<String> message_list = new ArrayList<>();

    public void show_messages()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.message_view_item,
                R.id.message_item,
                message_list);

        ListView listView = (ListView)mRootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }


}
