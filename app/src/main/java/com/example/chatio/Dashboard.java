package com.example.chatio;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Dashboard extends AppCompatActivity implements InternetConnectivityListener
{
	static Button btnEditProfilePicture;
	Button btnBack;
	Button btnSettings;
	CardView cardView;
	private static final String TAG = "Dashboard";
	public static MessageListFragment messageListFragment;
	public static SettingsFragment settingsFragment;
	Context context;
	public static TextView txtTitle;
	
	public static Dashboard instance;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		//Change Profile Picture - Getting the Image
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode == 1)
			{
				Uri selectedImage = data.getData();
				try
				{
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
							selectedImage);
					ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageBytes);
					byte[] imageData = imageBytes.toByteArray();
					
					FirebaseStorage.getInstance().getReference().child(SettingsFragment.email)
							.child("ProfilePicture")
							.putBytes(imageData)
							.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
							{
								@Override
								public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
								{
									Log.i(TAG, "Profile Picture Update Successful!");
								}
							});
					
					ProfileFragment.roundedImageView.setImageBitmap(bitmap);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Dashboard.instance = this;
		
		Window w = getWindow();
		w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		Log.i(TAG, "onCreate Dash: "+ FirebaseAuth.getInstance().getCurrentUser());
		
		InternetAvailabilityChecker.init(this);
		InternetAvailabilityChecker mInternetAvailabilityChecker =
				InternetAvailabilityChecker.getInstance();
		mInternetAvailabilityChecker.addInternetConnectivityListener(this);
		
		txtTitle = findViewById(R.id.txtTitle);
		context = Dashboard.this;
		btnBack = findViewById(R.id.btnBack);
		btnSettings = findViewById(R.id.btnSettings);
		btnEditProfilePicture = findViewById(R.id.btnEditProfilePicture);
		cardView = findViewById(R.id.cardView);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		messageListFragment = new MessageListFragment();
		fragmentTransaction.add(R.id.cardView, messageListFragment);
		fragmentTransaction.commit();
		
		btnEditProfilePicture.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//Change Profile Picture - Open Gallery
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				String[] mimeTypes = {"image/jpeg", "image/png"};
				intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
				startActivityForResult(intent, 1);
				
			}
		});
		
		
	}
	
	public void onBtnSettingsClicked(View view)
	{
		try
		{
			settingsFragment = new SettingsFragment();
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.cardView, settingsFragment);
			fragmentTransaction.commit();
			btnSettings.setVisibility(View.GONE);
		} catch (Exception e)
		{
			Log.e(TAG, "onBtnSettingsClicked: ", e);
		}
	}
	
	public Dashboard loadFragment(Context context, Class fragmentClass)
	{
		try
		{
			startActivity(new Intent(context, Dashboard.class));
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.cardView, (Fragment) fragmentClass.newInstance());
			fragmentTransaction.commit();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public void onBackPressed()
	{
		onBackButtonClicked(null);
	}
	
	public void onBackButtonClicked(View view)
	{
		
		if (messageListFragment.isVisible())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogTheme);
			builder.setMessage("Log out?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							Login.auth.getInstance().signOut();
							startActivity(new Intent(context, Login.class));
							finish();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							dialogInterface.cancel();
						}
					}).show();
			
		}
		else if (settingsFragment != null && settingsFragment.isVisible())
		{
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.cardView, messageListFragment);
			fragmentTransaction.commit();
			btnSettings.setVisibility(View.VISIBLE);
			
		}
		else if (MessageListFragment.selectContactsFragment != null && MessageListFragment.selectContactsFragment.isVisible())
		{
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.cardView, messageListFragment);
			fragmentTransaction.commit();
			
		}
		else if (SelectContactsFragment.addContactsFragment != null && SelectContactsFragment.addContactsFragment.isVisible())
		{
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.cardView, MessageListFragment.selectContactsFragment);
			fragmentTransaction.commit();
			
			
		}
		else if (SettingsFragment.profileFragment != null && SettingsFragment.profileFragment.isVisible())
		{
			btnEditProfilePicture.setVisibility(View.GONE);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.cardView, settingsFragment);
			fragmentTransaction.commit();
			
			
		}
		
		
	}
	
	public void switchToChat(String username, String url)
	{
		Intent intent = new Intent(this, Chat.class);
		Bundle extras = new Bundle();
		extras.putString("username", username);
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}
	
	
	
	@Override
	public void onInternetConnectivityChanged(boolean isConnected)
	{
		if (isConnected)
		{
			Toast.makeText(getApplicationContext(), "Connected",
					Toast.LENGTH_LONG).show();
			Log.i(TAG, "onInternetConnectivityChanged: connected");
			
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Internet Connection Lost",
					Toast.LENGTH_LONG).show();
			Log.i(TAG, "onInternetConnectivityChanged: lost connection");
		}
	}
}
