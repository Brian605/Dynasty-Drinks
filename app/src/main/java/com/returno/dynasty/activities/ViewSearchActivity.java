package com.returno.dynasty.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.adapters.DrinksAdapter;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.cart.Cart;
import com.returno.dynasty.cart.CartHelper;
import com.returno.dynasty.models.Offer;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.DrinkUtils;
import com.returno.dynasty.utils.SortParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class ViewSearchActivity extends AppCompatActivity {
private MaterialToolbar toolbar;
private MaterialTextView spinner;
private TextView cartCounter, noData;
private DrinksAdapter adapter;
private List<Offer>offerList;
private RecyclerView recyclerView;
private RelativeLayout cartView;
private String category, searchTerm;
private ImageView sortView;
private MaterialTextView filterText;
private SearchView searchView;
private Intent intent;
private boolean onUserInteraction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_search);
        intent=getIntent();

        toolbar=findViewById(R.id.toolbar);
        spinner=findViewById(R.id.spinner);
        setSupportActionBar(toolbar);
        cartCounter=findViewById(R.id.cartCounter);
        recyclerView=findViewById(R.id.recycler);
        cartView=findViewById(R.id.relative);
        noData=findViewById(R.id.noData);
        sortView=findViewById(R.id.sort);
        filterText=findViewById(R.id.filter);
        searchView=findViewById(R.id.searchView);

        cartCounter.setText("0");
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.zoom_out);
        cartCounter.setAnimation(animation);
        cartCounter.startAnimation(animation);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        offerList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));


        refreshCart();
        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartHelper.getCartSize()<1){
                    Toast.makeText(getApplicationContext(),"The cart is empty",Toast.LENGTH_LONG).show();
                    return ;
                }
                startActivity(new Intent(ViewSearchActivity.this,CartActivity.class));
            }
        });
        cartCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartHelper.getCartSize()<1){
                    Toast.makeText(getApplicationContext(),"The cart is empty",Toast.LENGTH_LONG).show();
                    return ;
                }
                startActivity(new Intent(ViewSearchActivity.this,CartActivity.class));
            }
        });

   adapter=new DrinksAdapter(getApplicationContext(), offerList, new ClickListener() {
       @Override
       public void onClick(String itemName, String itemImage, int itemPrice) {
addToCart(itemImage,itemName,itemPrice);
       }
   });

   recyclerView.setAdapter(adapter);

   boolean isCategory=intent.getBooleanExtra(Constants.IS_CATEGORY,false);
   boolean isSearch=intent.getBooleanExtra(Constants.IS_SEARCH,false);


   if (isCategory){
       Dialog pDialog=new Dialog(ViewSearchActivity.this);
       pDialog.setContentView(R.layout.progress_dialog_transparent);
       pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       pDialog.setCancelable(false);
       pDialog.show();
category=intent.getStringExtra(Constants.DRINK_CATEGORY);
spinner.setText(category);
       DrinkUtils.getInstance().getDrinksByCategory(category, new FetchCallBacks() {
           @Override
           public void onFetch(List<Offer> offers) {
               pDialog.dismiss();
offerList.clear();
offerList.addAll(offers);
if (offerList.size()<1){
noData.setVisibility(View.VISIBLE);
}
adapter.notifyDataSetChanged();
           }

           @Override
           public void onError(String message) {
               pDialog.dismiss();
Toast.makeText(getApplicationContext(),"An Error Occured",Toast.LENGTH_LONG).show();
               noData.setVisibility(View.VISIBLE);

           }
       });

   }

   if (isSearch){
       Dialog pDialog=new Dialog(ViewSearchActivity.this);
       pDialog.setContentView(R.layout.progress_dialog_transparent);
       pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       pDialog.setCancelable(false);
       pDialog.show();
       searchTerm=intent.getStringExtra(Constants.SEARCH_KEY);
DrinkUtils.getInstance().getDrinksBySearch(searchTerm, new FetchCallBacks() {
    @Override
    public void onFetch(List<Offer> offers) {
        pDialog.dismiss();
        offerList.clear();
        offerList.addAll(offers);
        if (offerList.size()<1){
            noData.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String message) {
        pDialog.dismiss();
        Toast.makeText(getApplicationContext(),"An Error Occured",Toast.LENGTH_LONG).show();
        noData.setVisibility(View.VISIBLE);
    }
});
   }


   sortView.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           String[] sortOpt=new String[]{"Highest Price First","Lowest Price First","Highest Discount First","Lowest Discount First"};
           AlertDialog.Builder builder=new AlertDialog.Builder(ViewSearchActivity.this);
           builder.setSingleChoiceItems(sortOpt, 0, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                   Timber.e(String.valueOf(which));
                   if (which==0){
                       sortList(SortParams.PRICE_ASC);
                   }
                   if (which==1){
                       sortList(SortParams.PRICE_DES);
                   }
                   if (which==2){
                       sortList(SortParams.DISCOUNT_ASC);
                   }
                   if (which==3){
                       sortList(SortParams.DISCOUNT_DESC);
                   }
               }
           });
           Dialog dialog=builder.create();
           dialog.show();
       }
   });

   filterText.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           View view= LayoutInflater.from(ViewSearchActivity.this).inflate(R.layout.filter_layout,null,false);
           AppCompatSeekBar minSeekBar, maxSeekBar;
           TextInputEditText minInput, maxInput;
           MaterialButton filterButton, cancelButton;

           minSeekBar=view.findViewById(R.id.min_seek);
           maxSeekBar=view.findViewById(R.id.max_seek);
           minInput=view.findViewById(R.id.minInput);
           maxInput=view.findViewById(R.id.maxInput);
           filterButton=view.findViewById(R.id.filter);
           cancelButton=view.findViewById(R.id.cancel);

           Dialog dialog=new Dialog(ViewSearchActivity.this);
           dialog.setContentView(view);

           minSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
               @Override
               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                   minInput.setText(String.valueOf(progress));
               }

               @Override
               public void onStartTrackingTouch(SeekBar seekBar) {

               }

               @Override
               public void onStopTrackingTouch(SeekBar seekBar) {

               }
           });

           maxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
               @Override
               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                   maxInput.setText(String.valueOf(progress));
               }

               @Override
               public void onStartTrackingTouch(SeekBar seekBar) {

               }

               @Override
               public void onStopTrackingTouch(SeekBar seekBar) {

               }
           });

           cancelButton.setOnClickListener(v1 -> dialog.dismiss());

           filterButton.setOnClickListener(v12 -> {
               int min, max;
               try{
                   min=Integer.parseInt(minInput.getText().toString());
                   max=Integer.parseInt(maxInput.getText().toString());
               }catch (Exception e){
                   min=50;
                   max=1000;

               }
               adapter.getFilter().filter("filter__"+min+"__"+max);
               if (dialog.isShowing())dialog.dismiss();
           });

           dialog.show();





       }
   });

   searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
       @Override
       public boolean onQueryTextSubmit(String query) {
           return false;
       }

       @Override
       public boolean onQueryTextChange(String newText) {
           String quer="search__"+newText;
           adapter.getFilter().filter(quer);
           return false;
       }
   });

   setupSpinner();
    }

    private void setupSpinner() {
         spinner.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
AlertDialog.Builder builder=new AlertDialog.Builder(ViewSearchActivity.this)
        .setItems(getResources().getStringArray(R.array.categories), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
intent.putExtra(Constants.IS_CATEGORY,true);
intent.putExtra(Constants.DRINK_CATEGORY,getResources().getStringArray(R.array.categories)[which]);
category=getResources().getStringArray(R.array.categories)[which];
spinner.setText(category);
dialog.dismiss();
                Dialog pDialog=new Dialog(ViewSearchActivity.this);
                pDialog.setContentView(R.layout.progress_dialog_transparent);
                pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pDialog.setCancelable(false);
                pDialog.show();
                category=intent.getStringExtra(Constants.DRINK_CATEGORY);
                spinner.setText(category);
                DrinkUtils.getInstance().getDrinksByCategory(category, new FetchCallBacks() {
                    @Override
                    public void onFetch(List<Offer> offers) {
                        pDialog.dismiss();
                        offerList.clear();
                        offerList.addAll(offers);
                        if (offerList.size()<1){
                            noData.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String message) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"An Error Occured",Toast.LENGTH_LONG).show();
                        noData.setVisibility(View.VISIBLE);

                    }
                });
            }
        });
Dialog dialog=builder.create();
dialog.show();
             }
        });

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }


    private void sortList(int priceAsc) {
       adapter.sortList(priceAsc);
    }

    private void addToCart(String itemImage, String itemName, int itemPrice) {
        AlertDialog.Builder builder=new AlertDialog.Builder(ViewSearchActivity.this);
        builder.setMessage("Add This Drink to Cart?")
                .setPositiveButton("Add", (dialog, which) -> {
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
                    thisCart.setCategory(category);
                    CartHelper.addItemToCart(thisCart);
                    refreshCart();

                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
        ;
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
       if (cartCounter!=null)refreshCart();
    }

    private void refreshCart() {
        cartCounter.setText(String.valueOf(CartHelper.getCartSize()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){

            startActivity(new Intent(ViewSearchActivity.this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}