package com.returno.dynasty.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.returno.dynasty.R;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.models.Offer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferHolder> implements Filterable {
    private Context context;
    private List<Offer> offerList;
    private List<Offer> filterList;
    private ClickListener listener;

    public OffersAdapter(Context context, List<Offer> offerList, ClickListener listener) {
        this.context = context;
        this.offerList = offerList;
        this.listener=listener;
        this.filterList=offerList;
    }

    @NonNull
    @Override
    public OffersAdapter.OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.offers_item,parent,false);
        return new OfferHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OffersAdapter.OfferHolder holder, int position) {
        Offer offer=filterList.get(position);
        holder.itemImage.setDrawingCacheEnabled(true);
holder.itemImage.setImageURI(offer.getItemUrl());
holder.itemPreviousPrice.setText(String.format(Locale.getDefault(),"Ksh.%d", offer.getPreviousPrice()));
holder.itemPreviousPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
holder.itemCurrentPrice.setText(String.format(Locale.getDefault(),"Ksh.%d", offer.getCurrentPrice()));
holder.itemTitle.setText(offer.getDrinkName());
  int diff=offer.getPreviousPrice()-offer.getCurrentPrice();
   float pd=(float)diff/offer.getPreviousPrice()*100;
   holder.itemPercentageDiscount.setText(String.format("-%s%%", Math.round(pd)));
        holder.addToCart.setOnClickListener(v -> listener.onClick(offer.getDrinkName(),offer.getItemUrl(),offer.getCurrentPrice()));
    }

    public void sortList(int order){
        for (Offer offer:offerList){
            offer.setSortParam(order);
        }
        Collections.sort(offerList);
        filterList=offerList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                int minPrice, maxPrice;
                boolean isFilter;
                isFilter=constraint.toString().split("__")[0].equals("filter");

                FilterResults filterResults=new FilterResults();
                ArrayList<Offer> offers=new ArrayList<>();
                String constraints=constraint.toString();
                Timber.e(constraints);
                Timber.e(String.valueOf(constraints.length()));
                if (constraints.isEmpty() || constraints.equals("search__")){
                    filterResults.count=offerList.size();
                    filterResults.values=offerList;
                    return filterResults;
                }
                if (constraint.length() == 0){
                    filterResults.count=filterList.size();
                    filterResults.values=filterList;
                    return filterResults;
                }else {
                    if (isFilter){
                        for (Offer offer : offerList) {

                            try {
                                minPrice = Integer.parseInt(constraint.toString().split("__")[1]);
                                maxPrice = Integer.parseInt(constraint.toString().split("__")[2]);

                            } catch (NumberFormatException e) {
                                Timber.e(e);
                                maxPrice = 1000;
                                minPrice = 100;
                            }
                            if (offer.getCurrentPrice() >= minPrice && offer.getCurrentPrice() <= maxPrice) {
                                offers.add(offer);

                            }
                        }
                }else {
                        for (Offer offer : offerList) {
                            String searchKey=constraint.toString().split("__")[1].toLowerCase();

                            if (offer.getDrinkName().toLowerCase().contains(searchKey) || offer.getDrinkName().toLowerCase().equals(searchKey)) {
                                offers.add(offer);

                            }
                        }
                    }
                    filterResults.count=offers.size();
                    filterResults.values=offers;
                }
                return filterResults;
            }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (ArrayList<Offer>) results.values;
                notifyDataSetChanged();
                if (filterList.isEmpty()){
                    Toast.makeText(context,"No Results match the filter",Toast.LENGTH_LONG).show();
                }
            }
        };
    }



    public static class OfferHolder extends RecyclerView.ViewHolder {
SimpleDraweeView itemImage;
TextView itemTitle, itemCurrentPrice,itemPreviousPrice, itemPercentageDiscount;
MaterialButton addToCart;
        public OfferHolder(@NonNull View itemView) {
            super(itemView);
            itemImage=itemView.findViewById(R.id.itemImage);
            itemTitle=itemView.findViewById(R.id.offerItemTitle);
            itemCurrentPrice=itemView.findViewById(R.id.itemCurrentPrice);
            itemPreviousPrice=itemView.findViewById(R.id.itemPreviousPrice);
            itemPercentageDiscount=itemView.findViewById(R.id.percentageDiscount);
            addToCart=itemView.findViewById(R.id.addToCart);

        }
    }

}
