package com.brik.chat.android;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.brik.chat.utils.ChatModule;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import roboguice.RoboGuice;

/**
 * Created by wangfengchen on 15/6/29.
 */
public class ChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice. setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new ChatModule(this));
        initImageLoader();

    }

    void initImageLoader(){
        Log.d("initImageLoader", "initImageLoaderConfiguration");
        DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.whitesmoke)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//oom
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultDisplayImageOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
