package com.returno.dynasty.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.models.User;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import timber.log.Timber;

public class UserUtils {
    public static boolean getAuthStatus(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.USER_PREFS,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.IS_USER_LOGGED_IN,false);
    }

    public static int generateRandomOTP(){
        return ThreadLocalRandom.current().nextInt(1000,9999+1);
    }

    public static void saveUser(User user, Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.USER_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.putString(Constants.USER_NAME,user.getUserName());
        editor.putString(Constants.USER_PHONE,user.getPhoneNumber());
        editor.putString(Constants.USER_LOCATION,user.getLocation());
        editor.putString(Constants.IMAGE_URL,user.getImageUrl());
        editor.putBoolean(Constants.IS_USER_LOGGED_IN,true);
        editor.apply();

    }

    public static User getUser(Context context){
        if (!getAuthStatus(context)){
            return null;
        }
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.USER_PREFS,Context.MODE_PRIVATE);
String userName=sharedPreferences.getString(Constants.USER_NAME,null);
String userPhone=sharedPreferences.getString(Constants.USER_PHONE,null);
String userLocation=sharedPreferences.getString(Constants.USER_LOCATION,null);
String userImage=sharedPreferences.getString(Constants.IMAGE_URL,null);

        return new User(userName,userPhone,userLocation,userImage);
    }


    public static void signOutUser(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.USER_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void updateAvatar(File avatar,String phone, CompleteListener listener){
        AndroidNetworking.upload(Urls.EDIT_DETAILS_URL)
                .addMultipartFile(Constants.IMAGE_URL,avatar)
                .addMultipartParameter(Constants.USER_PHONE,phone)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        listener.onComplete(response);
                    }

                    @Override
                    public void onError(ANError anError) {
listener.onError(anError.getMessage());
                    }
                });
    }

    public static void updateName(String name,String phone, CompleteListener listener){
        AndroidNetworking.post(Urls.EDIT_DETAILS_URL)
                .addBodyParameter("name",name)
                .addBodyParameter(Constants.USER_PHONE,phone)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        listener.onComplete(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        listener.onError(anError.getMessage());
                    }
                });
    }

    public static void updateLocation(String location,String phone, CompleteListener listener){
        AndroidNetworking.post(Urls.EDIT_DETAILS_URL)
                .addBodyParameter(Constants.USER_LOCATION,location)
                .addBodyParameter(Constants.USER_PHONE,phone)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        listener.onComplete(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        listener.onError(anError.getMessage());
                    }
                });
    }

    public static void updateBalance(String amount,String phone, CompleteListener listener){
        AndroidNetworking.post(Urls.EDIT_DETAILS_URL)
                .addBodyParameter("amount",amount)
                .addBodyParameter(Constants.USER_PHONE,phone)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        listener.onComplete(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        listener.onError(anError.getMessage());
                    }
                });
    }

}
