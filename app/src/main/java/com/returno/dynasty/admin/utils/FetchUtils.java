package com.returno.dynasty.admin.utils;

import android.os.AsyncTask;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.returno.dynasty.admin.listeners.AnalyticsListener;
import com.returno.dynasty.admin.models.Message;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.models.CashBack;
import com.returno.dynasty.models.Offer;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class FetchUtils {
    public synchronized void getAllOffers(FetchCallBacks callBacks){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Urls.OFFERS_URL)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if (response!=null){
                                    try {
                                        List<Offer> offerList=new ArrayList<>();
                                        for (int i=0;i<response.length();i++){
                                            JSONObject object=response.getJSONObject(i);
                                            int id=object.getInt(Constants.ITEM_ID);
                                            String drinkName=object.getString(Constants.DRINK_NAME);
                                            int previousPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.PREVIOUS_PRICE)).intValue();
                                            int currentPrice=NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.CURRENT_PRICE)).intValue();
                                            String itemImage=object.getString(Constants.IMAGE_URL);
                                            Timber.e(itemImage);
                                            Offer offer=new Offer(drinkName,itemImage,previousPrice,currentPrice);
                                            offer.setOfferId(id);
                                            offerList.add(offer);
                                        }

                                        callBacks.onFetch(offerList);
                                    }catch (Exception e){
                                        callBacks.onError(e.getMessage());
                                    }
                                }else {
                                    callBacks.onError("Could not fetch data");
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                callBacks.onError(anError.getMessage());
                            }
                        });
            }
        });
        thread.start();
    }
    public synchronized void getAllDrinks(FetchCallBacks callBacks){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Urls.DRINKS_URL)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if (response!=null){
                                    try {
                                        List<Offer> offerList=new ArrayList<>();
                                        for (int i=0;i<response.length();i++){
                                            JSONObject object=response.getJSONObject(i);
                                            int id=object.getInt(Constants.ITEM_ID);
                                            String drinkName=object.getString(Constants.DRINK_NAME);
                                            int previousPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.PREVIOUS_PRICE)).intValue();
                                            int currentPrice=NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.CURRENT_PRICE)).intValue();
                                            String itemImage=object.getString(Constants.IMAGE_URL);
                                            Timber.e(itemImage);
                                            Offer offer=new Offer(drinkName,itemImage,previousPrice,currentPrice);
                                            offer.setOfferId(id);
                                            offerList.add(offer);
                                        }

                                        callBacks.onFetch(offerList);
                                    }catch (Exception e){
                                        callBacks.onError(e.getMessage());
                                    }
                                }else {
                                    callBacks.onError("Could not fetch data");
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                callBacks.onError(anError.getMessage());
                            }
                        });
            }
        });
        thread.start();
    }

    public void getAllMessages(FetchCallBacks callBacks){
        class Fetcher extends AsyncTask<Void,Integer,List<Message>>{

            @Override
            protected List<Message> doInBackground(Void... voids) {
                AndroidNetworking.get(Urls.GET_MESSAGES_URL)
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try{
                                    List<Message> messages=new ArrayList<>();
                                    for (int i=0;i<response.length();i++){
                                        JSONObject object=response.getJSONObject(i);
                                        int messageId=object.getInt(Constants.ITEM_ID);
                                        String message=object.getString(Constants.MESSAGE);
                                        String timeSent=object.getString(Constants.TIME_SENT);
                                        messages.add(new Message(messageId,message,timeSent));
                                    }
                                    onPostExecute(messages);
                                } catch (JSONException e) {
                                    Timber.e(e);
                                    callBacks.onError(e.getMessage());
                                    onPostExecute(null);
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                Timber.e(anError);
callBacks.onError(anError.getMessage());
onPostExecute(null);
                            }
                        });
                return null;
            }

            @Override
            protected void onPostExecute(List<Message> messageList) {
                super.onPostExecute(messageList);
                if (messageList==null){
                    return;
                }
                callBacks.onFetchMessage(messageList);
            }
        }
        new Fetcher().execute();
    }

    public void fetchAnalytics(AnalyticsListener listener){
        HashMap<String,Integer>usersMap=new HashMap<>(),categoryMap=new HashMap<>(),dayCategoryMap =new HashMap<>();

        AndroidNetworking.get(Urls.GET_ANALYTICS_URL)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                          try {
                            JSONArray usersArray=response.getJSONArray("users");
                            JSONArray categsArray=response.getJSONArray("categories");
                            JSONArray dayCategsArray=response.getJSONArray("day_count");


                            for (int i=0;i<usersArray.length();i++){
                                JSONObject object=usersArray.getJSONObject(i);
                                String date=object.getString("time");
                                int dateCount=object.getInt("count");
                                usersMap.put(date,dateCount);
                            }


                            for (int i=0;i<categsArray.length();i++){
                                JSONObject object=categsArray.getJSONObject(i);
                                String categ=object.getString("category");
                                int categCount=object.getInt("count");
                                categoryMap.put(categ,categCount);
                            }


                            for (int i=0;i<dayCategsArray.length();i++){
                                JSONObject object=categsArray.getJSONObject(i);
                                String categ=object.getString("date");
                                int categCount=object.getInt("count");
                                dayCategoryMap.put(categ,categCount);
                            }

                            listener.onAnalytics(usersMap,categoryMap,dayCategoryMap);

                        } catch (JSONException e) {
                            listener.onAnalytics(usersMap,categoryMap,dayCategoryMap);
                            listener.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
listener.onError("An Error Occured");
                        listener.onAnalytics(usersMap,categoryMap,dayCategoryMap);

                    }
                });
    }

    public synchronized void fetchCashBacks(FetchCallBacks callBacks){

        AndroidNetworking.get(Urls.GET_ALL_CASHBACK_URL)
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
                                String phoneNumber=object.getString(Constants.USER_PHONE);
                                int amount=object.getInt("amount");
                                cashBacks.add(new CashBack(cashbackId,phoneNumber,amount));

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
