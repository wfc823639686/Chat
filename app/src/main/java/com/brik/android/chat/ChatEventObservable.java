package com.brik.android.chat;

import com.brik.android.chat.service.ConnectListener;
import com.brik.android.chat.service.LoginListener;
import com.brik.android.chat.utils.MapUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wangfengchen on 15/7/8.
 */
public class ChatEventObservable extends Observable {

    private static ChatEventObservable instance = new ChatEventObservable();

    public static ChatEventObservable getInstance() {
        return instance;
    }

//    private Map<Class, List<Listener>> listeners;
//
//    public ChatEventObservable() {
//        listeners = new HashMap<>();
//    }

    public void register(Listener listener) {
//        List<Listener> ls = listeners.get(listener.getClass());
//        if(ls==null) {
//            ls = new ArrayList<>();
//            ls.add(listener);
//            listeners.put(listener.getClass(), ls);
//        } else {
//            ls.add(listener);
//        }
        addObserver(listener);
    }

    public void unregister(Listener listener) {
//        List<Listener> ls = listeners.get(listener.getClass());
//        if(ls!=null) {
//            ls.remove(listener);
//        }
        deleteObserver(listener);
    }

    public void successChanged(Class c) {
        dataChanged(c, true, null, null);
    }

    public void successChanged(Class c, BaseEvent event) {
        dataChanged(c, true, event, null);
    }

    public void failChanged(Class c, Throwable throwable) {
        dataChanged(c, false, null, throwable);
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
