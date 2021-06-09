package com.returno.dynasty.custom;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.adapters.OrdersAdapter;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.utils.UploadUtils;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.models.Order;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.PostUtils;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminOrdersDialog extends DialogFragment {
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private static List<Order> orderList;

    public static void showDialog(FragmentManager manager, List<Order> orders){
        AdminOrdersDialog dialog=new AdminOrdersDialog();
        dialog.show(manager,"Admin Orders");
        orderList=orders;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_LiquorStore_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog=getDialog();
        if (dialog!=null){
            int width= ViewGroup.LayoutParams.MATCH_PARENT;
            int height=ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width,height);
            dialog.getWindow().setWindowAnimations(R.style.Theme_LiquorStore_Slide);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.orders_layout,container,false);
       toolbar=view.findViewById(R.id.toolbar);
       recyclerView =view.findViewById(R.id.listView);
       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       recyclerView.setHasFixedSize(true);
       adapter=new OrdersAdapter(getActivity(), orderList, new ClickListener() {
           @Override
           public void onClick(View view1,Order order) {

               PopupMenu popupMenu=new PopupMenu(getActivity(),view1);
               popupMenu.getMenuInflater().inflate(R.menu.order_options_pop_menu,popupMenu.getMenu());
               popupMenu.setGravity(Gravity.BOTTOM);

               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
if (item.getItemId()==R.id.viewDetails){
    popupMenu.dismiss();
    showOrderDetails(order);
}
if (item.getItemId()==R.id.markDelivered){
                           popupMenu.dismiss();
                           markOrderDelivered(order.getOrderId(),order.getUserId());

                       }

                       if (item.getItemId()==R.id.delete){
                           popupMenu.dismiss();
                           deleteOrder(order.getOrderId());

                       }

                       if (item.getItemId()==R.id.mark_as_sent){
                           popupMenu.dismiss();
                           markOrderSent(order.getOrderId(),order.getUserId());

                       }

                       return false;
                   }
               });

               popupMenu.show();
               showOrderDetails(order);
           }
       });
       return view;

    }

    private void markOrderSent(String orderId,String phone) {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

        new  PostUtils().markOrderDelivered(phone, orderId, new CompleteListener() {

            @Override
            public void onComplete(String message) {
                if (message.equals("Success")){
                    pDialog.dismiss();
                }
            }

            @Override
            public void onError(String message) {
                pDialog.dismiss();
                getActivity().recreate();
            }
        });
    }

    private void deleteOrder(String orderId) {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

        new UploadUtils().deleteOrder(orderId, new CompleteListener() {
            @Override
            public void onComplete() {
                pDialog.dismiss();
                Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(String message) {
                pDialog.dismiss();
                Toast.makeText(getActivity(),"An Error Occured",Toast.LENGTH_LONG).show();


            }
        });

    }

    private void showOrderDetails(Order order) {
        Dialog dialog=new Dialog(getActivity());
        dialog.setContentView(R.layout.order_child_item);
        MaterialTextView orderValueView=dialog.findViewById(R.id.orderValue);
        MaterialTextView dateView=dialog.findViewById(R.id.orderDateValue);
        MaterialTextView statusView=dialog.findViewById(R.id.orderStatus);
        MaterialTextView orderItemsView=dialog.findViewById(R.id.orderItems);
        CircleImageView circleImageView=dialog.findViewById(R.id.closeDialog);

        MaterialButton markDelivered=dialog.findViewById(R.id.buttonMarkDelivered);


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        orderValueView.setText(String.format(Locale.getDefault(),"Ksh.%d", order.getTotalPrice()));
        dateView.setText(order.getOrderDate());

        if (order.getOrderStatus().equals(Constants.STATUS_SENT)){
            statusView.setText(Constants.STATUS_SENT);
        }

        if (order.getOrderStatus().equals(Constants.STATUS_RECEIVED)){
            statusView.setText(Constants.STATUS_RECEIVED);
            markDelivered.setVisibility(View.GONE);
        }

        orderItemsView.setText(formatItems(order));
        markDelivered.setVisibility(View.GONE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void markOrderDelivered(String orderId,String phone) {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

       new  PostUtils().markOrderDelivered(phone, orderId, new CompleteListener() {
           @Override
           public void onComplete() {
               pDialog.dismiss();
               Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();

           }

           @Override
           public void onComplete(String message) {
if (message.equals("Success")){
pDialog.dismiss();
}
           }

           @Override
           public void onError(String message) {
pDialog.dismiss();
getActivity().recreate();
           }
       });
    }

    private String formatItems(Order order) {
        String[] items=order.getOrderItems().split("__");
        String[] prices=order.getOrderPrices().split("__");

        int counter=0;

        StringBuilder builder=new StringBuilder();
        for (String itm:items){
            builder.append(counter+1).append(". ").append(itm).append(".... Ksh.").append(prices[counter]).append('\n');
            counter++;
        }
        return builder.toString();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        recyclerView.setAdapter(adapter);

    }
}
