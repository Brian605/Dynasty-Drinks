package com.returno.dynasty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.models.CashBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class CashBackAdapter extends RecyclerView.Adapter<CashBackAdapter.CashBackHolder> implements Filterable {
    private Context context;
    private List<CashBack> offerList;
    private List<CashBack> filterList;
    private ClickListener listener;

    public CashBackAdapter(Context context, List<CashBack> offerList, ClickListener listener) {
        this.context = context;
        this.offerList = offerList;
        this.listener=listener;
        this.filterList=offerList;
    }

    @NonNull
    @Override
    public CashBackAdapter.CashBackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.cashback_item,parent,false);
        return new CashBackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashBackAdapter.CashBackHolder holder, int position) {
        CashBack cashBack=filterList.get(position);
       holder.cashId.setText(cashBack.getCashBackId());
       holder.cashAmount.setText(String.format(Locale.getDefault(),"Ksh.%d", cashBack.getAmount()));
       holder.btnRedeem.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               listener.onClick(cashBack.getCashBackId());
           }
       });
         }


    @Override
    public int getItemCount() {
        return filterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults=new FilterResults();
                ArrayList<CashBack> offers=new ArrayList<>();
                String constraints=constraint.toString();
                Timber.e(constraints);
                Timber.e(String.valueOf(constraints.length()));
                if (constraints.isEmpty() || constraints.equals("search__")){
                    filterResults.count=offerList.size();
                    filterResults.values=offerList;
                    return filterResults;
                }
                if (constraint.length() == 0){
                    filterResults.count=filterList.size();
                    filterResults.values=filterList;
                    return filterResults;

                }else {
                        for (CashBack offer : offerList) {
                            String searchKey=constraint.toString().split("__")[1].toLowerCase();

                            if (offer.getCashBackId().toLowerCase().contains(searchKey) || offer.getPhoneNumber().toLowerCase().equals(searchKey)) {
                                offers.add(offer);

                            }
                        }
                        filterResults.count=offers.size();
                    filterResults.values=offers;
                    }
                return filterResults;
                }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (ArrayList<CashBack>) results.values;
                notifyDataSetChanged();
                if (filterList.isEmpty()){
                    Toast.makeText(context,"No Results match the filter",Toast.LENGTH_LONG).show();
                }
            }
        };
    }



    public static class CashBackHolder extends RecyclerView.ViewHolder {

MaterialTextView cashId, cashAmount;
MaterialButton btnRedeem;
        public CashBackHolder(@NonNull View itemView) {
            super(itemView);
            cashAmount=itemView.findViewById(R.id.cashAmountView);
            cashId=itemView.findViewById(R.id.cashBackIdView);
            btnRedeem=itemView.findViewById(R.id.redeem);

        }
    }

}
