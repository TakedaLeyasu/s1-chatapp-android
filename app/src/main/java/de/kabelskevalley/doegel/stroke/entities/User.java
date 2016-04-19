package de.kabelskevalley.doegel.stroke.entities;

/**
 * Created by livestream on 12.04.2016.
 */
public class User {
    final String token;
    final String name;
    final String thumbnail;

    public User(String token, String name)
    {
        this.token = token;
        this.name = name;
        this.thumbnail=null;
    }

    public String getName(){return name;}
    public String getToken(){return token;}
}
