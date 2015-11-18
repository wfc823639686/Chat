package com.brik.chat.android;

import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.brik.chat.android.TestActivity;

/**
 * Created by wangfengchen on 15/11/18.
 */
public class MyTest extends InstrumentationTestCase {

    TestActivity testActivity;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("com.brik.chat.android", TestActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        testActivity = (TestActivity) getInstrumentation().startActivitySync(intent);
//        testActivity = launchActivity("com.brik.chat.android", TestActivity.class, null);
    }

    @Override
    protected void tearDown() throws Exception {
        testActivity.finish();
        super.tearDown();
    }

    public void test() throws Exception {
        Log.v("test", "test the method");
        testActivity.testMessageDB();
    }
}
