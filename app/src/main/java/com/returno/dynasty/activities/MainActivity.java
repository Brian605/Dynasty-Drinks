package com.returno.dynasty.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.adapters.OffersAdapter;
import com.returno.dynasty.admin.MainAdminActivity;
import com.returno.dynasty.admin.adapters.MessagesAdapter;
import com.returno.dynasty.admin.listeners.SelectionListener;
import com.returno.dynasty.admin.models.Message;
import com.returno.dynasty.admin.utils.FetchUtils;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.cart.Cart;
import com.returno.dynasty.cart.CartHelper;
import com.returno.dynasty.custom.CashBacksDialog;
import com.returno.dynasty.custom.IndicatedViewFlipper;
import com.returno.dynasty.models.CashBack;
import com.returno.dynasty.models.Offer;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.DrinkUtils;
import com.returno.dynasty.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
private IndicatedViewFlipper flipper;
private OffersAdapter adapter;
private List<Offer> offerList;
private TextView cartCounter;
private TextView noData;
private boolean isFragment=false;
private CircleImageView topDeals,cashBacks;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flipper=findViewById(R.id.viewFlipper);
        MaterialToolbar toolbar = findViewById(R.id.toolBar);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        CircleImageView flipPrev = findViewById(R.id.flipPrev);
        CircleImageView flipNext = findViewById(R.id.flipNext);
        noData=findViewById(R.id.noData);
        MaterialTextView allOffersView = findViewById(R.id.allOffers);
        topDeals=findViewById(R.id.topDeals);
        cashBacks=findViewById(R.id.creditCashBack);


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Dynasty Drinks");


        offerList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

adapter=new OffersAdapter(this, offerList, new ClickListener() {
    @Override
    public void onClick(String itemName, String itemImage, int itemPrice) {
        addToCart(itemImage,itemName,itemPrice);
    }
});
recyclerView.setAdapter(adapter);

bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
    if (item.getItemId()==R.id.categoryMenu){
        showCategories();
    }
    else
        if (item.getItemId()==R.id.feedsMenu){
            showMessages();
        }
    else
        if (item.getItemId()==R.id.accountMenu){
            if (UserUtils.getAuthStatus(MainActivity.this)){
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,new ProfileFragment())
                        .commit();
                transaction.addToBackStack(null);
            }else {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,new RegisterFragment())
                        .commit();
                transaction.addToBackStack(null);
            }
        }
    else
        {
        Toast.makeText(getApplicationContext(),"Coming soon :)",Toast.LENGTH_LONG).show();
    }

return true;});



        Animation animIn = AnimationUtils.loadAnimation(this, R.anim.animin);
        Animation animOut = AnimationUtils.loadAnimation(this, R.anim.animout);

        flipper.setFlipInterval(5000);
        flipper.setAutoStart(true);
        flipper.setInAnimation(animIn);
        flipper.setOutAnimation(animOut);
        flipper.setAnimateFirstView(true);
        flipper.startFlipping();

 flipNext.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         flipper.showNext();
     }
 });
 flipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipper.showPrevious();
            }
        });

 allOffersView.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         gotoAllOffers();
     }
 });

 topDeals.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         gotoAllOffers();
     }
 });

 cashBacks.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         fetchCashBacks();
     }
 });


 fetchOffers();
    }

    private void fetchCashBacks() {
        Dialog pDialog=new Dialog(MainActivity.this);
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

        new com.returno.dynasty.utils.FetchUtils().fetchCashBacks(getApplicationContext(),new FetchCallBacks() {
            @Override
            public void onError(String message) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"An Error Occured",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCashBacks(List<CashBack> cashBackList) {
pDialog.dismiss();
                CashBacksDialog.showDialog(getSupportFragmentManager(),cashBackList);
            }
        });
    }

    private void gotoAllOffers() {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,new AllOffersFragment())
                .commit();
        transaction.addToBackStack(null);
    }


    private void showMessages() {
        Dialog pDialog=new Dialog(MainActivity.this);
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

        new FetchUtils().getAllMessages(new FetchCallBacks() {
            @Override
            public void onError(String message) {
pDialog.dismiss();
            }

            @Override
            public void onFetchMessage(List<Message> messages) {
pDialog.dismiss();
Dialog dialog=new Dialog(MainActivity.this);
dialog.setContentView(R.layout.message_dialog);
RecyclerView recyclerView=dialog.findViewById(R.id.recycler);
recyclerView.setHasFixedSize(true);
recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

recyclerView.setAdapter(new MessagesAdapter(getApplicationContext(), messages, new SelectionListener() {
    @Override
    public void onSelect(Message message) {

    }
}));

dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_white_round_corner));
dialog.show();

            }
        });
    }

    private void fetchOffers() {
        Dialog pDialog=new Dialog(MainActivity.this);
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        DrinkUtils.getInstance().getOffersPreview(new FetchCallBacks() {
            @Override
            public void onFetch(List<Offer> offers) {
                if (offers.size()<1){
                    noData.setVisibility(View.VISIBLE);
                    pDialog.dismiss();
                }
                pDialog.dismiss();
                offerList.addAll(offers);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
noData.setVisibility(View.VISIBLE);
Timber.e(message);
Toast.makeText(getApplicationContext(),"An Error Occured",Toast.LENGTH_LONG).show();
pDialog.dismiss();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (cartCounter!=null)refreshCartStatus();
    }

    private void showCategories() {
String[] categoryString=getResources().getStringArray(R.array.categories);
Intent intent=new Intent(MainActivity.this,ViewSearchActivity.class);
intent.putExtra(Constants.IS_CATEGORY,true);
AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
builder.setItems(categoryString, (dialog, which) -> {

        intent.putExtra(Constants.DRINK_CATEGORY,categoryString[which]);
Timber.e(categoryString[which]);
startActivity(intent);
dialog.dismiss();

});
Dialog dialog=builder.create();
dialog.show();
    }

    private void addToCart(String itemImage, String itemName, int itemPrice) {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Add This Drink to Cart?")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
int total=itemPrice;
int quantity=1;


if (CartHelper.isItemInCart(itemName)){
   Cart cart=CartHelper.getACartItemByName(itemName);
    quantity=cart.getItemQuantity()+1;
    total=itemPrice*quantity;

    int index=CartHelper.getIndexOfCartItem(itemName);
    CartHelper.removeItemFromCartByIndex(index);
}

Cart thisCart=new Cart(itemName,itemImage,itemPrice,quantity,total);
CartHelper.addItemToCart(thisCart);
refreshCartStatus();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
dialog.dismiss();
            }
        });
        Dialog dialog=builder.create();
        dialog.show();
    }

    private void refreshCartStatus() {
cartCounter.setText(String.valueOf(CartHelper.getCartSize()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);

        MenuItem item=menu.findItem(R.id.searchView);
        if (isFragment)item.setVisible(false);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.search_bg_white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent=new Intent(MainActivity.this, ViewSearchActivity.class);
                intent.putExtra(Constants.IS_SEARCH,true);
                intent.putExtra(Constants.SEARCH_KEY,query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });

        MenuItem cartMenu=menu.findItem(R.id.cart);
        if (isFragment)cartMenu.setVisible(false);
        cartMenu.setActionView(R.layout.cart_icon);
        RelativeLayout relativeLayout=(RelativeLayout)cartMenu.getActionView();
        cartCounter=(TextView)relativeLayout.findViewById(R.id.cartCounter);
        cartCounter.setText("0");
        cartCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartHelper.getCartSize()<1){
                    Toast.makeText(getApplicationContext(),"The cart is empty",Toast.LENGTH_LONG).show();
                    return;
                }
                startActivity(new Intent(MainActivity.this,CartActivity.class));
            }
        });
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.zoom_out);
        cartCounter.setAnimation(animation);
        cartCounter.startAnimation(animation);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.cart){
            if (CartHelper.getCartSize()<1){
                Toast.makeText(getApplicationContext(),"The cart is empty",Toast.LENGTH_LONG).show();
                return false;
            }
            startActivity(new Intent(MainActivity.this,CartActivity.class));

        }
        if (item.getItemId()==R.id.admin){
            startActivity(new Intent(MainActivity.this, MainAdminActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    public void setToolbar(MaterialToolbar toolbar){
        isFragment=true;
        invalidateOptionsMenu();
        setSupportActionBar(toolbar);
    }
}