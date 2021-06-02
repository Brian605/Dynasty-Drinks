package com.returno.dynasty.callbacks;

import com.returno.dynasty.admin.models.Message;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.models.CashBack;
import com.returno.dynasty.models.Offer;
import com.returno.dynasty.models.Order;

import java.util.List;

public interface FetchCallBacks {
    default void onFetch(List<Offer> offerList){

    }

    default void onError(String message){

    }

    default void onCashBacks(List<CashBack> cashBackList){

    }


    default void onCustomerKeys(String key,String secret){

    }

    default void onFetchMessage(List<Message> messages){

    }

    default void onUserFetched(User user){

    }

    default void onStringFetched(String data){

    }

    default void onUserOrderFetched(List<Order>orders){

    }
}
