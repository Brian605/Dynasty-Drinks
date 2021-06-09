package com.returno.dynasty.admin.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.models.Order;
import com.returno.dynasty.utils.Constants;

import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.Holder> {
private final Context context;
private final List<Order>orderList;
private final ClickListener listener;

    public AdminOrdersAdapter(Context context, List<Order> orderList, ClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View convertView= LayoutInflater.from(context).inflate(R.layout.order_item,parent,false);
Holder holder=new Holder(convertView);
convertView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
listener.onClick(v,orderList.get(holder.getAdapterPosition()));
    }
});

    return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Order order= orderList.get(position);
        String title=order.getOrderId();
        holder.titleView.setText(title);
       holder. titleView.setTypeface(null, Typeface.BOLD);
       holder.phoneView.setText(order.getUserId());

        if (order.getOrderStatus().equals(Constants.STATUS_SENT)){
            holder.titleView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_local_shipping_orange,0,R.drawable.ic_baseline_keyboard_arrow_down_24,0);
        }
        if (order.getOrderStatus().equals(Constants.STATUS_RECEIVED)){
            holder.titleView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_green,0,R.drawable.ic_baseline_keyboard_arrow_down_24,0);
        }


    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        MaterialTextView titleView,phoneView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            titleView=itemView.findViewById(R.id.orderIdView);
            phoneView=itemView.findViewById(R.id.phoneView);


        }
    }
}
