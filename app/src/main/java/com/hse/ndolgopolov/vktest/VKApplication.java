package com.hse.ndolgopolov.vktest;

import android.app.Application;

import com.vk.sdk.VKSdk;


/**
 * Created by Nickolay Dolgopolov on 07.07.2016.
 */

public class VKApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this.getApplicationContext());
    }
}
