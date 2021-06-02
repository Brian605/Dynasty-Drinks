package com.returno.dynasty.admin.utils;

import android.os.AsyncTask;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.listeners.DeleteListener;
import com.returno.dynasty.admin.listeners.UploadListener;
import com.returno.dynasty.models.Offer;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class UploadUtils implements DeleteListener {
    private int counter=0;
    private String mode;
    private List<Integer> ids;
    private String status;
    private DeleteListener listener;
    public UploadUtils(List<Integer> ids,String mode,DeleteListener listener){
        this.ids=ids;
        this.mode=mode;
        this.listener=listener;
    }
    public UploadUtils(){}

    @Override
    public void onItemDeleted() {
        if (counter>ids.size()-1){
            listener.onBatchDeleteComplete();
            return;
        }
        deleteSingleItem(ids.get(counter));
    }

    @Override
    public void onError(String message) {
        Timber.e(message);
        listener.onError(message);
    }

    public void start(){
        onItemDeleted();
    }

    public void uploadOffers(Offer offer, UploadListener listener){
    class AddOffer extends AsyncTask<Void,Integer,Void>{
String status="";
        @Override
        protected Void doInBackground(Void... voids) {
            AndroidNetworking.upload(Urls.ADD_OFFERS_URL)
                    .setPriority(Priority.HIGH)
                    .addMultipartParameter(Constants.DRINK_NAME,offer.getDrinkName())
                    .addMultipartParameter(Constants.PREVIOUS_PRICE, String.valueOf(offer.getPreviousPrice()))
                    .addMultipartParameter(Constants.CURRENT_PRICE, String.valueOf(offer.getCurrentPrice()))
                    .addMultipartFile(Constants.IMAGE_URL,new File(offer.getItemUrl()))
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Timber.e(response);
                            status=response;
                            onPostExecute(null);
                        }

                        @Override
                        public void onError(ANError anError) {
status=anError.getMessage();
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (status.equals("Success")){
                listener.onComplete();
                return;
            }
            listener.onError(status);
        }
    }
    new AddOffer().execute();
    }

    public void getApiKey(CompleteListener listener){
        AndroidNetworking.get(Urls.API_KEY_URL)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        listener.onComplete(response);
                    }

                    @Override
                    public void onError(ANError anError) {
listener.onError(anError.getMessage());
                    }
                });
    }
    public void uploadDrinks(Offer offer,String category, UploadListener listener){
        class AddOffer extends AsyncTask<Void,Integer,Void>{
            String status="";
            @Override
            protected Void doInBackground(Void... voids) {
                AndroidNetworking.upload(Urls.ADD_DRINKS_URL)
                        .setPriority(Priority.HIGH)
                        .addMultipartParameter(Constants.DRINK_NAME,offer.getDrinkName())
                        .addMultipartParameter(Constants.PREVIOUS_PRICE, String.valueOf(offer.getPreviousPrice()))
                        .addMultipartParameter(Constants.CURRENT_PRICE, String.valueOf(offer.getCurrentPrice()))
                        .addMultipartParameter(Constants.DRINK_CATEGORY,category)
                        .addMultipartFile(Constants.IMAGE_URL,new File(offer.getItemUrl()))
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Timber.e(response);
                                status=response;
                                onPostExecute(null);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Timber.e(anError);
                                status=anError.getMessage()!=null?anError.getMessage(): anError.toString();
                                onPostExecute(null);
                            }
                        });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (status.equals("Success")){
                    listener.onComplete();
                    return;
                }
                listener.onError(status);
            }
        }
        new AddOffer().execute();
    }

    public void deleteSingleItem(int id){
        class Deleter extends AsyncTask<Void,Integer,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    AndroidNetworking.post(Urls.DELETE_URL)
                            .setPriority(Priority.HIGH)
                            .addBodyParameter(Constants.DELETE_MODE, mode)
                            .addBodyParameter(Constants.ITEM_ID, String.valueOf(id))
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    status = response;
                                    onPostExecute(null);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Timber.e(anError);
                                    status = anError.getMessage();
                                    onPostExecute(null);
                                }
                            });

                } catch (Exception e) {
                    Timber.e(e);
                    status = e.getMessage();
                    onPostExecute(null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (status!=null && status.equals("Success")){
                    counter++;
                    onItemDeleted();
                }else{
                    if (counter!=0)
                    onError(status);
                }
            }
        }
        new Deleter().execute();
    }

    public void deleteAMessage(int id, DeleteListener listener){
        class Deleter extends AsyncTask<Void,Integer,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    AndroidNetworking.post(Urls.DELETE_MESSAGE_URL)
                            .setPriority(Priority.HIGH)
                            .addBodyParameter(Constants.ITEM_ID, String.valueOf(id))
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    status = response;
                                    onPostExecute(null);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Timber.e(anError);
                                    status = anError.getMessage();
                                    onPostExecute(null);
                                }
                            });

                } catch (Exception e) {
                    Timber.e(e);
                    status = e.getMessage();
                    onPostExecute(null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (status!=null && status.equals("Success")){
                   listener. onItemDeleted();
                }else{
                    listener.onError(status);
                }
            }
        }
        new Deleter().execute();
    }

    public String getCurrentTime(){
        Calendar calendar=Calendar.getInstance();
        int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH)+1;
        int year=calendar.get(Calendar.YEAR);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minutes=calendar.get(Calendar.MINUTE);

        return date+"/"+month+"/"+year+" @ "+hour+":"+minutes;
    }

    public void sendMessage(String key,String message,CompleteListener listener){
        class Sender extends AsyncTask<Void,Integer,Void>{

            private String status;
            @Override
            protected Void doInBackground(Void... voids) {
                //Send to db
                AndroidNetworking.post(Urls.SEND_MESSAGE_URL)
                        .setPriority(Priority.HIGH)
                        .addBodyParameter(Constants.MESSAGE,message)
                        .addBodyParameter(Constants.TIME_SENT,getCurrentTime())
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                               if (response.equals("Success")){
try {
    JSONObject notificationObject=new JSONObject();
    notificationObject.put("title","Dynasty Admin");
    notificationObject.put("body",message);

    JSONObject payLoad=new JSONObject();
    payLoad.put("to","/topics/broadcast");
    payLoad.put("notification",notificationObject);

    AndroidNetworking.post(Urls.NOTIFICATION_URL)
            .setPriority(Priority.HIGH)
            .setContentType("application/json; charset=utf-8")
            .addJSONObjectBody(payLoad)
            .addHeaders("Authorization",key)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    status="success";
                    onPostExecute(null);
                }

                @Override
                public void onError(ANError anError) {
                    status="failed";
                    listener.onError(anError.getMessage());
                    onPostExecute(null);
                    Timber.e(anError);
                }
            });


} catch (JSONException e) {
    status="failed";
    onError(new ANError(e));
}

                               }else {
                                  status="failed";
                                  onError(new ANError(response));
                                  onPostExecute(null);
                               }
                            }

                            @Override
                            public void onError(ANError anError) {
status="failed";
listener.onError(anError.getMessage());
onPostExecute(null);
Timber.e(anError);
                            }
                        });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (status!=null && status.equals("failed")){
                    return;
                }
                listener.onComplete();
            }
        }
        new Sender().execute();

    }

    public void deleteCashBack(String cashbackId, CompleteListener listener) {
        AndroidNetworking.post(Urls.DELETE_CASHBACK_URL)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.ITEM_ID,cashbackId)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        listener.onComplete();
                    }

                    @Override
                    public void onError(ANError anError) {
listener.onError(anError.getMessage());
                    }
                });
    }
}
