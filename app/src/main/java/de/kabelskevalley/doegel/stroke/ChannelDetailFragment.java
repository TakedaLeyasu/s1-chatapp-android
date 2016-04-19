package de.kabelskevalley.doegel.stroke;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Message;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.SocketHelper;

/**
 * A fragment representing a single Channel detail screen.
 * This fragment is either contained in a {@link ChannelListActivity}
 * in two-pane mode (on tablets) or a {@link ChannelDetailActivity}
 * on handsets.
 */
public class ChannelDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_NAME = "item_name";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private List<Message> message_list = new ArrayList<>();
    private boolean sended = false;

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

    private View.OnClickListener onSendClicked = new View.OnClickListener() {    //public damit Fab darauf zugreifen kann
        @Override
        public void onClick(View v) {
            EditText message = ((EditText) mRootView.findViewById(R.id.message_text));
            mSocket.emit("new message", message.getText());

            sended = true;
            message.setText("");
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String message = args[0].toString();
                    Calendar cal = new GregorianCalendar();
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int min = cal.get(Calendar.MINUTE);
                    String time_temp = String.valueOf(hour) + " : " + String.valueOf(min);

                    Message message_temp;

                    if (sended) {
                        message_temp = new Message(message, null, time_temp);
                        sended = false;
                    } else
                        message_temp = new Message(message, "anonym", time_temp);

                    message_list.add(message_temp);
                    show_messages();
                }
            });
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChannelDetailFragment() {
        mSocket = SocketHelper.getSocket();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_NAME)) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.detail_toolbar);
            toolbar.setTitle(getArguments().getCharSequence(ARG_ITEM_NAME));
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

        mSocket.on("new message", onNewMessage);
        //mSocket.on("user joined", onNewMessage);
        //mSocket.on("user left", onNewMessage);
        //mSocket.on("typing", onNewMessage);
        //mSocket.on("stop typing", onNewMessage);
        mSocket.connect();

        User user = (User) StorageHelper.getInstance().getObject("user", User.class);
        mSocket.emit("add user", user.getName());

        //mSocket.emit("typing");
        //mSocket.emit("stop typing");
    }

    @Override
    public void onPause() {
        super.onPause();

        mRootView.findViewById(R.id.message_send).setOnClickListener(null);

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        //mSocket.off("user joined", onNewMessage);
        //mSocket.off("user left", onNewMessage);
        //mSocket.off("typing", onNewMessage);
        //mSocket.off("stop typing", onNewMessage);
    }

    public void show_messages() {

        MessageAdapterItem myAdapter = new MessageAdapterItem(getActivity(), R.layout.message_view_item, message_list);

        ListView listView = (ListView) mRootView.findViewById(R.id.listView);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public class MessageAdapterItem extends ArrayAdapter<Message> {

        Context mContext;
        int layoutResourceId;
        List<Message> data = null;


        public MessageAdapterItem(Context mContext, int layoutResourceId, List<Message> data) {

            super(mContext, layoutResourceId, data);

            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }




        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }


            // get the TextView and then set the text (item name) and tag (item ID) values

            TextView text_view = (TextView) convertView.findViewById(R.id.message_text_item);
            TextView time_view = (TextView) convertView.findViewById(R.id.message_time_item);
            TextView sender_view = (TextView) convertView.findViewById(R.id.message_sender_item);
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.message_layout);


            if (data.get(position).getSender() == null) {
                layout.setBackgroundColor(Color.YELLOW);
            } else {
                sender_view.setText(data.get(position).getSender());
                layout.setGravity(Gravity.END);
                text_view.setGravity(Gravity.END);
                sender_view.setGravity(Gravity.END);
                time_view.setGravity(Gravity.START);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                layout.setLayoutParams(params);

            }

            text_view.setText(data.get(position).getMessage());
            time_view.setText(data.get(position).getTime());

            return convertView;

        }

    }


}
