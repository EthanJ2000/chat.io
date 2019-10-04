package com.example.chatio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MessageListFragment extends Fragment
{
	private static final String TAG = "MessageListFragment";
	ProgressBar messagelistProgress;
	FloatingActionButton fab;
	CardView cardView;
	int numChats;
	static RecentChatAdapter recentChatAdapter;
	private ArrayList<String> mRecentUsernames = new ArrayList<>();
	private ArrayList<String> mRecentEmails = new ArrayList<>();
	private ArrayList<String> mRecentMessages = new ArrayList<>();
	public static SelectContactsFragment selectContactsFragment;
	private DatabaseReference mDatabase;
	FirebaseAuth auth;
	
	
	
	public MessageListFragment()
	{
		// Required empty public constructor
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_message_list, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		
		messagelistProgress = getView().findViewById(R.id.messagelistProgress);
		recentChatAdapter = new RecentChatAdapter(mRecentUsernames, mRecentMessages, mRecentEmails
				, getContext());
		
		mRecentMessages.clear();
		mRecentEmails.clear();
		mRecentUsernames.clear();
		recentChatAdapter.notifyDataSetChanged();
		
		numChats = recentChatAdapter.getItemCount();
		Log.i(TAG, "onViewCreated: " + numChats);
		mDatabase = FirebaseDatabase.getInstance().getReference();
		auth = FirebaseAuth.getInstance();
		String userId = auth.getCurrentUser().getUid();
		
		if (numChats == 0)
		{
			Query query = mDatabase.child("users").child(userId).child("recents");
			ChildEventListener childEventListener = new ChildEventListener()
			{
				@Override
				public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
				{
					if (dataSnapshot.exists())
					{
//                      Log.i(TAG, "onChildAdded: "+dataSnapshot.getKey()+" "+dataSnapshot
//                      .getValue());
						String username = dataSnapshot.getKey();
						String email = dataSnapshot.getValue().toString();
						
						initRecentChats(username, email, "");
						initRecentsRecyclerView();
						messagelistProgress.setVisibility(View.GONE);
						
					}
					
				}
				
				@Override
				public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
				{
				
				}
				
				@Override
				public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
				{
				
				}
				
				@Override
				public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
				{
				
				}
				
				@Override
				public void onCancelled(@NonNull DatabaseError databaseError)
				{
				
				}
			};
			query.addChildEventListener(childEventListener);
			query.addValueEventListener(new ValueEventListener()
			{
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot)
				{
					if (!dataSnapshot.exists())
					{
						messagelistProgress.setVisibility(View.GONE);
					}
				}
				
				@Override
				public void onCancelled(@NonNull DatabaseError databaseError)
				{
				
				}
			});
			
			
		}
		
		Dashboard.txtTitle.setText("Messages");
		//Floating Action Button OnClick (Start)
		fab = getView().findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				selectContactsFragment = new SelectContactsFragment();
				FragmentTransaction fragmentTransaction =
						getActivity().getSupportFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.cardView, selectContactsFragment);
				fragmentTransaction.commit();
			}
		});
		//Floating Action Button OnClick (End)
		super.onViewCreated(view, savedInstanceState);
	}
	
	public void initRecentChats(String username, String email, String message)
	{
		Log.i(TAG, "initRecentChats: called");
		mRecentUsernames.add(username);
		mRecentEmails.add(email);
		mRecentMessages.add(message);
	}
	
	public void initRecentsRecyclerView()
	{
		Log.i(TAG, "initRecentsRecyclerView: called");
		try
		{
			RecyclerView recentChatsRecyclerView =
					getView().findViewById(R.id.recyclerViewRecentChats);
			recentChatsRecyclerView.setAdapter(recentChatAdapter);
			recentChatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		} catch (Exception e)
		{
			Log.e(TAG, "initRecentsRecyclerView: ", e);
		}
		
		
	}
	
	
	
}
