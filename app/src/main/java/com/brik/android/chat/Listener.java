package com.brik.android.chat;

/**
 * Created by wangfengchen on 15/6/26.
 */
public interface Listener<T extends BaseEvent> {

    void onSuccess(T data);

    void onFail(Throwable throwable);
}
