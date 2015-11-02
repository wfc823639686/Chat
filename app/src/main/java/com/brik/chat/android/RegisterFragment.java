package com.brik.chat.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.brik.chat.common.BaseActivity;
import com.brik.chat.common.BaseFragment;
import com.google.inject.Inject;

import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/10/30.
 */
public class RegisterFragment extends BaseFragment {

    @Inject
    private XMPPClient client;
    @Inject
    private SystemSettings settings;

    @InjectView(R.id.register_username)
    EditText usernameEdit;
    @InjectView(R.id.register_password)
    EditText passwordEdit;
    @InjectView(R.id.register_submit)
    Button submitBtn;

    private BaseActivity baseActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.framgnet_register, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_submit:
                final String username = usernameEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                Log.d("username", username);
                Log.d("password", password);
                client.register(username, password, new XMPPClient.RegisterListener() {

                    @Override
                    public void onComplete(final int result) {
                        Log.d("register", "result " + result);
                        submitBtn.post(new Runnable() {
                            @Override
                            public void run() {
                                switch (result) {
                                    case 1:
                                        baseActivity.showToast("注册成功");
                                        baseActivity.popFragment();
                                        return;
                                    case 0:
                                        baseActivity.showToast("注册失败");
                                        return;
                                }
                            }
                        });
                    }
                });
                break;
        }
    }

}
