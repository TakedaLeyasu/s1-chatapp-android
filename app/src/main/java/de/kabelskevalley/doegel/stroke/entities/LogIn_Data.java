package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={"_id", "__v"})
public class LogIn_Data {
    private String username;
    private String password;
    private String name;
    private String token;

    public LogIn_Data(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.name = username;
    }

    public LogIn_Data(String token)
    {
        this.token = token;
    }

    public String getUsername(){return username;}
    public String getName(){return name;}
    public String getPassword(){return password;}
    public String getToken(){return token;}

    public boolean hasToken()
    {
        return token != null;
    }
}
