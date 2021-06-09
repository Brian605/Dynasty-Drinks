package com.returno.dynasty.admin.listeners;

public interface CompleteListener {
    default void onComplete(){

    }
    default void onComplete(String message){

    }
    default void onError(String message){

    }
}
