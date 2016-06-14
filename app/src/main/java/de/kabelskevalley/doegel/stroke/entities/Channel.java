package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={"_id", "__v"})
public class Channel {
    protected String id;
    private String name;
    private String thumbnail;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public Channel(String name, String thumbnail)
    {
        this.name = name;
        this.thumbnail = thumbnail;
        this.id = generate_id();
    }
    public Channel () {}

    private String generate_id()
    {
        return name + thumbnail;
    }
}
