package com.example.chatio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
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
	private static final String TAG = "Chat";
	private ArrayList<Message> arrMessage = new ArrayList<>();
	TextView userName;
	String currentChat;
	private DatabaseReference mDatabaseRef;
	private DatabaseReference mDatabase;
	private FirebaseAuth auth;
	public String CurrentUsername;
	static RecyclerView recyclerMessages;
	MessagesAdapter messagesAdapter;
	String recievedChat;
	
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
		
		
		recyclerMessages = findViewById(R.id.recyclerMessages);
		messagesAdapter = new MessagesAdapter(arrMessage);
		recyclerMessages.setAdapter(messagesAdapter);
		
		
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
						                                         recievedChat =
								                                         setUsername + "- " + CurrentUsername;
						                                         checkForRecievedMessages(recievedChat, CurrentUsername, setUsername, currentChat);
						                                         checkForExistingMessages(currentChat, recievedChat, setUsername, CurrentUsername);
						                                         chatProgress.setVisibility(View.GONE);
						                                         
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
						initSentMessages(messageEntry.getText().toString(), time);
						Log.i(TAG, "onClick: " + messageEntry.getText().toString());
						
						
						ChatMessage chatMessage = new ChatMessage(setUsername, CurrentUsername,
								messageEntry.getText().toString(), time);
						sendToDatabase(chatMessage, time, recievedChat);
						messageEntry.setText("");
						recyclerMessages.smoothScrollToPosition(messagesAdapter.getItemCount());
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
	
	
	private void initSentMessages(String editTextMessage, long timestamp)
	{
		arrMessage.add(new Message(editTextMessage, Message.ItemType.SENT_TYPE, timestamp));
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//		linearLayoutManager.setStackFromEnd(true);
		recyclerMessages.setLayoutManager(linearLayoutManager);
		
	}
	
	private void initReceivedMessages(String message, long timestamp)
	{
		arrMessage.add(new Message(message, Message.ItemType.RECEIVED_TYPE, timestamp));
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//		linearLayoutManager.setStackFromEnd(true);
		recyclerMessages.setLayoutManager(linearLayoutManager);
		
		
	}
	
	public void sendToDatabase(final ChatMessage chatMessage, long time, final String reversedChat)
	{
		final String strTime = Long.toString(time);
		
		Query query = mDatabaseRef.child("chats").child(currentChat);
		query.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot)
			{
				if (dataSnapshot.exists())
				{
					mDatabaseRef.child("chats").child(currentChat).child(strTime).setValue(chatMessage);
				}
				else
				{
					Query query = mDatabaseRef.child("chats").child(reversedChat);
					query.addListenerForSingleValueEvent(new ValueEventListener()
					{
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot)
						{
							if (dataSnapshot.exists())
							{
								mDatabaseRef.child("chats").child(reversedChat).child(strTime).setValue(chatMessage);
							}
							else
							{
								mDatabaseRef.child("chats").child(currentChat).child(strTime).setValue(chatMessage);
							}
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError databaseError)
						{
						
						}
					});
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
			
			}
		});
		
	}
	
	public void checkForExistingMessages(final String currentChat, final String currentChat2,
	                                     final String setUsername, final String currentUsername)
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
						Log.i(TAG, "onChildAddedSEND:" + child);
						String message = child.child("message").getValue(String.class);
						long timestamp = child.child("timestamp").getValue(Long.class);
						Log.i(TAG, "long " + timestamp);
						Log.i(TAG, "onChildAddedSEND: here");
						initSentMessages(message, timestamp);
						Log.i(TAG, "notification created: ");
						
					}
				}
				else
				{
					Query query =
							mDatabaseRef.child("chats").child(currentChat2).orderByChild("receiver"
							).equalTo(setUsername);
					query.addListenerForSingleValueEvent(new ValueEventListener()
					{
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot)
						{
							if (dataSnapshot.exists())
							{
								for (DataSnapshot child : dataSnapshot.getChildren())
								{
									Log.i(TAG, "onChildAddedSEND:" + child);
									String message = child.child("message").getValue(String.class);
									long timestamp = child.child("timestamp").getValue(Long.class);
									Log.i(TAG, "long " + timestamp);
									Log.i(TAG, "onChildAddedSEND: here");
									initSentMessages(message, timestamp);
									Log.i(TAG, "notification created: ");
									
									
								}
							}
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError databaseError)
						{
						
						}
					});
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
			
			}
		});
	}
	
	public void checkForRecievedMessages(final String currentChat, final String currentUsername,
	                                     final String setUsername, final String currentChat2)
	{
		Log.i(TAG, "checkForRecievedMessages: called");
		Log.i(TAG, "checkForRecievedMessages: " + currentUsername);
		Log.i(TAG, "checkForRecievedMessages: " + currentChat);
		Log.i(TAG, "checkForRecievedMessages: " + currentChat2);
		Query query =
				mDatabaseRef.child("chats").child(currentChat).orderByChild("receiver").equalTo(currentUsername);
		query.addChildEventListener(new ChildEventListener()
		{
			@RequiresApi(api = Build.VERSION_CODES.O)
			@Override
			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
				Log.i(TAG, "onChildAdded: called");
				if (dataSnapshot.exists())
				{
					Log.i(TAG, "onChildAdded: exists");
					String message = "";
					for (DataSnapshot child : dataSnapshot.getChildren())
					{
						Log.i(TAG, "onChildKey:" + child.getKey());
						
						if (child.getKey().equals("message") && message.equals(""))
						{
							message = child.getValue(String.class);
						}
						
						if (child.getKey().equals("timestamp"))
						{
							Log.i(TAG, "onChildAdded: here");
							initReceivedMessages(message, child.getValue(Long.class));
							Log.i(TAG, "long " + (child.getValue(Long.class)));
							createNotification(currentUsername, message);
							Log.i(TAG, "notification created: ");
							message = "";
						}
						
					}
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
		
		Query query2 =
				mDatabaseRef.child("chats").child(currentChat2).orderByChild("receiver").equalTo(currentUsername);
		query2.addChildEventListener(new ChildEventListener()
		{
			@RequiresApi(api = Build.VERSION_CODES.O)
			@Override
			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
				Log.i(TAG, "onChildAdded: called");
				if (dataSnapshot.exists())
				{
					Log.i(TAG, "onChildAdded: exists");
					String message = "";
					for (DataSnapshot child : dataSnapshot.getChildren())
					{
						Log.i(TAG, "onChildKey:" + child.getKey());
						
						if (child.getKey().equals("message") && message.equals(""))
						{
							message = child.getValue(String.class);
						}
						
						if (child.getKey().equals("timestamp"))
						{
							Log.i(TAG, "onChildAdded: here");
							initReceivedMessages(message, child.getValue(Long.class));
							Log.i(TAG, "long " + (child.getValue(Long.class)));
							createNotification(currentUsername, message);
							Log.i(TAG, "notification created: ");
							message = "";
						}
						
					}
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
	
	@RequiresApi(api = Build.VERSION_CODES.O)
	public void createNotification(String username, String message)
	{
		String CHANNEL_ID = "my_channel_01";
		CharSequence name = "my_channel";
		String Description = "This is my channel";
		
		int NOTIFICATION_ID = 234;
		
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
		{
			
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel newChannel = new NotificationChannel(CHANNEL_ID, name, importance);
			newChannel.setDescription(Description);
			newChannel.enableVibration(true);
			
			if (notificationManager != null)
			{
				notificationManager.createNotificationChannel(newChannel);
			}
			
			
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
					                                     .setContentTitle(username)
					                                     .setContentText(message)
					                                     .setSmallIcon(R.mipmap.ic_launcher)
					                                     .setPriority(NotificationCompat.PRIORITY_DEFAULT);
			if (notificationManager != null)
			{
				
				notificationManager.notify(NOTIFICATION_ID, builder.build());
			}
			
		}
		
	}
}

