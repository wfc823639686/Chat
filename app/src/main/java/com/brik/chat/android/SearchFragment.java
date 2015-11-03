package com.brik.chat.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brik.chat.common.BaseActivity;
import com.brik.chat.common.BaseFragment;
import com.brik.chat.entry.Contact;
import com.google.inject.Inject;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/11/2.
 */
public class SearchFragment extends BaseFragment {
    @Inject
    XMPPClient client;
    @InjectView(R.id.search_input)
    EditText searchEdit;
    @InjectView(R.id.search_submit)
    Button submitBtn;
    @InjectView(R.id.search_list)
    RecyclerView recyclerView;
    @Inject
    LayoutInflater layoutInflater;

    MyAdapter mAdapter = new MyAdapter();

    BaseActivity baseActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.framgnet_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        submitBtn.setOnClickListener(this);
    }

    private void initRecyclerView(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
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
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            UserViewHolder vh = (UserViewHolder) holder;
            vh.bindView(getItem(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
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
            String format = "%1$s (%2$s)";
            nameView.setText(String.format(format, item.getName(), item.getUser()));
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    client.addFriend(item.getUser(), new XMPPClient.AddFriendListener() {
                        @Override
                        public void onSuccess() {
                            baseActivity.showToast("添加成功");
                        }

                        @Override
                        public void onFail(Throwable t) {
                            Log.e("addFriend", "失败", t);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_submit:
                String user = searchEdit.getText().toString();
                client.searchUsers(user, new XMPPClient.SearchUsersListener() {
                    @Override
                    public void onSuccess(final ArrayList<Contact> result) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.clear();
                                mAdapter.addAllAtStart(result);
                            }
                        });
                    }

                    @Override
                    public void onFail(Throwable t) {

                    }
                });
                break;
        }
    }
}
