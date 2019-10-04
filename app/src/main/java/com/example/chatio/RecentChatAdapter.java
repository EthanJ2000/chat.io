package com.example.chatio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
	static boolean bSelected = false;
	
	
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
				holder.recentchat_parent_layout.setBackgroundResource(R.color.gray);
			}
		});
		
		holder.recentchat_parent_layout.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View view)
			{
				Log.i(TAG, "onLongClick: "+mRecentUsernames.get(position));
				
				if (!bSelected){
					holder.recentchat_parent_layout.setBackgroundResource(R.color.gray_highlighted);
					//Show new buttons
					Dashboard.btnDeleteChat.setVisibility(View.VISIBLE);
					Dashboard.btnBackOptions.setVisibility(View.VISIBLE);
					
					//Hide old buttons
					Dashboard.btnSettings.setVisibility(View.GONE);
					Dashboard.btnBack.setVisibility(View.GONE);
					
					bSelected = true;
				}
				
				
				return true;
			}
		});
		
		Dashboard.btnDeleteChat.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Log.i(TAG, "onClick: "+mRecentUsernames.get(position)+" deleted");
			}
		});
		
		
		final String chatUsername = mRecentUsernames.get(position);
		FirebaseAuth auth = FirebaseAuth.getInstance();
		final String[] message = new String[1];
		final String currentEmail = auth.getCurrentUser().getEmail();
		DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
		mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				for (DataSnapshot dsp : dataSnapshot.getChildren())
				{
					User user1 = dsp.getValue(User.class);
					if (user1 != null && user1.email.equals(currentEmail))
					{
						StringBuilder name = new StringBuilder(user1.username.toLowerCase());
						String[] nameArray = name.toString().split(" ");
						name = new StringBuilder();
						for (String item : nameArray)
						{
							name.append(String.valueOf(item.charAt(0)).toUpperCase()).append(item.substring(1)).append(" ");
						}
						String username = name.toString();
						
						final String chat1 = username + "- " + chatUsername;
						final String chat2 = chatUsername + "- " + username;
						
						//Start of query 1
						DatabaseReference newRef = FirebaseDatabase.getInstance().getReference();
						Query query1 = newRef.child("chats").child(chat1).orderByKey().limitToLast(1);
						query1.addChildEventListener(new ChildEventListener()
						{
							@Override
							public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
							                         @Nullable String s)
							{
								if (dataSnapshot.exists()){
									Log.i(TAG, "--------------------");
									Log.i(TAG, "query 1 exists");
									Log.i(TAG, "chat1: "+chat1);
									
									for (DataSnapshot child : dataSnapshot.getChildren()){
										if (child.getKey().equals("message")){
											Log.i(TAG, "onChildAdded1: "+child.getValue());
											holder.recentMessage.setText(child.getValue(String.class));
											holder.recentchat_parent_layout.setBackgroundResource(R.color.gray_highlighted);
											try
											{
												Thread.sleep(1000);
											}
											catch(InterruptedException ex)
											{
												Thread.currentThread().interrupt();
											}
											holder.recentchat_parent_layout.setBackgroundResource(R.color.gray);
										}
									}
									
								}
							}
							
							@Override
							public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
							                           @Nullable String s)
							{
							
							}
							
							@Override
							public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
							{
							
							}
							
							@Override
							public void onChildMoved(@NonNull DataSnapshot dataSnapshot,
							                         @Nullable String s)
							{
							
							}
							
							@Override
							public void onCancelled(@NonNull DatabaseError databaseError)
							{
							
							}
						});//End of query 1
						
						//Start of query 2
						DatabaseReference newRef2 = FirebaseDatabase.getInstance().getReference();
						Query query2 = newRef2.child("chats").child(chat2).orderByKey().limitToLast(1);
						query2.addChildEventListener(new ChildEventListener()
						{
							@Override
							public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
							                         @Nullable String s)
							{
								if (dataSnapshot.exists()){
									Log.i(TAG, "--------------------");
									Log.i(TAG, "query 2 exists");
									Log.i(TAG, "chat2: "+chat2);
									
									for (DataSnapshot child : dataSnapshot.getChildren()){
										if (child.getKey().equals("message")){
											Log.i(TAG, "onChildAdded2: "+child.getValue());
											holder.recentMessage.setText(child.getValue(String.class));
										}
									}
									
								}
							}
							
							@Override
							public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
							                           @Nullable String s)
							{
							
							}
							
							@Override
							public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
							{
							
							}
							
							@Override
							public void onChildMoved(@NonNull DataSnapshot dataSnapshot,
							                         @Nullable String s)
							{
							
							}
							
							@Override
							public void onCancelled(@NonNull DatabaseError databaseError)
							{
							
							}
						});//End of query 2
						
					}
				}
				
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
				Log.e(TAG, "onCancelled: ", databaseError.toException());
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
			itemView.setClickable(true);
			recentProfilePicture = itemView.findViewById(R.id.recentProfilePicture);
			recentUsername = itemView.findViewById(R.id.recentUsername);
			recentMessage = itemView.findViewById(R.id.recentMessage);
			recentchat_parent_layout = itemView.findViewById(R.id.recentchat_parent_layout);
		}
	}
	
	
	
	
	
}
