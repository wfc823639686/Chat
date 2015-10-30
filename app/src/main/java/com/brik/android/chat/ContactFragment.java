package com.brik.android.chat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brik.android.chat.common.BaseFragment;
import com.brik.android.chat.common.OnOptionClickListener;
import com.brik.android.chat.entry.Contact;
import com.google.inject.Inject;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import roboguice.fragment.RoboFragment;

/**
 * Created by wangfengchen on 15/7/21.
 */
public class ContactFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, OnOptionClickListener {

    private SuperRecyclerView mRecycler;

    private MyAdapter mAdapter = new MyAdapter();

    private IChatService mService;

    @Inject
    private LayoutInflater layoutInflater;
    @Inject
    XMPPClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            mService = ((MainActivity) activity).getIChatService();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSuperRecyclerView(view);
        getContact();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initSuperRecyclerView(View view) {
        mRecycler = (SuperRecyclerView) view.findViewById(R.id.list);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        getContact();
    }

    void getContact() {
        client.getRoster(new XMPPClient.RosterListener() {
            @Override
            public void onComplete(Roster roster) {
                Collection<RosterEntry> entries = roster.getEntries();
                if (entries != null && !entries.isEmpty()) {

                    for (RosterEntry entry : entries) {
                        System.out.print(entry.getName() + " - " + entry.getUser() + " - "
                                + entry.getType() + " - " + entry.getGroups().size());
                        Presence presence = roster.getPresence(entry.getUser());
                        System.out.println(" - " + presence.getStatus() + " - "
                                + presence.getFrom());
                        final Contact contact = new Contact();
                        contact.setName(entry.getName());
                        contact.setUser(entry.getUser());
                        contact.setStatus(presence.getStatus());
                        contact.setFrom(presence.getFrom());
                        mAdapter.add(contact);
                    }
                }
            }
        });
    }

    @Override
    public void onOptionClick(View view) {

    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Contact> items = new ArrayList<>();

        public void add(Contact item) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }

        public void addAllAtStart(List<Contact> list) {
            items.addAll(0, list);
        }

        public Contact getItem(int position) {
            return items.get(position);
        }

        public void clear() {
            items.clear();
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View view = layoutInflater.inflate(R.layout.item_contact, parent, false);
                return new GroupViewHolder(view);
            } else {
                View view = layoutInflater.inflate(R.layout.item_contact, parent, false);
                return new UserViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == 1) {
                GroupViewHolder vh = (GroupViewHolder) holder;
                vh.bindView(getItem(position));
            } else {
                UserViewHolder vh = (UserViewHolder) holder;
                vh.bindView(getItem(position));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {
            Contact ct = getItem(position);
            if ("group".equals(ct.getType())) {
                return 1;
            }
            return 0;
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        ImageView headView;
        TextView nameView;

        public UserViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            headView = (ImageView) itemView.findViewById(R.id.item_contact_head);
            nameView = (TextView) itemView.findViewById(R.id.item_contact_name);
        }


        public void bindView(final Contact item) {
            String account = item.getUser().substring(0, item.getUser().indexOf('@'));
            String name = item.getName();
            nameView.setText((name == null ? "" : name) + "(" + account + ")");
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("user", item.getUser());
                    intent.putExtra("type", 1);
                    startActivity(intent);
                }
            });
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        ImageView headView;
        TextView nameView;

        public GroupViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            headView = (ImageView) itemView.findViewById(R.id.item_contact_head);
            nameView = (TextView) itemView.findViewById(R.id.item_contact_name);
        }


        public void bindView(final Contact item) {
            nameView.setText(item.getName());
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("jid", item.getJid());
                    intent.putExtra("type", 2);
                    startActivity(intent);
                }
            });
        }
    }

}
