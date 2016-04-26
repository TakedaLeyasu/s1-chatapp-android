package de.kabelskevalley.doegel.stroke.network;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by felixrudat on 19.04.16.
 */
public class SocketHelper {

    final static String URL = "http://chat.kabelskevalley.com:3000/";

    private static Socket mSocket;

    public static Socket getSocket()
    {
        if (mSocket == null)
        {
            try {
                mSocket = IO.socket(URL);
            } catch (URISyntaxException e) {
            }
        }

        return mSocket;
    }

    private SocketHelper()
    {
    }
}
