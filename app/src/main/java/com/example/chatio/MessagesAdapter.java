package com.example.chatio;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private final static int SENT_TYPE = 1, RECEIVED_TYPE = 2;
	private ArrayList<Message> arrMessage;
	private static final String TAG = "MessagesAdapter";
	
	
	public MessagesAdapter(ArrayList<Message> arrMessage)
	{
		this.arrMessage = arrMessage;
		
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Log.i(TAG, "onCreateViewHolder: " + viewType);
		if (viewType == SENT_TYPE)
		{
			Log.i(TAG, "onCreateViewHolder: sent inflated");
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbubble_sent,
					parent, false);
			return new SentViewHolder(view);
		}
		else if (viewType == RECEIVED_TYPE)
		{
			Log.i(TAG, "onCreateViewHolder: received inflated");
			View view =
					LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbubble_received,
							parent, false);
			return new RecievedViewHolder(view);
		}
		else
		{
			throw new RuntimeException("Type has to be 1 or 2");
		}
	}
	
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
	{
		int viewType = holder.getItemViewType();
		switch (viewType)
		{
			case SENT_TYPE:
				initSentLayout((SentViewHolder) holder, position);
				break;
			case RECEIVED_TYPE:
				initRecievedLayout((RecievedViewHolder) holder, position);
				break;
			default:
				break;
		}
		
		
	}
	
	private void initSentLayout(SentViewHolder holder, int pos)
	{
		Log.i(TAG, "initSentLayout: sent set");
		holder.sentMessage.setText(arrMessage.get(pos).getMessage());
	}
	
	private void initRecievedLayout(RecievedViewHolder holder, int pos)
	{
		Log.i(TAG, "initRecievedLayout: receieved set");
		holder.receivedMessage.setText(arrMessage.get(pos).getMessage());
	}
	
	@Override
	public int getItemCount()
	{
		return arrMessage.size();
	}
	
	@Override
	public int getItemViewType(int position)
	{
		Collections.sort(arrMessage, new Comparator<Message>()
		{
			@Override
			public int compare(Message message, Message t1)
			{
				return (int)(message.getTimestamp() - t1.getTimestamp());
			}
		});
		
		Message message = arrMessage.get(position);
		
		
		if (message.getType() == Message.ItemType.RECEIVED_TYPE)
		{
			return RECEIVED_TYPE;
		}
		else if (message.getType() == Message.ItemType.SENT_TYPE)
		{
			return SENT_TYPE;
		}
		else
		{
			return -1;
		}
	}
	
	public class SentViewHolder extends RecyclerView.ViewHolder
	{
		TextView sentMessage;
		
		public SentViewHolder(@NonNull View itemView)
		{
			super(itemView);
			sentMessage = itemView.findViewById(R.id.sentMessage);
		}
		
	}
	
	
	public class RecievedViewHolder extends RecyclerView.ViewHolder
	{
		
		TextView receivedMessage;
		
		public RecievedViewHolder(@NonNull View itemView)
		{
			super(itemView);
			receivedMessage = itemView.findViewById(R.id.receivedMessage);
		}
	}
	
}


