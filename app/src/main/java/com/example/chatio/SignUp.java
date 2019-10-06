package com.example.chatio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    EditText edtEmailSignUp;
    EditText edtUsernameSignUp;
    EditText edtPasswordSignUp;
    Button btnSignUp;
    ProgressBar progressBar;
    String errorCode;
    private DatabaseReference mDatabase;

    private static final String TAG = "SignUp";
    //Login.auth


    @Override
    protected void onStart() {
        super.onStart();

        if (Login.auth.getCurrentUser() != null) {
            //handle already login user
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Login.auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtEmailSignUp = findViewById(R.id.edtEmailSignUp);
        edtUsernameSignUp = findViewById(R.id.edtUsernameSignUp);
        edtPasswordSignUp = findViewById(R.id.edtPasswordSignUp);

        edtEmailSignUp.clearFocus();
        edtUsernameSignUp.clearFocus();
        edtPasswordSignUp.clearFocus();

        edtEmailSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        edtUsernameSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        edtPasswordSignUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

    }

    public void onSignedUp(View view) {

        if (edtUsernameSignUp.getText().toString().trim().equals("") || edtPasswordSignUp.getText().toString().trim().equals("") || edtEmailSignUp.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            final String email = edtEmailSignUp.getText().toString().trim();
            String password = edtPasswordSignUp.getText().toString().trim();
            final String username = edtUsernameSignUp.getText().toString().trim();

            Login.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.i(TAG, "onComplete: " + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                Log.i(TAG, "onComplete: " + errorCode);

                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(getApplicationContext(), "Invalid Email.", Toast.LENGTH_LONG).show();
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        Toast.makeText(getApplicationContext(), "Weak Password.", Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext(), "Password must contain at least 6 characters.", Toast.LENGTH_LONG).show();
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(getApplicationContext(), "Email already in use.", Toast.LENGTH_LONG).show();
                                        break;
                                }

                            } else {
                            
//                                StringBuilder name = new StringBuilder(username.toLowerCase());
//                                String[] nameArray = name.toString().split(" ");
//                                name = new StringBuilder();
//                                for (String item : nameArray) {
//                                    name.append(String.valueOf(item.charAt(0)).toUpperCase()).append(item.substring(1)).append(" ");
//                                }
//                                final String newUsername = name.toString();
	
	
	                            User user = new User(username, email);
	                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
	                            writeNewUser(user, userId);
	                            startActivity(new Intent(SignUp.this, Login.class));
	                            SignUp.this.finish();
	                            progressBar.setVisibility(View.GONE);
	                            
                                
    
                                }
                                
                            
                            }


                        });

                    }


        }

        

    private void writeNewUser(User user, String userid) {
        mDatabase.child("users").child(userid).setValue(user);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
