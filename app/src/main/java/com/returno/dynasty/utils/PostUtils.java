package com.returno.dynasty.utils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.returno.dynasty.admin.listeners.CompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import timber.log.Timber;

public class PostUtils {

    public synchronized void makeOrder(List<String> items,String phone, String totalPrice, CompleteListener listener){
        AndroidNetworking.post(Urls.SAVE_ORDER_URL)
                .addBodyParameter("o_items",items.get(0))
                .addBodyParameter("user_id",phone)
                .addBodyParameter("o_prices",items.get(1))
                .addBodyParameter("total_price",totalPrice)
                .addBodyParameter("categories",items.get(2))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            listener.onComplete(response.getString("order_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
listener.onError(anError.getMessage());
                    }
                });
    }

    public synchronized void markOrderDelivered(String phone, String orderId, CompleteListener listener){
        AndroidNetworking.post(Urls.EDIT_ORDERS_STATUS_URL)
                .addBodyParameter("o_id",orderId)
                .addBodyParameter(Constants.USER_PHONE,phone)
                .addBodyParameter("status",Constants.STATUS_RECEIVED)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        listener.onComplete(response);
                    }

                    @Override
                    public void onError(ANError anError) {
listener.onError(anError.getMessage());
                    }
                });
    }

    public synchronized void addAnalytics(String phone){
        AndroidNetworking.post(Urls.ADD_ANALYTICS_URL)
                .addBodyParameter(Constants.USER_PHONE,phone)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
    public synchronized void requestCashBack(String cashbackId, CompleteListener listener){
AndroidNetworking.post(Urls.REQUEST_CASHBACK_URL)
        .addBodyParameter(Constants.ITEM_ID,cashbackId)
        .build()
        .getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                listener.onComplete();
            }

            @Override
            public void onError(ANError anError) {
                Timber.e(anError);
listener.onError(anError.getMessage());
            }
        });
    }


}
