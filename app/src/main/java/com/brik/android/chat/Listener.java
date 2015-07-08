package com.brik.android.chat;

import java.util.Observer;

/**
 * Created by wangfengchen on 15/6/26.
 */
public interface Listener<T extends BaseEvent> extends Observer{

    void onSuccess(T data);

    void onFail(Throwable throwable);
}
