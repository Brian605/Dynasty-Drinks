package com.returno.dynasty.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.custom.AccountDialog;
import com.returno.dynasty.custom.AccountSettingsDialog;
import com.returno.dynasty.custom.OrdersDialog;
import com.returno.dynasty.models.Order;
import com.returno.dynasty.utils.FetchUtils;
import com.returno.dynasty.utils.UserUtils;

import java.util.List;

import timber.log.Timber;

public class ProfileFragment extends Fragment {
private MaterialToolbar toolbar;
private SimpleDraweeView avatarView;
private MaterialTextView nameView,phoneView,accountView,ordersView,settingsView;
private MaterialButton signOutView;
private User user;
private String balance;
private Dialog pDialog;
private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        container.removeAllViews();
        View view=inflater.inflate(R.layout.activity_profile,container,false);
        toolbar=view.findViewById(R.id.toolBar);
        avatarView=view.findViewById(R.id.user_avatar);
        nameView=view.findViewById(R.id.user_name);
        phoneView=view.findViewById(R.id.user_phone);
        accountView=view.findViewById(R.id.user_account);
        ordersView=view.findViewById(R.id.user_orders);
        settingsView=view.findViewById(R.id.user_settings);
        signOutView=view.findViewById(R.id.user_logout);

        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ((MainActivity)getActivity()).setToolbar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
                activity.recreate();

            }
        });

        accountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAccountsFragment() ;
            }
        });

        signOutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserUtils.signOutUser(getActivity());
                activity.onBackPressed();
                activity.recreate();
            }
        });

        ordersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrdersFragment();
            }
        });

        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsFragment();
            }
        });

        pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);

        fetchUserInfo();
        return view;
    }

    private void showOrdersFragment() {
        OrdersDialog.showDialog(getActivity().getSupportFragmentManager(),orderList);
    }

    private void showAccountsFragment() {
        Timber.e(balance);
        AccountDialog.showDialog(getActivity().getSupportFragmentManager(),balance);
    }

    private void showSettingsFragment() {
        AccountSettingsDialog.showDialog(getActivity().getSupportFragmentManager());
    }

    private void fetchUserInfo() {
        pDialog.show();
        User user= UserUtils.getUser(getActivity());
        avatarView.setImageURI(user.getImageUrl());
      new FetchUtils().fetchUserBalance(user.getPhoneNumber(), new FetchCallBacks() {
          @Override
          public void onError(String message) {
              if (pDialog.isShowing())pDialog.dismiss();
              Toast.makeText(getActivity(),"Could not get Your account Data",Toast.LENGTH_LONG).show();
              AppCompatActivity activity=(AppCompatActivity)getActivity();
              activity.onBackPressed();
              activity.recreate();
          }

          @Override
          public void onStringFetched(String data) {
              nameView.setText(user.getUserName());
              phoneView.setText(user.getPhoneNumber());
balance=data;

fetchOrders(user.getPhoneNumber());

          }
      });
    }

    private void fetchOrders(String phoneNumber) {
        new FetchUtils().fetchOrders(phoneNumber, new FetchCallBacks() {
            @Override
            public void onError(String message) {
                if (pDialog.isShowing())pDialog.dismiss();
                Toast.makeText(getActivity(),"Could not get Your account Data",Toast.LENGTH_LONG).show();
                AppCompatActivity activity=(AppCompatActivity)getActivity();
                activity.onBackPressed();
                activity.recreate();
            }

            @Override
            public void onUserOrderFetched(List<Order> orders) {
                if (pDialog.isShowing())pDialog.dismiss();
orderList=orders;
            }
        });
    }
}