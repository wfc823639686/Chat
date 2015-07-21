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

import com.brik.android.chat.db.MessageDAO;
import com.brik.android.chat.db.entry.Contact;
import com.brik.android.chat.service.event.ConnectEvent;
import com.brik.android.chat.service.event.LoginEvent;
import com.brik.android.chat.service.event.RosterEvent;
import com.brik.android.chat.service.listener.ConnectListener;
import com.brik.android.chat.service.listener.LoginListener;
import com.brik.android.chat.service.listener.RosterListener;
import com.google.inject.Inject;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import roboguice.fragment.RoboFragment;

/**
 * Created by wangfengchen on 15/7/21.
 */
public class ContactFragment extends RoboFragment implements SwipeRefreshLayout.OnRefreshListener{

    private SuperRecyclerView mRecycler;

    private MyAdapter mAdapter = new MyAdapter();

    private IChatService mService;

    @Inject
    private LayoutInflater layoutInflater;

    private ConnectListener connectListener = new ConnectListener() {
        @Override
        public void onSuccess(ConnectEvent data) {
            System.out.println("连接成功");
        }

        @Override
        public void onFail(Throwable throwable) {
            System.out.println("连接失败，" + throwable.getMessage());
        }
    };

    private LoginListener loginListener = new LoginListener() {
        @Override
        public void onSuccess(LoginEvent data) {
            System.out.println("login成功");
        }

        @Override
        public void onFail(Throwable throwable) {
            System.out.println("login失败，" + throwable.getMessage());
        }
    };

    private RosterListener rosterListener = new RosterListener() {
        @Override
        public void onSuccess(final RosterEvent data) {
            final Collection<RosterEntry> entries = data.roster.getEntries();
            if(entries!=null && !entries.isEmpty()) {

                mRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clear();
                        for (RosterEntry entry : entries) {
                            System.out.print(entry.getName() + " - " + entry.getUser() + " - "
                                    + entry.getType() + " - " + entry.getGroups().size());
                            Presence presence = data.roster.getPresence(entry.getUser());
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
                });

            }
        }

        @Override
        public void onFail(Throwable throwable) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatEventObservable.getInstance().register(connectListener);
        ChatEventObservable.getInstance().register(loginListener);
        ChatEventObservable.getInstance().register(rosterListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatEventObservable.getInstance().unregister(connectListener);
        ChatEventObservable.getInstance().unregister(loginListener);
        ChatEventObservable.getInstance().unregister(rosterListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof MainActivity) {
            mService = ((MainActivity)activity).getIChatService();
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
        try {
            mService.getRoster();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
                View view = layoutInflater.inflate(R.layout.item_contact, parent, false);
                return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MyViewHolder vh = (MyViewHolder) holder;
                vh.bindView(getItem(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        ImageView headView;
        TextView nameView;

        public MyViewHolder(View itemView) {
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
                    intent.putExtra("user", item.getUser());
                    startActivity(intent);
                }
            });
        }
    }


}
