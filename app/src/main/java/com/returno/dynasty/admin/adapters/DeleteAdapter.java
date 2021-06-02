package com.returno.dynasty.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.listeners.SelectionListener;
import com.returno.dynasty.models.Offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.ItemHolder> {
private final Context context;
private final List<Offer>offers;
private final SelectionListener listener;
public static boolean isSelecting=false;
public static List<View>selectedViews;

    public DeleteAdapter(@NonNull Context context, List<Offer> offers,SelectionListener listener) {
        this.context=context;
        this.offers=offers;
        this.listener=listener;
        selectedViews=new ArrayList<>();
    }

    public void setSelected(View view){
        view.setSelected(true);
    }
    public void unSelect(View view){
        view.setSelected(false);
    }

    public void clearSelection(){
        for (View view:selectedViews){
                view.setSelected(false);

        }
    }
    public void selectAll(){
        for (View view:selectedViews){
            if (isSelecting){
            view.setSelected(true);
            }
        }
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.admin_offers_item,parent,false);
       selectedViews.add(view);
       ItemHolder holder=new ItemHolder(view);
       view.setOnLongClickListener(view1 -> {
          listener.onSelectIntent(offers.get(holder.getAdapterPosition()),view1);
           return true;
       });
       view.setOnClickListener(view12 -> listener.onSelect(offers.get(holder.getAdapterPosition()),view12));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Offer offer=offers.get(position);
       holder.imageView.setImageURI(offer.getItemUrl());
        holder.titleView.setText(offer.getDrinkName());
        holder.currentView.setText(String.format(Locale.getDefault(),"Ksh.%d", offer.getCurrentPrice()));
        holder.previousView.setText(String.format(Locale.getDefault(),"Ksh.%d", offer.getPreviousPrice()));
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        MaterialTextView titleView,currentView,previousView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.itemImage);
            titleView=itemView.findViewById(R.id.offerItemTitle);
            currentView=itemView.findViewById(R.id.itemCurrentPrice);
            previousView=itemView.findViewById(R.id.itemPreviousPrice);
        }
    }
}
