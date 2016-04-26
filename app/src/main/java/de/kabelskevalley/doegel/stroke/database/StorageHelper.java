package de.kabelskevalley.doegel.stroke.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by felixrudat on 19.04.16.
 */
public class StorageHelper {

    private static StorageHelper mInstance;

    private String mName;

    private Context mContext;

    private ObjectMapper mMapper;

    private StorageHelper(Context context, String name)
    {
        this.mContext = context;
        this.mName = name;

        this.mMapper = new ObjectMapper();
    }

    public static void Init(Context context, String name)
    {
        mInstance = new StorageHelper(context, name);
    }

    public static StorageHelper getInstance()
    {
        return mInstance;
    }

    public boolean storeObject(String key, Object object)
    {
        try {
            String serializedUser = this.mMapper.writeValueAsString(object);

            getSharedPreferences().edit()
                    .putString(key, serializedUser)
                    .apply();

            return true;
        } catch (Exception e) {
            // nothing to do here
        }

        return false;
    }

    public Object getObject(String key, Class clazz)
    {
        String serializedUser = getSharedPreferences().getString(key, null);

        try {
            return this.mMapper.readValue(serializedUser, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    private SharedPreferences getSharedPreferences()
    {
        return mContext.getSharedPreferences(this.mName, Context.MODE_PRIVATE);
    }
}

