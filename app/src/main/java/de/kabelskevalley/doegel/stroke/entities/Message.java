package de.kabelskevalley.doegel.stroke.entities;

/**
 * Created by Hartmut on 13.04.2016.
 */
public class Message {
    private final String message;
    private final String sender;
    private final String time;

    public Message(String message, String sender, String time)
    {
        this.message = message;
        this.sender = sender;
        this.time = time;
    }
    public String getMessage(){return message;}
    public String getSender(){return sender;}
    public String getTime(){return time;}

}
