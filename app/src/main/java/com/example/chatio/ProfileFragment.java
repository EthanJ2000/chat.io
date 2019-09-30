package com.example.chatio;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import static android.content.ContentValues.TAG;


public class ProfileFragment extends Fragment {
    TextView profileUsername;
    static RoundedImageView roundedImageView;
    FirebaseStorage storage;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        roundedImageView = getView().findViewById(R.id.roundedImageView);
        Dashboard.btnEditProfilePicture.setVisibility(View.VISIBLE);
        Dashboard.txtTitle.setText("Profile");
        profileUsername = getView().findViewById(R.id.profileUsername);
        profileUsername.setText(SettingsFragment.username);

        SettingsFragment.storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Glide.with(getContext()).load(url).into(roundedImageView);
                Log.i(TAG, "onSuccess: " + url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "getDownloadUrl() Failed");
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
