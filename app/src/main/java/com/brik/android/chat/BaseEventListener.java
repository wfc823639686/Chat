package com.brik.android.chat;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wangfengchen on 15/7/8.
 */
public abstract class BaseEventListener<T extends BaseEvent> implements Listener<T>, Observer {

    @Override
    public void update(Observable observable, Object o) {
        Map map = (Map) o;
        Class c = (Class) map.get("type");
        if(c == this.getClass()) {
            Boolean b = (Boolean) map.get("b");
            if(b) {
                T event = (T) map.get("data");
                onSuccess(event);
            } else {
                Throwable throwable = (Throwable) map.get("error");
                onFail(throwable);
            }

        }

    }
}