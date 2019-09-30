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

public class SentRecyclerViewAdapter extends RecyclerView.Adapter<SentRecyclerViewAdapter.Viewholder>
{
	private ArrayList<String> arrMessage;
	private static final String TAG = "SentRecyclerViewAdapter";
	
	public SentRecyclerViewAdapter(ArrayList<String> message, Context context)
	{
		this.arrMessage = message;
	}
	
	@NonNull
	@Override
	
	
	public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbubble_sent, parent, false);
		Viewholder viewholder = new Viewholder(view);
		return viewholder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull Viewholder holder, final int position)
	{
		
		Log.d(TAG, "onBindViewHolder: called");
		
		holder.message.setText(arrMessage.get(position));
		holder.parent_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "onClick: clicked on "+arrMessage.get(position));
			}
		});
		
	}
	
	@Override
	public int getItemCount()
	{
		return arrMessage.size();
	}
	
	public class Viewholder extends RecyclerView.ViewHolder {
		
		TextView message;
		RelativeLayout parent_layout;
		
		public Viewholder(@NonNull View itemView) {
			super(itemView);
			message = itemView.findViewById(R.id.sentMessage);
			parent_layout = itemView.findViewById(R.id.chatbubble_parentlayout);
		}
	}
	
}
