package com.example.chatio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity
{
	ProgressBar chatProgress;
	CardView chatCardView;
	Button btnBack;
	ChatEditText messageEntry;
	RelativeLayout chatbubble_container;
	RelativeLayout relativeLayoutChat;
	CircleImageView userProfilePicture;
	FloatingActionButton fab;
	RecyclerView sentMessages;
	SentRecyclerViewAdapter adapter;
	ReceivedRecyclerViewAdapter receivedAdapter;
	private static final String TAG = "Chat";
	private ArrayList<String> message = new ArrayList<>();
	private ArrayList<String> arrReceivedMessages = new ArrayList<>();
	TextView userName;
	String currentChat;
	private DatabaseReference mDatabaseRef;
	private DatabaseReference mDatabase;
	private FirebaseAuth auth;
	public String CurrentUsername;
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Window w = getWindow();
		w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		chatProgress = findViewById(R.id.chatProgress);
		chatCardView = findViewById(R.id.chatCardView);
		mDatabase = FirebaseDatabase.getInstance().getReference("users");
		auth = FirebaseAuth.getInstance();
		mDatabaseRef = FirebaseDatabase.getInstance().getReference();
		fab = findViewById(R.id.chatFab);
		userName = findViewById(R.id.userName);
		userProfilePicture = findViewById(R.id.userProfilePicture);
		sentMessages = findViewById(R.id.sentMessages);
		
		
		final Intent intent = getIntent();
		final String setUsername = intent.getStringExtra("username");
		String url = intent.getStringExtra("url");
		Log.i(TAG, "onCreate: " + url);
		if (url == null)
		{
			userProfilePicture.setImageResource(R.drawable.default_profile_picture);
		}
		else
		{
			Glide.with(getApplicationContext()).load(url).into(userProfilePicture);
		}
		userName.setText(setUsername);
		
		final String currentEmail = auth.getCurrentUser().getEmail();
		
		mDatabase.addListenerForSingleValueEvent(new ValueEventListener()
		                                         {
			                                         @Override
			                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			                                         {
				                                         for (DataSnapshot dsp :
						                                         dataSnapshot.getChildren())
				                                         {
					                                         User user1 = dsp.getValue(User.class);
					                                         if (user1.email.equals(currentEmail))
					                                         {
						                                         StringBuilder name =
								                                         new StringBuilder(user1.username.toLowerCase());
						                                         String[] nameArray =
								                                         name.toString().split(
										                                         " ");
						                                         name = new StringBuilder();
						                                         for (String item : nameArray)
						                                         {
							                                         name.append(String.valueOf(item.charAt(0)).toUpperCase()).append(item.substring(1)).append(" ");
						                                         }
						                                         CurrentUsername = name.toString();
						                                         currentChat = CurrentUsername +
								                                                       "- " + setUsername;
						                                         String recievedChat =
								                                         setUsername + "- " + CurrentUsername;
						                                         checkForExistingMessages(currentChat, setUsername);
						                                         checkForRecievedMessages(recievedChat, CurrentUsername);
						
						
					                                         }
				                                         }
			                                         }
			
			                                         @Override
			                                         public void onCancelled(@NonNull DatabaseError databaseError)
			                                         {
				                                         Log.e(TAG, "onCancelled: ",
						                                         databaseError.toException());
			                                         }
		                                         }
		);
		
		relativeLayoutChat = findViewById(R.id.relativeLayoutChat);
		relativeLayoutChat.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				hideKeyboard(getCurrentFocus());
				fab.setTranslationY(-0f);
				chatbubble_container.setTranslationY(0f);
				return true;
			}
		});
		chatCardView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				Log.i(TAG, "onTouch: screen touchhed");
				hideKeyboard(getCurrentFocus());
				fab.setTranslationY(-0f);
				chatbubble_container.setTranslationY(0f);
				return true;
			}
		});
		
		
		chatbubble_container = findViewById(R.id.chatbubble_container);
		messageEntry = findViewById(R.id.messageEntry);
		messageEntry.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				messageEntry.requestFocus();
				fab.setTranslationY(-800f);
				chatbubble_container.setTranslationY(-800f);
				return false;
			}
		});
		messageEntry.setKeyImeChangeListener(new ChatEditText.KeyImeChange()
		{
			@Override
			public void onKeyIme(int keyCode, KeyEvent event)
			{
				fab.setTranslationY(-0f);
				chatbubble_container.setTranslationY(0f);
			}
		});
		
		try
		{
			
			fab.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					messageEntry.clearFocus();
					InputMethodManager imm =
							(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					assert imm != null;
					imm.hideSoftInputFromWindow(messageEntry.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					fab.setTranslationY(-0f);
					chatbubble_container.setTranslationY(0f);
					
					
					//Send message to database
					Date date = new Date();
					long time = date.getTime();
					if (!messageEntry.getText().toString().equals(""))
					{
						initSentMessages(messageEntry.getText().toString());
						initSentRecyclerViewAdapter();
						sentMessages.smoothScrollToPosition(adapter.getItemCount());
						
						ChatMessage chatMessage = new ChatMessage(CurrentUsername,
								messageEntry.getText().toString(), setUsername);
						sendToDatabase(chatMessage, time);
						messageEntry.setText("");
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Please type a message",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			
		} catch (Exception e)
		{
			Log.e(TAG, "onCreate: ", e);
		}
		
		
		btnBack = findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				new Dashboard().loadFragment(Chat.this, SelectContactsFragment.class);
				Chat.this.finish();
			}
		});
		
		
	}
	
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		messageEntry.clearFocus();
	}
	
	
	public void hideKeyboard(View view)
	{
		InputMethodManager inputMethodManager =
				(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	private void initReceivedMessages(String message)
	{
		arrReceivedMessages.add(message);
	}
	
	
	private void initReceivedRecyclerViewAdapter()
	{
		RecyclerView receivedRecyclerView = findViewById(R.id.receivedmessages);
		receivedAdapter = new ReceivedRecyclerViewAdapter(arrReceivedMessages, this);
		receivedRecyclerView.setAdapter(receivedAdapter);
		receivedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	}
	
	private void initSentMessages(String editTextMessage)
	{
		message.add(editTextMessage);
	}
	
	private void initSentRecyclerViewAdapter()
	{
		Log.d(TAG, "initRecyclerView: called");
		RecyclerView recyclerView = findViewById(R.id.sentMessages);
		adapter = new SentRecyclerViewAdapter(message, this);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}
	
	public void sendToDatabase(ChatMessage chatMessage, long time)
	{
		String strTime = Long.toString(time);
		mDatabaseRef.child("chats").child(currentChat).child(strTime).setValue(chatMessage);
	}
	
	public void checkForExistingMessages(String currentChat, String setUsername)
	{
		Log.i(TAG, "checkForExistingMessages: called");
		Query query =
				mDatabaseRef.child("chats").child(currentChat).orderByChild("receiver").equalTo(setUsername);
		query.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				if (dataSnapshot.exists())
				{
					for (DataSnapshot child : dataSnapshot.getChildren())
					{
						initSentMessages(child.child("message").getValue().toString());
						initSentRecyclerViewAdapter();
						chatProgress.setVisibility(View.GONE);
					}
				}else {
					chatProgress.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
				
			}
		});
	}
	
	public void checkForRecievedMessages(final String currentChat, final String currentUsername)
	{
		Query query =
				mDatabaseRef.child("chats").child(currentChat).orderByChild("receiver").equalTo(currentUsername);
		query.addChildEventListener(new ChildEventListener()
		{
			@Override
			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
				
				if (dataSnapshot.exists())
				{
					for (DataSnapshot child : dataSnapshot.getChildren())
					{
						if (child.getKey().equals("message"))
						{
							initReceivedMessages(child.getValue(String.class));
							initReceivedRecyclerViewAdapter();
						}
						
					}
				}
				else
				{
					Log.i(TAG, "onDataChange: doesnt exist");
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
		});
		
	}
	
}
