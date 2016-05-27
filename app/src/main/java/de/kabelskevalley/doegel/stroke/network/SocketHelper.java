package de.kabelskevalley.doegel.stroke.network;

import de.kabelskevalley.doegel.stroke.Constants;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by felixrudat on 19.04.16.
 */
public class SocketHelper {

    private static Socket mSocket;

    public static Socket getSocket()
    {
        if (mSocket == null)
        {
            try {
                mSocket = IO.socket(Constants.BASE_URL);
            } catch (URISyntaxException e) {
            }
        }

        return mSocket;
    }

    private SocketHelper()
    {
    }
}
