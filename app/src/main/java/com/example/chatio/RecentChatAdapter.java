package com.example.chatio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.Viewholder>
{
	private static final String TAG = "RecentChatAdapter";
	private ArrayList<String> mRecentUsernames;
	private ArrayList<String> mRecentEmails;
	private ArrayList<String> mRecentMessages;
	private Context context;
	
	public RecentChatAdapter(ArrayList<String> mRecentUsernames, ArrayList<String> mRecentMessages,ArrayList<String> mRecentEmails, Context context)
	{
		this.mRecentUsernames = mRecentUsernames;
		this.mRecentEmails = mRecentEmails;
		this.mRecentMessages = mRecentMessages;
		this.context = context;
		
	}
	
	@NonNull
	@Override
	public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat,parent,false);
		Viewholder viewholder = new Viewholder(view);
		return viewholder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull final Viewholder holder, final int position)
	{
		Log.i(TAG, "onBindViewHolder: RecentChatAdapter called");
		StorageReference reference = FirebaseStorage.getInstance().getReference().child(mRecentEmails.get(position)).child("ProfilePicture");
		
		
		final String[] url = new String[1];
		
		reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
		{
			@Override
			public void onSuccess(Uri uri)
			{
				url[0] = uri.toString();
				Glide.with(RecentChatAdapter.this.context).load(url[0]).into(holder.recentProfilePicture);
			}
		}).addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Log.i(TAG, "getDownloadUrl() Failed");
			}
		});
		
		holder.recentUsername.setText(mRecentUsernames.get(position));
		holder.recentMessage.setText(mRecentMessages.get(position));
		holder.recentchat_parent_layout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Log.i(TAG, "Clicked on: "+mRecentUsernames.get(position));
				Dashboard.instance.switchToChat(mRecentUsernames.get(position),url[0]);
			}
		});
		
		
	}
	
	
	@Override
	public int getItemCount()
	{
		return mRecentUsernames.size();
	}
	
	public class Viewholder extends RecyclerView.ViewHolder{
		CircleImageView recentProfilePicture;
		TextView recentUsername;
		TextView recentMessage;
		ConstraintLayout recentchat_parent_layout;
		
		public Viewholder(@NonNull View itemView)
		{
			super(itemView);
			recentProfilePicture = itemView.findViewById(R.id.recentProfilePicture);
			recentUsername = itemView.findViewById(R.id.recentUsername);
			recentMessage = itemView.findViewById(R.id.recentMessage);
			recentchat_parent_layout = itemView.findViewById(R.id.recentchat_parent_layout);
		}
	}
	
}
