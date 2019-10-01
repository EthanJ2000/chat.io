package com.example.chatio;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class AddContactsFragment extends Fragment
{
	Button btnAdd;
	String newEmail;
	TextView txtUserSearch;
	TextView txtEmailSearch;
	ProgressBar progressBar;
	CircleImageView searchedProfilePicture;
	StorageReference searchStorageReference;
	DatabaseReference mDatabase;
	String userSearch;
	String emailSearch;
	
	public AddContactsFragment()
	{
		// Required empty public constructor
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_contacts, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		mDatabase = FirebaseDatabase.getInstance().getReference();
		searchedProfilePicture = getView().findViewById(R.id.searchedProfilePicture);
		btnAdd = getView().findViewById(R.id.btnAdd);
		Dashboard.txtTitle.setText("Add Contacts");
		progressBar = getView().findViewById(R.id.progressBar);
		txtUserSearch = getView().findViewById(R.id.txtUserSearch);
		txtEmailSearch = getView().findViewById(R.id.txtEmailSearch);
		LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
		View mView = layoutInflaterAndroid.inflate(R.layout.dialog_box_layout, null);
		AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext(),
				R.style.dialogTheme);
		alertDialogBuilderUserInput.setView(mView);
		
		
		btnAdd.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				final String userID = Login.auth.getUid();
				DatabaseReference mContacts = mDatabase.child("users").child(userID).child(
						"contacts");
				
				
				mContacts.addListenerForSingleValueEvent(new ValueEventListener()
				{
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot)
					{
						if (dataSnapshot.child(encodeUserEmail(emailSearch)).exists())
						{
							Log.i(TAG, "onDataChange: already exists");
						}
						else
						{
							Log.i(TAG, "onDataChange: doesnt exist");
							mDatabase.child("users").child(userID).child("contacts").child(emailSearch).setValue(userSearch);
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError)
					{
						Log.e(TAG, "onCancelled: ", databaseError.toException());
					}
				});
				
				emailSearch = encodeUserEmail(emailSearch);


//                mDatabase.child("users").child(userID).child("contacts").push().setValue
//                (newContact);
				SelectContactsFragment.noContactsDisplay.setVisibility(View.GONE);
				SelectContactsFragment selectContactsFragment = new SelectContactsFragment();
				FragmentTransaction fragmentTransaction =
						Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.cardView, selectContactsFragment);
				fragmentTransaction.commit();
			}
		});
		
		final EditText userInputDialogEditText = mView.findViewById(R.id.userInputDialog);
		alertDialogBuilderUserInput
				.setCancelable(false)
				.setPositiveButton(R.string.Search, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialogBox, int id)
					{
						progressBar.setVisibility(View.VISIBLE);
						newEmail = userInputDialogEditText.getText().toString();
						searchStorageReference =
								FirebaseStorage.getInstance().getReference().child(newEmail).child("ProfilePicture");
						//Load New Contact Profile Picture
						
						searchStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
						{
							@Override
							public void onSuccess(Uri uri)
							{
								String url = uri.toString();
								Glide.with(getContext()).load(url).into(searchedProfilePicture);
								Log.i(ContentValues.TAG, "onSuccess: " + url);
							}
						}).addOnFailureListener(new OnFailureListener()
						{
							@Override
							public void onFailure(@NonNull Exception e)
							{
								Log.i(ContentValues.TAG, "getDownloadUrl() Failed");
							}
						});
						
						
						Log.i(TAG, "onClick: " + newEmail);
						getAllContacts(newEmail);
						
					}
				})
				
				.setNegativeButton(R.string.Cancel,
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialogBox, int id)
							{
								dialogBox.cancel();
								SelectContactsFragment selectContactsFragment = new SelectContactsFragment();
								FragmentTransaction fragmentTransaction =
										getActivity().getSupportFragmentManager().beginTransaction();
								fragmentTransaction.replace(R.id.cardView, selectContactsFragment);
								fragmentTransaction.commit();
							} 
						});
		
		AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
		
		
		alertDialogAndroid.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View v = getActivity().getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);
		alertDialogAndroid.show();
		
		super.onViewCreated(view, savedInstanceState);
	}
	
	
	public String getAllContacts(final String email)
	{
		
		Login.mDatabase.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				for (DataSnapshot dsp : dataSnapshot.getChildren())
				{
					User user1 = dsp.getValue(User.class);
					if (user1.email.equals(email))
					{
						StringBuilder name = new StringBuilder(user1.username.toLowerCase());
						String[] nameArray = name.toString().split(" ");
						name = new StringBuilder();
						for (String item : nameArray)
						{
							name.append(String.valueOf(item.charAt(0)).toUpperCase()).append(item.substring(1)).append(" ");
						}
						userSearch = name.toString();
						emailSearch = user1.getEmail();
						txtUserSearch.setText(userSearch);
						txtEmailSearch.setText(emailSearch);
						searchedProfilePicture.setVisibility(View.VISIBLE);
						
						if (emailSearch.equals(Login.auth.getCurrentUser().getEmail()))
						{
							btnAdd.setVisibility(View.GONE);
						}
						else
						{
							btnAdd.setVisibility(View.VISIBLE);
						}
					}
					Log.i(TAG, "onDataChange: " + user1.getUsername());
					progressBar.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
				Toast.makeText(getContext(), "Search Failed", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "onCancelled: " + databaseError.toString());
				progressBar.setVisibility(View.GONE);
			}
		});
		
		return null;
	}
	
	static String encodeUserEmail(String userEmail)
	{
		return userEmail.replace(".", ",");
	}
	
	static String decodeUserEmail(String userEmail)
	{
		return userEmail.replace(",", ".");
	}
}
