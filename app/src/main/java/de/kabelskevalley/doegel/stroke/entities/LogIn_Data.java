package de.kabelskevalley.doegel.stroke.entities;

/**
 * Created by livestream on 12.04.2016.
 */
public class LogIn_Data {
    final String username;
    final String password;

    public LogIn_Data(String name, String passwort)
    {
        this.password = passwort;
        this.username = name;
    }

    public String getUsername(){return username;}
    public String getPassword(){return password;}
}
