package com.returno.dynasty.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bdhobare.mpesa.Mode;
import com.bdhobare.mpesa.Mpesa;
import com.bdhobare.mpesa.interfaces.AuthListener;
import com.bdhobare.mpesa.interfaces.MpesaListener;
import com.bdhobare.mpesa.models.STKPush;
import com.bdhobare.mpesa.utils.Pair;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.adapters.CartAdapter;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.cart.Cart;
import com.returno.dynasty.cart.CartHelper;
import com.returno.dynasty.custom.AccountSettingsDialog;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.FetchUtils;
import com.returno.dynasty.utils.PostUtils;
import com.returno.dynasty.utils.Urls;
import com.returno.dynasty.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class CartActivity extends AppCompatActivity implements AuthListener, MpesaListener {

    private RecyclerView recyclerView;
    private List<Cart>cartList;
    private CartAdapter adapter;
    private ExtendedFloatingActionButton button;
    private MaterialTextView editView,nameView,phoneView,locationView;
    private RadioButton btnAccount,btnMpesa;
    private TextView totalView;
    private Toolbar toolbar;
    private Dialog pDialog;
    private String balance;
    private boolean isAccount=false;
    private boolean isMpesa=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView=findViewById(R.id.recycler);
        totalView=findViewById(R.id.totalValue);

        button=findViewById(R.id.checkout);
        toolbar=findViewById(R.id.toolbar);
        editView=findViewById(R.id.edit);
        nameView=findViewById(R.id.userName);
        phoneView=findViewById(R.id.user_phone);
        locationView=findViewById(R.id.location);

        btnAccount=findViewById(R.id.chooseAccount);
        btnMpesa=findViewById(R.id.chooseMpesa);

        btnMpesa.setChecked(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cartList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pDialog=new Dialog(CartActivity.this);
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        adapter=new CartAdapter(getApplicationContext(), cartList, new ClickListener() {
            @Override
            public void onClick(String itemName, String itemImage, int itemPrice) {

            }

            @Override
            public void onViewClick(int position, View view) {
                new MaterialAlertDialogBuilder(CartActivity.this)
                        .setBackground(ContextCompat.getDrawable(CartActivity.this,R.drawable.bg_white_round_corner))
                        .setMessage("Remove This Item From Cart? ")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CartHelper.removeItemFromCart(position);
                                cartList.clear();
                                cartList.addAll(CartHelper.getCartList());
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();

            }
        });

int total=CartHelper.getTotalPrices();
totalView.setText(String.format(Locale.getDefault(),"Ksh.%d", total));

        recyclerView.setAdapter(adapter);
        cartList.addAll(CartHelper.getCartList());
        adapter.notifyDataSetChanged();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAccount && Integer.parseInt(balance)>=CartHelper.getTotalPrices()){
            makeOrder();
            return;
                }
               // Toast.makeText(getApplicationContext(),"Insufficient Funds In Your Dynasty Account, Use Mpesa",Toast.LENGTH_LONG).show();

                View view= LayoutInflater.from(CartActivity.this).inflate(R.layout.checkout_view,null,false);
                AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                builder.setView(view);
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        lipaNaMpesa();
                    }
                });

                ImageView close=view.findViewById(R.id.closeDialog);
                Dialog dialog=builder.create();
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing())dialog.dismiss();
                    }
                });


                dialog.show();
            }
        });

        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSettingsDialog.showDialog(getSupportFragmentManager());
            }
        });

        btnAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isAccount=true;
                    isMpesa=false;
                    btnMpesa.setChecked(false);
                }
            }
        });

        btnMpesa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isAccount=false;
                    isMpesa=true;
                    btnAccount.setChecked(false);
                }
            }
        });
        setUpUserInfo();

        fetchBalance();
    }

    private void lipaNaMpesa() {
        if (!pDialog.isShowing())pDialog.show();
        User user= UserUtils.getUser(CartActivity.this);

        Mpesa.with(this, Constants.CUSTOMER_KEY,Constants.CUSTOMER_SECRET, Mode.SANDBOX);
        STKPush.Builder builder=new STKPush.Builder(Constants.SHORT_CODE,
                Constants.PASS_KEY,
                CartHelper.getTotalPrices(),
                Constants.SHORT_CODE,
                user.getPhoneNumber());
        builder.setDescription("Add Funds");
        builder.setCallBackURL(Urls.M_PESA_FALL_BACK_URL);
        STKPush push=builder.build();

        Mpesa.getInstance().pay(this,push);
    }


    private void makeOrder() {
        if (!pDialog.isShowing())pDialog.show();
        User user=UserUtils.getUser(CartActivity.this);
        new PostUtils().makeOrder(CartHelper.prepareCart(), user.getPhoneNumber(), String.valueOf(CartHelper.getTotalPrices()), new CompleteListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onComplete(String message) {
if (pDialog.isShowing())pDialog.dismiss();
MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(CartActivity.this);
builder.setMessage("Order Sent. Check your Account Orders For Details. Order ID: "+message);
builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
});
Dialog dialog=builder.create();
dialog.show();
            }

            @Override
            public void onError(String message) {
Timber.e(message);
if (pDialog.isShowing())pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Could not Save Your Order",Toast.LENGTH_LONG).show();

            }
        });

    }


    private void fetchBalance() {
       if (!pDialog.isShowing())pDialog.show();
        User user= UserUtils.getUser(CartActivity.this);
        new FetchUtils().fetchUserBalance(user.getPhoneNumber(), new FetchCallBacks() {
            @Override
            public void onError(String message) {
                if (pDialog.isShowing())pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Could not get Your account Data",Toast.LENGTH_LONG).show();
                 onBackPressed();
            }

            @Override
            public void onStringFetched(String data) {
                balance=data;
                if (pDialog.isShowing())pDialog.dismiss();

            }
        });
    }


    private void setUpUserInfo() {
        User user= UserUtils.getUser(CartActivity.this);
        nameView.setText(user.getUserName());
        phoneView.setText(user.getPhoneNumber());
        locationView.setText(user.getLocation());

    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            startActivity(new Intent(CartActivity.this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthError(Pair<Integer, String> result) {
        Timber.e(result.message);
        Timber.e(String.valueOf(result.code));
        if (pDialog.isShowing())pDialog.dismiss();
        Toast.makeText(getApplicationContext(),"An Error Occured While Authenticating",Toast.LENGTH_LONG).show();


    }

    @Override
    public void onAuthSuccess() {
        if (pDialog.isShowing())pDialog.dismiss();
    }

    @Override
    public void onMpesaError(Pair<Integer, String> result) {
        Timber.e(result.message);
        if (pDialog.isShowing())pDialog.dismiss();
        Toast.makeText(getApplicationContext(),"An Error Occured While Processing",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMpesaSuccess(String MerchantRequestID, String CheckoutRequestID, String CustomerMessage) {
        Timber.e(CustomerMessage);
        if (pDialog.isShowing())pDialog.dismiss();
        Toast.makeText(getApplicationContext(),"Wait For The STK",Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                makeOrder();
            }
        },6000);
    }

}