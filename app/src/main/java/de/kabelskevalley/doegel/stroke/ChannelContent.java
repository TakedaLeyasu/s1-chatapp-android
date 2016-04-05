package de.kabelskevalley.doegel.stroke;

/**
 * Created by Hartmut on 02.04.2016.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChannelContent {

    /**
     * An array of sample (channel) items.
     */
    public static final List<ChannelItem> ITEMS = new ArrayList<ChannelItem>();




    static {
        // Add standard channel/s
        addItem("Global Channel");
    }

    public static void addItem(String content)
    {
        ChannelItem item = createChannelItem(content);
        ITEMS.add(item);
    }
    public static void addItem(String content,String details)
    {
        ChannelItem item = createChannelItem(content,details);
        ITEMS.add(item);
    }

    public static void deleteItem(int id)
    {
        ITEMS.remove(id);
        change_id();
    }

    private static void change_id()
    {
        for(int i = 0; i<ITEMS.size();i++)
        {
            ChannelItem temp = ITEMS.get(i);
            temp.id = i;
            ITEMS.set(i,temp);
        }
    }
    private static ChannelItem createChannelItem(String content) {
        return new ChannelItem(content);
    }
    private static ChannelItem createChannelItem(String content,String details) {
        return new ChannelItem(content,details);
    }


    public static class ChannelItem {
        public final String content;
        public final String details;
        public int id;

        public ChannelItem(String content, String details) {
            this.content = content;
            this.details = details;
            this.id = ITEMS.size();
        }
        public ChannelItem(String content) {
            this.content = content;
            this.details = "no details available";
            this.id = ITEMS.size();
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
