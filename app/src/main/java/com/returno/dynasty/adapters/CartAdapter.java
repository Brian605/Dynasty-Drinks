package com.returno.dynasty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.cart.Cart;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Holder> {
    private Context context;
    private List<Cart>cartList;
    private ClickListener listener;

    public CartAdapter(Context context, List<Cart> cartList, ClickListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
       Holder holder= new Holder(view);
       view.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               listener.onViewClick(holder.getAdapterPosition(),view);
           }
       });
       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Cart cart=cartList.get(position);
holder.quantityView.setText(String.format(Locale.getDefault(),"x%d",cart.getItemQuantity()));
holder.titleView.setText(cart.getItemName());
holder.priceView.setText(String.format(Locale.getDefault(),"Ksh.%d", cart.getItemPrice()));
holder.imageView.setImageURI(cart.getItemImage());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
SimpleDraweeView imageView;
MaterialTextView titleView, priceView, quantityView;
        public Holder(@NonNull View itemView) {
            super(itemView);
  imageView=itemView.findViewById(R.id.itemImage);
  titleView=itemView.findViewById(R.id.itemName);
  priceView=itemView.findViewById(R.id.itemPrice);
  quantityView=itemView.findViewById(R.id.quantity);
        }
    }
}
