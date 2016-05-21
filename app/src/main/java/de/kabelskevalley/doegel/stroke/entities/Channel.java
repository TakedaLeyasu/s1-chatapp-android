package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={"_id", "__v"})
public class Channel {
    private String id;
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
}
