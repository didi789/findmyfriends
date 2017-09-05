package com.sindj.findmyfriends.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sindj.findmyfriends.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PrivateMessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageTextView;
    public TextView timeTextView;
    public LinearLayout messageContent;

    public PrivateMessageViewHolder(View itemView) {
        super(itemView);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
        messageContent = (LinearLayout) itemView.findViewById(R.id.messageContent);
    }

    public void bindToPost(FriendlyMessage friendlyMessage, View.OnClickListener onClickListener) {
        messageTextView.setText(friendlyMessage.getDisplayName() + ": " + friendlyMessage.getText());
        Date date = new Date(friendlyMessage.getTime());
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);
        timeTextView.setText(dateFormatted);
        messageContent.setOnClickListener(onClickListener);
    }
}
