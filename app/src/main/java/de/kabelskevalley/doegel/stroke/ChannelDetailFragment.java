package de.kabelskevalley.doegel.stroke;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;
import de.kabelskevalley.doegel.stroke.entities.Message;
import de.kabelskevalley.doegel.stroke.entities.User;
import de.kabelskevalley.doegel.stroke.network.SocketHelper;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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
    private List<String> typingPersons = new ArrayList<>();
    private boolean typing = false;
    private MessageAdapterItem myAdapter;
    private ListView listView;
    private HashMap<String,Integer> colorMap;

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

    /**
     * This is the channel we want to join for this fragment.
     */
    private String mChannel;

    private View.OnClickListener onSendClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText message = ((EditText) mRootView.findViewById(R.id.message_text));
            mSocket.emit("new message", message.getText());
            message.setText("");
        }
    };
    private View.OnLongClickListener onSendLongClicked = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            //   image_choser();
            return false;
        }
    };

    private void deleteOldTypingMessage() {
        try {
            if (message_list.get((message_list.size() - 1)).getType() == Message.Type.Info
                    && message_list.get((message_list.size() - 1)).getMessage().contains("schreib"))
                message_list.remove(message_list.size() - 1);
        } catch (Exception e) {
        }
    }

    private void generateNewTypingMessage() {
        Message message_temp;
        switch (typingPersons.size()) {
            case 0:
                message_temp = null;
                break;

            case 1:
                message_temp = new Message(Message.Type.Info, null, typingPersons.get(0) +" schreibt...");
                break;

            default:
                message_temp = new Message(Message.Type.Info, null, typingPersons.get(0) +" und " + String.valueOf(typingPersons.size() - 1) + " weitere Personen schreiben...");
                break;
        }
        if (message_temp != null)
            message_list.add(message_temp);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            EditText message_text = ((EditText) mRootView.findViewById(R.id.message_text));
            if (typing) {
                if (message_text.getText().toString().equals("")) {
                    mSocket.emit("stop typing");
                    typing = false;
                }
            } else {
                if (!message_text.getText().toString().equals("")) {
                    mSocket.emit("typing");
                    typing = true;
                }
            }
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Message message = ChannelDetailFragment.this.parseMessage(args[0].toString());

                    deleteOldTypingMessage();
                    message_list.add(message);
                    myAdapter.notifyDataSetChanged();
                    listView.setSelection(myAdapter.getCount() - 1);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Message info = ChannelDetailFragment.this.parseMessage(args[0].toString());
                    typingPersons.add(info.getSender().getUsername()); //Schreibende Person wird gespeichert
                    deleteOldTypingMessage();
                    generateNewTypingMessage();
                    myAdapter.notifyDataSetChanged();
                    listView.setSelection(myAdapter.getCount() - 1);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Message info = ChannelDetailFragment.this.parseMessage(args[0].toString());
                    typingPersons.remove(info.getSender().getUsername());
                    Log.i("Liste",typingPersons.toString());
                    deleteOldTypingMessage();
                    generateNewTypingMessage();
                    myAdapter.notifyDataSetChanged();
                    listView.setSelection(myAdapter.getCount() - 1);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override

                    public void run() {
                        Message info = ChannelDetailFragment.this.parseMessage(args[0].toString());
                        Message message_temp = new Message(Message.Type.Info, info.getSender(), "joined :)");
                        deleteOldTypingMessage();
                        message_list.add(message_temp);
                        generateNewTypingMessage();
                        myAdapter.notifyDataSetChanged();
                        listView.setSelection(myAdapter.getCount() - 1);
                    }
                });
            } catch (Exception e) {
            }
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message info = ChannelDetailFragment.this.parseMessage(args[0].toString());
                        Message message_temp = new Message(Message.Type.Info, info.getSender(), "left :(");
                        deleteOldTypingMessage();
                        message_list.add(message_temp);
                        generateNewTypingMessage();
                        myAdapter.notifyDataSetChanged();
                        listView.setSelection(myAdapter.getCount() - 1);
                    }
                });
            } catch (Exception e) {
            }
        }
    };

    /**
     * Parses the message into the message object
     *
     * @param value
     * @return
     */
    private Message parseMessage(String value) {
        try {
            return (new ObjectMapper()).readValue(value, Message.class);
        } catch (Exception e) {
            return null;
        }
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

        if (getArguments().containsKey(ARG_ITEM_NAME)) {
            getActivity().setTitle(getArguments().getString(ARG_ITEM_NAME));
        }

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mChannel = getArguments().getString(ARG_ITEM_ID);
        }

        mSocket = SocketHelper.getSocket();
        colorMap = new HashMap<>();
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

        listView = (ListView) mRootView.findViewById(R.id.listView);
        myAdapter = new MessageAdapterItem(getActivity(), 0, message_list);
        listView.setAdapter(myAdapter);
        configurate_Menu();

        mRootView.findViewById(R.id.message_send).setOnClickListener(onSendClicked);
        mRootView.findViewById(R.id.message_send).setOnLongClickListener(onSendLongClicked);

        EditText message_text = (EditText) mRootView.findViewById(R.id.message_text);
        message_text.addTextChangedListener(textWatcher);


        mSocket.on("new message", onNewMessage);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);

        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);

        // connect
        mSocket.connect();

        // authorize the current user to chat
        User user = (User) StorageHelper.getInstance().getObject("user", User.class);
        mSocket.emit("add user", user.getToken());

        // join the selected channel
        mSocket.emit("join", mChannel);
    }

    @Override
    public void onPause() {
        super.onPause();

        mRootView.findViewById(R.id.message_send).setOnClickListener(null);

        // leave the channel
        mSocket.emit("leave", mChannel);

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);

        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
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
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            Message message = data.get(position);

            switch (message.getType()) {
                case Unknown:
                    break;

                case Chat:
                    if (message.isMyMessage()) {
                        convertView = inflater.inflate(R.layout.my_message_view, parent, false);
                    } else {
                        convertView = inflater.inflate(R.layout.other_message_view, parent, false);

                        TextView sender_view = (TextView) convertView.findViewById(R.id.message_sender_item);
                        sender_view.setText(message.getSender().getName());

                        Integer color;
                        if(!colorMap.containsKey(data.get(position).getSender().getUsername().toString()))
                        {
                            Random random = new Random();
                            int r = random.nextInt(256);
                            int g = random.nextInt(256);
                            int b = random.nextInt(256);
                            color = Color.rgb(r, g, b);
                            colorMap.put(data.get(position).getSender().getUsername().toString(),color);
                        }
                        else
                            color = colorMap.get(data.get(position).getSender().getUsername().toString());

                        Drawable mDrawable = getContext().getDrawable(R.drawable.bubble_other);
                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(color, PorterDuff.Mode.OVERLAY));
                        RelativeLayout other_message_layout = (RelativeLayout)convertView.findViewById(R.id.other_message_layout);
                        other_message_layout.setBackground(mDrawable);
                    }

                    TextView text_view = (TextView) convertView.findViewById(R.id.message_text_item);
                    TextView time_view = (TextView) convertView.findViewById(R.id.message_time_item);
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.message_image_item);

                    text_view.setText(message.getMessage());
                    time_view.setText(message.getTime());

                    if (message.getSender().getThumbnail() != null)
                        ImageLoader.getInstance().displayImage(message.getSender().getThumbnail(), imageView);
                    else
                        imageView.setImageResource(R.drawable.profilbild);
                    break;

                case Info:
                    convertView = inflater.inflate(R.layout.state_view, parent, false);

                    TextView state_view = (TextView) convertView.findViewById(R.id.state_view);
                    if(message.getSender()!=null)
                        state_view.setText(message.getSender().getName() + " " + message.getMessage());
                    else
                        state_view.setText(message.getMessage());
                    break;
            }
            if (message.getChecked())
                convertView.setBackgroundColor(Color.argb(50, 0, 0, 0));
            else
                convertView.setBackgroundColor(Color.argb(0, 255, 255, 255));

            return convertView;
        }
    }

    private void configurate_Menu() {

        final List<Integer> itemPositions = new ArrayList<>();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {

                //Ausgewählte Items werden in Liste aufgenommen und markiert
                if (checked && message_list.get(position).getType() != Message.Type.Info) {
                    message_list.get(position).setChecked(true);
                    for (int i = 0; i <= itemPositions.size(); i++) {
                        if(i == itemPositions.size())
                        {
                            itemPositions.add(position);
                            break;
                        }

                        if (itemPositions.get(i) > position) {
                            itemPositions.add(i,position);
                            break;
                        }
                    }
                } else {
                    message_list.get(position).setChecked(false);
                    itemPositions.remove((Object) position);
                }

                myAdapter.notifyDataSetChanged();
                listView.setSelection(myAdapter.getCount() - 1);


                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.delete:
                        delete_Messages(itemPositions);
                        itemPositions.clear();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.copy:
                        copy_Messages(itemPositions);
                        int position;
                        for (int i = 0; i < itemPositions.size(); i++) {
                            position = itemPositions.get(i);
                            message_list.get(position).setChecked(false);
                        }
                        itemPositions.clear();
                        mode.finish(); // Action picked, so close the CAB
                        return true;

                    default:
                        return false;
                }
            }


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.contextual_action_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                int position;
                for (int i = 0; i < itemPositions.size(); i++) {
                    position = itemPositions.get(i);
                    message_list.get(position).setChecked(false);
                }
                itemPositions.clear();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
    }

    private void delete_Messages(List<Integer> itemPositions) {
        int position;
        for (int i = itemPositions.size() - 1; i >= 0; i--) {
            position = itemPositions.get(i);
            message_list.remove(position);
        }
        myAdapter.notifyDataSetChanged();
        listView.setSelection(myAdapter.getCount() - 1);
    }

    private void copy_Messages(List<Integer> itemPositions) {
        int position;
        String text = "";
        for (int i = 0; i < itemPositions.size(); i++) {
            position = itemPositions.get(i);
            text += message_list.get(position).getMessage().toString();
            if (i < itemPositions.size() - 1)
                text += " | ";
        }

        if (!text.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        }

    }

}
