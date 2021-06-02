package com.returno.dynasty.admin.listeners;

import android.view.View;

import com.returno.dynasty.admin.models.Message;
import com.returno.dynasty.models.Offer;

public interface SelectionListener {
    default void onSelect(Offer offer, View view){

    }

    default void onSelectIntent(Offer offer,View view){

    }

    default void onSelect(Message message){

    }
}
