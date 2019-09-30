package com.example.chatio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Login extends AppCompatActivity
{
	Button btnLogin;
	TextView btnSignUp;
	ConstraintLayout loginScreen;
	EditText edtUsernameLogin;
	EditText edtPasswordLogin;
	ProgressBar progressBar;
	String errorCode;
	TextInputLayout textInputLayoutUsername;
	public static FirebaseAuth auth;
	public static DatabaseReference mDatabase;
	private static final String TAG = "Login";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		Window w = getWindow();
		w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mDatabase = FirebaseDatabase.getInstance().getReference("users");
		auth = FirebaseAuth.getInstance();
		
		if (auth.getCurrentUser() != null){
			startActivity(new Intent(Login.this, Dashboard.class));
			Login.this.finish();
		}
		
		progressBar = findViewById(R.id.progressBar);
		btnLogin = findViewById(R.id.btnLogin);
		btnSignUp = findViewById(R.id.btnSignUp);
		loginScreen = findViewById(R.id.signupScreen);
		edtUsernameLogin = findViewById(R.id.edtEmailSignUp);
		edtPasswordLogin = findViewById(R.id.edtPasswordSignUp);
		textInputLayoutUsername = findViewById(R.id.textInputLayoutUsername);
		
		edtUsernameLogin.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					hideKeyboard(v);
				}
			}
		});
		
		edtPasswordLogin.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (!hasFocus)
				{
					hideKeyboard(v);
				}
			}
		});
		
		
	}
	
	public void onLoginClicked(View view)
	{
		
		if (edtUsernameLogin.getText().toString().trim().equals("") || edtPasswordLogin.getText().toString().trim().equals(""))
		{
			Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_LONG).show();
		}
		else
		{
			progressBar.setVisibility(View.VISIBLE);
			String email = edtUsernameLogin.getText().toString();
			String password = edtPasswordLogin.getText().toString();
			
			if (isNetworkAvailable(getApplication()))
			{
				//Handle Login
				
				Login.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							startActivity(new Intent(Login.this, Dashboard.class));
							Login.this.finish();
							progressBar.setVisibility(View.GONE);
						}
						else
						{
							progressBar.setVisibility(View.GONE);
							errorCode =
									((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
							switch (errorCode)
							{
								case "ERROR_WRONG_PASSWORD":
									Toast.makeText(getApplicationContext(), "Invalid Password.",
											Toast.LENGTH_LONG).show();
									break;
								case "ERROR_USER_NOT_FOUND":
									Toast.makeText(getApplicationContext(), "An account with that " +
											                                        "email does " +
											                                        "not exist.",
											Toast.LENGTH_LONG).show();
									break;
							}
							
							Log.i(TAG, "onComplete: " + errorCode);
						}
					}
				});
				
			}
			else
			{
				Toast.makeText(getApplicationContext(), "No Connection. Try Again Later.",
						Toast.LENGTH_LONG).show();
				progressBar.setVisibility(View.GONE);
			}
			
			
		}
		
		
	}
	
	public void onSignUpClicked(View view)
	{
		Toast.makeText(this, "Sign Up Clicked", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(this, SignUp.class));
	}
	
	
	public void hideKeyboard(View view)
	{
		InputMethodManager inputMethodManager =
				(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public static boolean isNetworkAvailable(Context con)
	{
		try
		{
			ConnectivityManager cm = (ConnectivityManager) con
					                                               .getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			
			if (networkInfo != null && networkInfo.isConnected())
			{
				return true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
}

