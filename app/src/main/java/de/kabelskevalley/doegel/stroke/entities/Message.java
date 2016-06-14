package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.kabelskevalley.doegel.stroke.database.StorageHelper;

@JsonIgnoreProperties(value={"_id", "__v"})
public class Message {

    public enum Type {
        Unknown,
        Chat,
        Info
    }

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("user")
    private User sender = null;

    @JsonProperty("numUsers")
    private int userCount = 0;

    @JsonIgnore
    private Type type = Type.Unknown;

    @JsonIgnore
    private String time = null;

    @JsonIgnore
    private Boolean checked = false;

    public Message()
    {
        this.type = Type.Chat;
        this.time = this.getFreshTimestamp();
    }

    public Message(Type type, User sender, String message)
    {
        this.type = type;
        this.sender = sender;
        this.message = message;
        this.time = this.getFreshTimestamp();
    }

    public String getMessage() { return message; }
    public User getSender() { return sender; }
    public String getTime() { return time; }
    public int getUserCount() { return userCount; }
    public Type getType() { return type; }
    public Boolean getChecked(){return checked;};

    public void setChecked(Boolean checked) {this.checked = checked;}

    public boolean isMyMessage()
    {
        try
        {
            User user = (User) StorageHelper.getInstance().getObject("user", User.class);
            return this.getSender().getToken().equals(user.getToken());
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private String getFreshTimestamp()
    {
        SimpleDateFormat s = new SimpleDateFormat("hh:mm");
        return s.format(new Date());
    }
}
