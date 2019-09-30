package com.example.chatio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class SelectContactsFragment extends Fragment
{
	TextView addContactsButton;
	static RelativeLayout noContactsDisplay;
	RecyclerViewAdapter adapter;
	public static AddContactsFragment addContactsFragment;
	private ArrayList<String> mUsernames = new ArrayList<>();
	private ArrayList<String> mEmails = new ArrayList<>();
	DatabaseReference mRef;
	FirebaseAuth newAuth;
	long contactsNum;
	FloatingActionButton fab2;
	
	public SelectContactsFragment()
	{
		// Required empty public constructor
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_select_contacts, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		newAuth = FirebaseAuth.getInstance();
		mRef = FirebaseDatabase.getInstance().getReference("users");
		fab2 = getView().findViewById(R.id.fab2);
		fab2.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				addContactsFragment = new AddContactsFragment();
				FragmentTransaction fragmentTransaction =
						getActivity().getSupportFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.cardView, addContactsFragment);
				fragmentTransaction.commit();
				
				//clear recycler view to avoid printing double
				mEmails.clear();
				mUsernames.clear();
				adapter.notifyDataSetChanged();
			}
		});
		
		try
		{
			if (newAuth.getCurrentUser() != null)
			{
				
				Query query = mRef.child(Objects.requireNonNull(newAuth.getUid())).child(
						"contacts");
				ChildEventListener childEventListener = new ChildEventListener()
				{
					@Override
					public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
					                         @Nullable String s)
					{
						initContacts(Objects.requireNonNull(dataSnapshot.getValue()).toString(),
								AddContactsFragment.decodeUserEmail(Objects.requireNonNull(dataSnapshot.getKey())));
						if (contactsNum != 0)
						{
							noContactsDisplay.setVisibility(View.GONE);
							fab2.show();
						}
						else
						{
							fab2.hide();
						}
						
						initRecyclerView();
						
						
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
				};
				query.addChildEventListener(childEventListener);
				
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "onViewCreated: ", e);
		}
		
		
		noContactsDisplay = getView().findViewById(R.id.noContactsDisplay);
		Dashboard.txtTitle.setText("Select Contacts");
		
		
		addContactsButton = getView().findViewById(R.id.addContactsButton);
		addContactsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				
				addContactsFragment = new AddContactsFragment();
				FragmentTransaction fragmentTransaction =
						getActivity().getSupportFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.cardView, addContactsFragment);
				fragmentTransaction.commit();
				
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void initContacts(String usernames, String emails)
	{
		mUsernames.add(usernames);
		mEmails.add(emails);
	}
	
	private void initRecyclerView()
	{
		RecyclerView recyclerView =
				Objects.requireNonNull(getView()).findViewById(R.id.contactList);
		adapter = new RecyclerViewAdapter(mUsernames, mEmails, getContext());
		contactsNum = adapter.getItemCount();
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
	}
	
}
