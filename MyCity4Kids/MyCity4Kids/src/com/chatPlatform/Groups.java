package com.chatPlatform;

import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by anshul on 16/12/15.
 */
public class Groups {
    private static final String VIEW_NAME = "groupListView";
    private static final String DOC_TYPE = "GROUP";


    public static Query getQuery(Database database) {
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
            view.setMap(map, "2");
        }

        Query query = view.createQuery();
        query.setDescending(true);
        return query;
    }
}
