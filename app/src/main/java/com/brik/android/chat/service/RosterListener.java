package com.brik.android.chat.service;

import com.brik.android.chat.Listener;

import org.jivesoftware.smack.RosterEntry;

import java.util.Collection;

/**
 * Created by wangfengchen on 15/6/26.
 */
public interface RosterListener extends Listener{

    void onSuccess(Collection<RosterEntry> collection);
}
