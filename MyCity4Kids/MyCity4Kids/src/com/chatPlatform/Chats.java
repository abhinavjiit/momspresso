package com.chatPlatform;

import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anshul on 16/12/15.
 */
 public class Chats {
    private static final String VIEW_NAME = "chatListView";
    private static final String DOC_TYPE = "CHAT";

    public static Query getQuery(Database database,String channelId) {
        com.couchbase.lite.View view = database.getView(VIEW_NAME);
        if (view.getMap() == null) {
            Mapper map = new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    if(DOC_TYPE.equals(document.get("type"))){
                        java.util.List<Object> keys = new ArrayList<Object>();
                        keys.add(document.get("channelId"));
                        keys.add(document.get("createdAt"));
                        emitter.emit(keys, document);

                    }

                }
            };
            view.setMap(map, "3");
        }

        Query query = view.createQuery();

        java.util.List<Object> startKeys = new ArrayList<Object>();
        startKeys.add(channelId);


        java.util.List<Object> endKeys = new ArrayList<Object>();
        endKeys.add(channelId);
        endKeys.add(new HashMap<String, Object>());

        query.setStartKey(startKeys);
        query.setEndKey(endKeys);

        return query;
    }
}

