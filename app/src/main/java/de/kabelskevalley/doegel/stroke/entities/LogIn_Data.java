package de.kabelskevalley.doegel.stroke.entities;

/**
 * Created by livestream on 12.04.2016.
 */
public class LogIn_Data {
    final String username;
    final String password;
    final String name;

    public LogIn_Data(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.name = username;
    }

    public String getUsername(){return username;}
    public String getName(){return name;}
    public String getPassword(){return password;}
}
