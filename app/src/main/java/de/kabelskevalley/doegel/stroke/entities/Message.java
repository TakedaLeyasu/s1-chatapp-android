package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;

/**
 * Created by Hartmut on 13.04.2016.
 */
public class Message {

    public enum Type {
        Unknown,
        Chat,
        Info
    }

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("username")
    private String sender = null;

    @JsonProperty("numUsers")
    private int userCount = 0;

    @JsonIgnore
    private Type type = Type.Unknown;

    @JsonIgnore
    private String time = null;

    public Message()
    {
        this.type = Type.Chat;
        this.time = this.getFreshTimestamp();
    }

    public Message(Type type, String sender, String message)
    {
        this.type = type;
        this.sender = sender;
        this.message = message;
        this.time = this.getFreshTimestamp();
    }

    public String getMessage() { return message; }
    public String getSender() { return sender; }
    public String getTime() { return time; }
    public int getUserCount() { return userCount; }
    public Type getType() { return type; }

    public boolean isMyMessage()
    {
        User user = (User) StorageHelper.getInstance().getObject("user", User.class);
        return this.getSender().equals(user.getName());
    }

    private String getFreshTimestamp()
    {
        Calendar cal = new GregorianCalendar();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        return String.valueOf(hour) + " : " + String.valueOf(min);
    }
}
