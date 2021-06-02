package com.returno.dynasty.callbacks;

import android.view.View;

import com.returno.dynasty.models.Order;

public interface ClickListener {
   default void onClick(String itemName, String itemImage, int itemPrice){

   }
    default void onViewClick(int position, View view){

    }

    default void onClick(Order order){

    }

   default void onClick(String cashBackId){

   }
}
