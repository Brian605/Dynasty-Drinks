package com.returno.dynasty.admin.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.adapters.DeleteAdapter;
import com.returno.dynasty.admin.listeners.DeleteListener;
import com.returno.dynasty.admin.listeners.SelectionListener;
import com.returno.dynasty.admin.utils.FetchUtils;
import com.returno.dynasty.admin.utils.UploadUtils;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.models.Offer;
import com.returno.dynasty.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;


public class DeleteDrinksFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Offer> drinksList;
    private DeleteAdapter adapter;
    private MaterialToolbar toolbar,actionBar;
    private List<Integer> selectedIds;
    private MaterialTextView selectMode;
    private CircleImageView deleteAll;
    private List<View> selectedViews;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
          View root = inflater.inflate(R.layout.fragment_delete, container, false);
          recyclerView=root.findViewById(R.id.listView);
          actionBar=root.findViewById(R.id.toolbar2);
          selectMode=root.findViewById(R.id.selectAll);
          deleteAll=root.findViewById(R.id.deleteSelected);
          drinksList =new ArrayList<>();
          selectedIds=new ArrayList<>();
          selectedViews=new ArrayList<>();

          recyclerView.setHasFixedSize(true);
          recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
          toolbar=root.findViewById(R.id.toolbar);
         AppCompatActivity activity=(AppCompatActivity) getActivity();
         activity.setSupportActionBar(toolbar);
         activity.getSupportActionBar().setTitle("Delete Drinks");

         toolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 activity.onBackPressed();
                 activity.recreate();
             }
         });
         actionBar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 adapter.clearSelection();
                 selectedIds.clear();
                 syncSelectionStat();
             }
         });

         selectMode.setOnClickListener(view -> {
             PopupMenu popupMenu=new PopupMenu(getActivity(),selectMode);
             popupMenu.getMenuInflater().inflate(R.menu.list_selection_menu,popupMenu.getMenu());
             popupMenu.setGravity(Gravity.BOTTOM);
             popupMenu.setOnMenuItemClickListener(item -> {
                 for (Offer offer: drinksList){
                     if (!selectedIds.contains(offer.getOfferId())) {
                         selectedIds.add(offer.getOfferId());
                     }
                 }
                 adapter.selectAll();
                 syncSelectionStat();
                 return true;
             });
             popupMenu.show();
         });

         deleteAll.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 deleteSelectedItems();
             }
         });

         adapter=new DeleteAdapter(getActivity(), drinksList, new SelectionListener() {
             @Override
             public void onSelect(Offer offer,View view) {
                 if (DeleteAdapter.isSelecting){
                     int id=offer.getOfferId();
                     if (selectedIds.contains(id)){
                         selectedIds.remove(Integer.valueOf(id));
                         adapter.unSelect(view);
                         selectedViews.remove(view);
                         syncSelectionStat();
                         return;
                     }
                     selectedIds.add(id);
                     adapter.setSelected(view);
                     selectedViews.add(view);
                     syncSelectionStat();
                 }

             }

             @Override
             public void onSelectIntent(Offer offer,View view) {
                 actionBar.setVisibility(View.VISIBLE);
                 selectedIds.add(offer.getOfferId());
                 adapter.setSelected(view);
                 selectedViews.add(view);
DeleteAdapter.isSelecting =true;
syncSelectionStat();
             }
         });

         recyclerView.setAdapter(adapter);
         fetchDrinks();

        return root;
    }

    private void deleteSelectedItems() {
        if (selectedIds.isEmpty()){
            return;
        }
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

        new UploadUtils(selectedIds, Constants.MODE_DRINKS, new DeleteListener() {
            @Override
            public void onBatchDeleteComplete() {
                //if (getResources()!=null)
                pDialog.dismiss();
                getActivity().recreate();
                Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_LONG).show();


            }

            @Override
            public void onError(String message) {
                pDialog.dismiss();
Toast.makeText(getActivity(),"An Error Occured, Check your connection and try again",Toast.LENGTH_LONG).show();
            }
        }).start();

    }

    private void syncSelectionStat() {
        if (selectedIds.size()<1){
            selectMode.setText("0 Selected");
            actionBar.setVisibility(View.GONE);
            DeleteAdapter.isSelecting=false;
            adapter.clearSelection();
            return;
        }
        selectMode.setText(String.format(Locale.getDefault(),"%d Selected", selectedIds.size()));
    }

    private void fetchDrinks() {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
       new FetchUtils().getAllDrinks(new FetchCallBacks() {
           @Override
           public void onFetch(List<Offer> offers) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       drinksList.addAll(offers);
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
}