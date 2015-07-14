package com.brik.android.chat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.brik.android.chat.R;

/**
 * Created by wangfengchen on 15/7/13.
 */
public class TemplateView extends FrameLayout {
    private int mTemplateViewMainLayout, mTemplateViewContentLayout, mTemplateViewTitleLayout;
    private View mTitleView, mContentView;

    protected ViewStub mContent, mTitle;

    public TemplateView(Context context) {
        super(context);
        this.initView();
    }

    public TemplateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initAttrs(attrs);
        this.initView();
    }

    public TemplateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initAttrs(attrs);
        this.initView();
    }

    public TemplateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initAttrs(attrs);
        this.initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = this.getContext().obtainStyledAttributes(attrs, com.malinskiy.superrecyclerview.R.styleable.superrecyclerview);

        try {
            mTemplateViewMainLayout = a.getResourceId(R.styleable.templateview_mainLayout, 0);
            mTemplateViewContentLayout = a.getResourceId(R.styleable.templateview_contentLayoutId, 0);
            mTemplateViewTitleLayout = a.getResourceId(R.styleable.templateview_titleLayoutId, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

    }

    protected void initView() {
        if(!this.isInEditMode()) {
            View v = LayoutInflater.from(this.getContext()).inflate(this.mTemplateViewMainLayout, this);

            mTitle = (ViewStub)v.findViewById(R.id.title);
            this.mTitle.setLayoutResource(this.mTemplateViewTitleLayout);
            if(this.mTemplateViewTitleLayout != 0) {
                this.mTitleView = this.mTitle.inflate();
            }

            this.mContent = (ViewStub) v.findViewById(R.id.content);
            this.mContent.setLayoutResource(this.mTemplateViewContentLayout);
            if(this.mTemplateViewContentLayout != 0) {
                this.mContentView = this.mContent.inflate();
            }
        }
    }

}
