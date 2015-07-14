package com.brik.android.chat.service.event;

import org.jivesoftware.smack.RosterEntry;

import java.util.Collection;

/**
 * Created by wangfengchen on 15/7/13.
 */
public class RosterEvent extends BaseEvent{
    public Collection<RosterEntry> entries;
}
