package com.example.chatio;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReceivedRecyclerViewAdapter extends RecyclerView.Adapter<ReceivedRecyclerViewAdapter.Viewholder>
{
	private ArrayList<String> arrReceivedMessages;
	private static final String TAG = "ReceivedRecyclerViewAda";
	
	public ReceivedRecyclerViewAdapter(ArrayList<String> arrReceivedMessages, Context context)
	{
		this.arrReceivedMessages = arrReceivedMessages;
	}
	
	@NonNull
	@Override
	public ReceivedRecyclerViewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent,
	                                                                 int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbubble_received,
				parent, false);
		Viewholder viewholder = new Viewholder(view);
		return viewholder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull ReceivedRecyclerViewAdapter.Viewholder holder,
	                             final int position)
	{
		holder.receivedMessage.setText(arrReceivedMessages.get(position));
		holder.receivedchatbubble_parentlayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Log.d(TAG, "onClick: clicked on " + arrReceivedMessages.get(position));
			}
		});
	}
	
	@Override
	public int getItemCount()
	{
		return arrReceivedMessages.size();
	}
	
	public class Viewholder extends RecyclerView.ViewHolder
	{
		TextView receivedMessage;
		RelativeLayout receivedchatbubble_parentlayout;
		
		public Viewholder(@NonNull View itemView)
		{
			super(itemView);
			receivedMessage = itemView.findViewById(R.id.receivedMessage);
			receivedchatbubble_parentlayout =
					itemView.findViewById(R.id.receivedchatbubble_parentlayout);
		}
	}
}
