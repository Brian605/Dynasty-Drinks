package com.returno.dynasty.utils;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.models.CashBack;
import com.returno.dynasty.models.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FetchUtils {
    public synchronized void fetchUserDetails(String phoneNumber, FetchCallBacks callBacks){
        AndroidNetworking.post(Urls.GET_USER_DETAILS)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.USER_PHONE,phoneNumber)
                .addBodyParameter("mode","users")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object=response.getJSONObject(0);
                            String name=object.getString(Constants.USER_NAME);
                            String phone=object.getString(Constants.USER_PHONE);
                            String location=object.getString(Constants.USER_LOCATION);
                            String image=object.getString(Constants.IMAGE_URL);
                            User user=new User(name,phone,location,image);

                            callBacks.onUserFetched(user);

                        } catch (JSONException e) {
                            onError(new ANError(e));
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                       callBacks.onError(anError.getMessage());
                        anError.printStackTrace();
                    }
                });
    }
    public synchronized void fetchUserBalance(String phoneNumber, FetchCallBacks callBacks){
        AndroidNetworking.post(Urls.GET_BALANCE_URL)
                .addBodyParameter(Constants.USER_PHONE,phoneNumber)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        callBacks.onStringFetched(response);
                    }

                    @Override
                    public void onError(ANError anError) {
callBacks.onError(anError.getMessage());
                    }
                });
    }
    public synchronized void fetchOrders(String phoneNumber, FetchCallBacks callBacks){
AndroidNetworking.post(Urls.GET_ORDERS_URL)
        .addBodyParameter(Constants.USER_PHONE,phoneNumber)
        .setPriority(Priority.HIGH)
        .build()
        .getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Timber.e(String.valueOf(response));
                try {
                    List<Order> orderList=new ArrayList<>();

                    for (int i=0;i<response.length();i++){
                        JSONObject object=response.getJSONObject(i);
                        String orderId=object.getString(Constants.ITEM_ID);
                        String total=object.getString("cost");
                        String status=object.getString("status");
                        String date=object.getString("date");
                        String itemsPrices=object.getString("items");
                        String items=itemsPrices.split("___")[0];
                        String prices=itemsPrices.split("___")[1];

                        Order order=new Order(orderId,items,prices,null,status,date,Integer.parseInt(total));
                        orderList.add(order);

                    }

                    callBacks.onUserOrderFetched(orderList);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBacks.onError(e.getMessage());
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
    public synchronized void fetchCashBacks(Context context,FetchCallBacks callBacks){

        AndroidNetworking.post(Urls.GET_USER_CASHBACK_URL)
                .addBodyParameter(Constants.USER_PHONE,UserUtils.getUser(context).getPhoneNumber())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<CashBack> cashBacks=new ArrayList<>();
                            for (int i=0;i<response.length();i++){
                                JSONObject object=response.getJSONObject(i);
                              String cashbackId=object.getString(Constants.ITEM_ID);
                              int amount=object.getInt("amount");
                              cashBacks.add(new CashBack(cashbackId,null,amount));

                            }
                            callBacks.onCashBacks(cashBacks);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
Timber.e(anError);
callBacks.onError(anError.getMessage());
                    }
                });

    }
    }
