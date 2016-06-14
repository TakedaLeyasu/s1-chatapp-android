package de.kabelskevalley.doegel.stroke.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        try
        {
            String message = name + thumbnail;
            return getMD5(message);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
