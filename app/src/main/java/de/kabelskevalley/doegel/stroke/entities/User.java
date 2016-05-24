package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={"_id", "__v"})
public class User {
    private String token;
    private String name;
    private String username;
    private String thumbnail;

    public String getName(){return name;}
    public String getUsername(){return username;}
    public String getToken(){return token;}
    public String getThumbnail(){return thumbnail;}
}
