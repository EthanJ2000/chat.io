package com.example.chatio;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;


public class SettingsFragment extends Fragment {
    TextView txtUsernameSettings;
    TextView txtEmailSettings;
    static String username;
    static String email;
    ProgressBar progressBar;
    CircleImageView displayPicture;
    FirebaseAuth auth;
    public static ProfileFragment profileFragment;
    private DatabaseReference mDatabase;
    public static StorageReference storageReference;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Dashboard.txtTitle.setText("Settings");
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = getView().findViewById(R.id.progressBar);
        txtUsernameSettings = getView().findViewById(R.id.txtUsernameSettings);
        txtEmailSettings = getView().findViewById(R.id.txtEmailSettings);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        setCurrentUserInfo();


        displayPicture = getView().findViewById(R.id.displayPicture);
        displayPicture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        displayPicture.animate().alpha(0.5f).setDuration(1);

                        profileFragment = new ProfileFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.cardView, profileFragment);
                        fragmentTransaction.commit();

                        break;
                    case MotionEvent.ACTION_UP:
                        displayPicture.animate().alpha(1).setDuration(1);
                        break;
                }
                return true;
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public void setCurrentUserInfo() {
        final String currentEmail = auth.getCurrentUser().getEmail();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user1 = dsp.getValue(User.class);
                    if (user1.email.equals(currentEmail)) {
                        StringBuilder name = new StringBuilder(user1.username.toLowerCase());
                        String[] nameArray = name.toString().split(" ");
                        name = new StringBuilder();
                        for (String item : nameArray) {
                            name.append(String.valueOf(item.charAt(0)).toUpperCase()).append(item.substring(1)).append(" ");
                        }
                        username = name.toString();
                        email = currentEmail;
                        storageReference = FirebaseStorage.getInstance().getReference().child(email).child("ProfilePicture");
                        txtUsernameSettings.setText(username);
                        txtEmailSettings.setText(email);
                        loadProfilePicture();
                        displayPicture.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });


    }

    public void loadProfilePicture() {
//        Glide.with(this).load(storageReference).into(displayPicture);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Glide.with(getContext()).load(url).into(displayPicture);
                Log.i(TAG, "onSuccess: "+url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "getDownloadUrl() Failed");
            }
        });

    }

}
