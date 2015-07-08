package com.brik.android.chat;

import com.brik.android.chat.utils.MapUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Created by wangfengchen on 15/7/8.
 */
public class ChatEventObservable extends Observable {

    private static ChatEventObservable instance = new ChatEventObservable();

    public static ChatEventObservable getInstance() {
        return instance;
    }

    private Map<Class, List<Listener>> listeners;

    public ChatEventObservable() {
        listeners = new HashMap<>();
    }

    public void register(Class cls, Listener listener) {
        List<Listener> ls = listeners.get(cls);
        if(ls==null) {
            ls = new ArrayList<>();
            ls.add(listener);
            listeners.put(cls, ls);
        } else {
            ls.add(listener);
        }
    }

    public void unregister(Class cls, Listener listener) {
        List<Listener> ls = listeners.get(cls);
        if(ls!=null) {
            ls.remove(listener);
        }
    }

    public void connectSuccessChanged(Class t) {
        dataChanged(t, true, null, null);
    }

    public void connectFailChanged(Class t, Throwable throwable) {
        dataChanged(t, false, null, throwable);
    }

    private void dataChanged(Class t, boolean b, BaseEvent data, Throwable throwable) {
        setChanged();
        notifyObservers(MapUtils.create(
                "type", t,
                "b", b,
                "data", data,
                "error", throwable
        ));
    }

}
