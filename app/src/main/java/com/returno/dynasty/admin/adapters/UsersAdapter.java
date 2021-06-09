package com.returno.dynasty.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.callbacks.ClickListener;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {
    private Context context;
    private List<User>userList;
    private ClickListener listener;

    public UsersAdapter(Context context, List<User> userList, ClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user=userList.get(position);
        holder.nameView.setText(user.getUserName());
        holder.phoneView.setText(user.getPhoneNumber());
        holder.optionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v,user.getPhoneNumber());
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        MaterialTextView nameView,phoneView,optionsView;
        public UserHolder(@NonNull View view) {
            super(view);
            nameView=view.findViewById(R.id.nameLayout);
            phoneView=view.findViewById(R.id.phoneLayout);
            optionsView=view.findViewById(R.id.more);

        }
    }
}
