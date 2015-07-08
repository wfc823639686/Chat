package com.brik.android.chat;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wangfengchen on 15/7/8.
 */
public abstract class BaseEventListener<T extends BaseEvent> implements Listener<T> {

    @Override
    public void update(Observable observable, Object o) {
        Map map = (Map) o;
        Class c = (Class) map.get("type");
        Class c1 = getClass().getSuperclass();
        if(c1.getName().equals(c.getName())) {
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