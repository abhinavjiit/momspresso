package com.mycity4kids;

public class MessageEvent {

    private Object object[];

    public MessageEvent(Object[] object) {
        this.object = object;
    }

    public Object[] getObject() {
        return object;
    }
}
