package com.returno.dynasty.utils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.models.Offer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DrinkUtils {
    private static DrinkUtils drinkUtils;

    public static DrinkUtils getInstance() {
        if (drinkUtils==null)drinkUtils=new DrinkUtils();
        return drinkUtils;
    }

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
                                            int currentPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.CURRENT_PRICE)).intValue();
                                            String itemImage=object.getString(Constants.IMAGE_URL);
                                            Offer offer=new Offer(drinkName,itemImage,previousPrice,currentPrice);
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

    public synchronized void getOffersPreview(FetchCallBacks callBacks){
Thread thread=new Thread(new Runnable() {
    @Override
    public void run() {
        AndroidNetworking.post(Urls.OFFERS_PREVIEW_URL)
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
                                    String drinkName=object.getString(Constants.DRINK_NAME);
                                    int previousPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.PREVIOUS_PRICE)).intValue();
                                    int currentPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.CURRENT_PRICE)).intValue();
                                    String itemImage=object.getString(Constants.IMAGE_URL);
                                    Offer offer=new Offer(drinkName,itemImage,previousPrice,currentPrice);
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

    public synchronized void getDrinksByCategory(String category, FetchCallBacks callBacks){
Thread thread=new Thread(new Runnable() {
    @Override
    public void run() {
AndroidNetworking.post(Urls.DRINKS_CATEGORY_URL)
        .setPriority(Priority.HIGH)
        .addBodyParameter(Constants.DRINK_CATEGORY,category)
        .build()
        .getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                if (response!=null){
                    try {
                        List<Offer> drinkList=new ArrayList<>();
                        for (int i=0;i<response.length();i++){
                            JSONObject object=response.getJSONObject(i);
                            String drinkName=object.getString(Constants.DRINK_NAME);
                            int previousPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.PREVIOUS_PRICE)).intValue();
                            int currentPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.CURRENT_PRICE)).intValue();
                            String itemImage=object.getString(Constants.IMAGE_URL);
                            Offer drink=new Offer(drinkName,itemImage,previousPrice,currentPrice);
                            drinkList.add(drink);
                        }

                        callBacks.onFetch(drinkList);
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

    public synchronized void getDrinksBySearch(String key, FetchCallBacks callBacks){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(Urls.DRINKS_SEARCH_URL)
                        .setPriority(Priority.HIGH)
                        .addBodyParameter(Constants.SEARCH_KEY,key)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if (response!=null){
                                    try {
                                        List<Offer> drinkList=new ArrayList<>();
                                        for (int i=0;i<response.length();i++){
                                            JSONObject object=response.getJSONObject(i);
                                            String drinkName=object.getString(Constants.DRINK_NAME);
                                            int previousPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.PREVIOUS_PRICE)).intValue();
                                            int currentPrice= NumberFormat.getNumberInstance(Locale.US).parse(object.getString(Constants.CURRENT_PRICE)).intValue();
                                            String itemImage=object.getString(Constants.IMAGE_URL);
                                            Offer drink=new Offer(drinkName,itemImage,previousPrice,currentPrice);
                                            drinkList.add(drink);
                                        }

                                        callBacks.onFetch(drinkList);
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

}