package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={"_id", "__v"})
public class LogIn_Data {
    private String username;
    private String password;
    private String name;
    private String token;
    private String thumbnail;

    public LogIn_Data(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.name = username;
        this.thumbnail = null;
    }
    public LogIn_Data(String username, String password,String thumbnail)
    {
        this.username = username;
        this.password = password;
        this.name = username;
        this.thumbnail = thumbnail;
    }

    public LogIn_Data(String token)
    {
        this.token = token;
    }

    public String getUsername(){return username;}
    public String getName(){return name;}
    public String getPassword(){return password;}
    public String getToken(){return token;}
    public String getThumbnail(){return thumbnail;}

    public boolean hasToken()
    {
        return token != null;
    }
}
