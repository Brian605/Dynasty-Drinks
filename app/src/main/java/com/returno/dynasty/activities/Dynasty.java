package com.returno.dynasty.activities;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returno.dynasty.BuildConfig;
import com.returno.dynasty.utils.PostUtils;
import com.returno.dynasty.utils.UserUtils;

import timber.log.Timber;

public class Dynasty extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        if (BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        FirebaseMessaging.getInstance().subscribeToTopic("broadcast");
sendAnalytics();

    }

    private void sendAnalytics() {
if (UserUtils.getAuthStatus(getApplicationContext())){
   new  PostUtils().addAnalytics(UserUtils.getUser(getApplicationContext()).getPhoneNumber());
}
    }


}
