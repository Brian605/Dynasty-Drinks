package com.returno.dynasty.admin.listeners;

public interface DeleteListener {
    default void onItemDeleted(){

    }

    default void onBatchDeleteComplete(){

    }

    default void onError(String message){

    }
}
