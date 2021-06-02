package com.returno.dynasty.admin.listeners;

public interface UploadListener {
    default void onComplete(){

    }

    default void onError(String message){

    }
}
