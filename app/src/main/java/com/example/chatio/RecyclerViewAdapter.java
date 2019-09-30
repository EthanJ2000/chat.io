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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Viewholder>
{
	private static final String TAG = "RecyclerViewAdapter";
	private ArrayList<String> mUsernames;
	private ArrayList<String> mEmails;
	private Context context;
	private DatabaseReference mDatabase;
	FirebaseAuth auth;
	
	public RecyclerViewAdapter(ArrayList<String> mUsernames, ArrayList<String> mEmails,
	                           Context context)
	{
		this.mUsernames = mUsernames;
		this.mEmails = mEmails;
		this.context = context;
	}
	
	@NonNull
	@Override
	public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent,
				false);
		Viewholder viewholder = new Viewholder(view);
		return viewholder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull final Viewholder holder, final int position)
	{
		Log.d(TAG, "onBindViewHolder: called");
		
		StorageReference reference =
				FirebaseStorage.getInstance().getReference().child(mEmails.get(position)).child(
						"ProfilePicture");
		
		final String[] url = new String[1];
		
		reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
		{
			@Override
			public void onSuccess(Uri uri)
			{
				url[0] = uri.toString();
				Glide.with(RecyclerViewAdapter.this.context).load(url[0]).into(holder.profilePicture);
			}
		}).addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Log.i(TAG, "getDownloadUrl() Failed");
			}
		});
		
		holder.userName.setText(mUsernames.get(position));
		holder.email.setText(mEmails.get(position));
		holder.parent_layout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Log.d(TAG,
						"onClick: clicked on " + mUsernames.get(position) + " " + mEmails.get(position));
				Dashboard.instance.switchToChat(mUsernames.get(position), url[0]);
				
				mDatabase = FirebaseDatabase.getInstance().getReference();
				auth = FirebaseAuth.getInstance();
				String userid = auth.getCurrentUser().getUid();
				mDatabase.child("users").child(userid).child("recents").child(mUsernames.get(position)).setValue(mEmails.get(position));
				
			}
		});
		
		
		
		
	}
	
	public int getItemCount()
	{
		return mUsernames.size();
	}
	
	public class Viewholder extends RecyclerView.ViewHolder
	{
		CircleImageView profilePicture;
		TextView userName;
		TextView email;
		ConstraintLayout parent_layout;
		
		public Viewholder(@NonNull View itemView)
		{
			super(itemView);
			profilePicture = itemView.findViewById(R.id.listProfilePicture);
			userName = itemView.findViewById(R.id.listUsername);
			email = itemView.findViewById(R.id.listEmail);
			parent_layout = itemView.findViewById(R.id.recentchat_parent_layout);
		}
	}
}
