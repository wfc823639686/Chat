// IChatService.aidl
package com.brik.android.chat;

// Declare any non-default types here with import statements

interface IChatService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void connect();

    void login(String username, String password);

    void register();
}