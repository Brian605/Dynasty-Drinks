package com.returno.dynasty.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.returno.dynasty.R;
import com.returno.dynasty.admin.listeners.SelectionListener;
import com.returno.dynasty.admin.models.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder> {
private final Context context;
private final List<Message>messageList;
private final SelectionListener listener;

    public MessagesAdapter(@NonNull Context context, List<Message> messageList, SelectionListener listener) {
        this.context=context;
        this.messageList=messageList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.message_item,parent,false);
       MessageHolder holder=new MessageHolder(view);
       view.setOnClickListener(view12 -> listener.onSelect(messageList.get(holder.getAdapterPosition())));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message=messageList.get(position);
        holder.messageView.setText(message.getMessage());
        holder.timeView.setText(message.getTimeSent());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {
        TextView messageView,timeView;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageView=itemView.findViewById(R.id.message);
            timeView=itemView.findViewById(R.id.timeView);
                }
    }
}
