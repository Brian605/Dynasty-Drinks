package com.returno.dynasty.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.returno.dynasty.R;
import com.returno.dynasty.adapters.OffersAdapter;
import com.returno.dynasty.admin.utils.FetchUtils;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.cart.Cart;
import com.returno.dynasty.cart.CartHelper;
import com.returno.dynasty.models.Offer;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class AllOffersFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Offer> offerList;
    private OffersAdapter adapter;
    private MaterialToolbar toolbar;
    private TextView cartCounter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
          View root = inflater.inflate(R.layout.all_offers_fragment, container, false);
          recyclerView=root.findViewById(R.id.listView);
          offerList=new ArrayList<>();
          recyclerView.setHasFixedSize(true);
          recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
          toolbar=root.findViewById(R.id.toolbar);
         AppCompatActivity activity=(AppCompatActivity) getActivity();
        ((MainActivity)getActivity()).setToolbar(toolbar);

         toolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 activity.onBackPressed();
                 activity.recreate();
             }
         });

         adapter=new OffersAdapter(getActivity(), offerList, new ClickListener() {
             @Override
             public void onClick(String itemName, String itemImage, int itemPrice) {
addToCart(itemImage,itemName,itemPrice);
             }
         });

         recyclerView.setAdapter(adapter);
         fetchOffers();

        return root;
    }


    private void addToCart(String itemImage, String itemName, int itemPrice) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
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

    private void fetchOffers() {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
       new FetchUtils().getAllOffers(new FetchCallBacks() {
           @Override
           public void onFetch(List<Offer> offers) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       offerList.addAll(offers);
                       adapter.notifyDataSetChanged();
                       pDialog.dismiss();
                   }
               });
           }

           @Override
           public void onError(String message) {
               Timber.e(message);
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       pDialog.dismiss();
                       Toast.makeText(getActivity(),"An Error Occured",Toast.LENGTH_LONG).show();
                   }
               });
           }
       });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu,menu);

        MenuItem item=menu.findItem(R.id.searchView);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.search_bg_white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                String quer="search__"+newText;
                adapter.getFilter().filter(quer);
                return false;

            }
        });

        MenuItem cartMenu=menu.findItem(R.id.cart);
        cartMenu.setActionView(R.layout.cart_icon);
        RelativeLayout relativeLayout=(RelativeLayout)cartMenu.getActionView();
        cartCounter=(TextView)relativeLayout.findViewById(R.id.cartCounter);
        cartCounter.setText("0");
        cartCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartHelper.getCartSize()<1){
                    Toast.makeText(getActivity(),"The cart is empty",Toast.LENGTH_LONG).show();
                    return;
                }
                startActivity(new Intent(getActivity(),CartActivity.class));
            }
        });
        Animation animation= AnimationUtils.loadAnimation(getActivity(),R.anim.zoom_out);
        cartCounter.setAnimation(animation);
        cartCounter.startAnimation(animation);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshCartStatus() {
        cartCounter.setText(String.valueOf(CartHelper.getCartSize()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.cart){
            if (CartHelper.getCartSize()<1){
                Toast.makeText(getActivity(),"The cart is empty",Toast.LENGTH_LONG).show();
                return false;
            }
            startActivity(new Intent(getActivity(),CartActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

}