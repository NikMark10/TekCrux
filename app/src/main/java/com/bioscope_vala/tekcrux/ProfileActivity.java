package com.bioscope_vala.tekcrux;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText usernameField;
    private EditText emailField;
    private ImageView userProfile;

    private Button saveData;
    private Button cancel;
    private Button changeProfilePhoto;

    private DatabaseReference mReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mFirebaseStorage;

    Uri profileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameField = findViewById(R.id.name);
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        userProfile = findViewById(R.id.userProfile);

        saveData = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        changeProfilePhoto = findViewById(R.id.changeProfilePhoto);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        showAllData(user);

        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReference.child("name").setValue(nameField.getText().toString());
                mReference.child("username").setValue(usernameField.getText().toString());
                mReference.child("email").setValue(emailField.getText().toString());
                if (profileUri != null) {
                    mReference.child("profileImage").setValue(profileUri.toString());
                    uploadProfileImage(user.getUid());
                }
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            profileUri = data.getData();
            userProfile.setImageURI(profileUri);
        }
    }

    private void uploadProfileImage(String userId) {
        mStorageReference = FirebaseStorage.getInstance().getReference("ProfileImages").child(userId);
        mStorageReference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TekCrux", "Image Uploaded to firebase");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TekCrux", "Image failed to upload to firebase");
            }
        });
    }

    private void showAllData(final FirebaseUser user) {
        Log.d("TekCrux", "Showing all data...");
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nameString = dataSnapshot.child("name").getValue(String.class);
                String usernameString = dataSnapshot.child("username").getValue(String.class);
                String emailString = dataSnapshot.child("email").getValue(String.class);
                String profileImage = dataSnapshot.child("profileImage").getValue(String.class);
                nameField.setText(nameString);
                usernameField.setText(usernameString);
                emailField.setText(emailString);
                if (TextUtils.isEmpty(profileImage)) {
                    Log.d("TekCrux", "Does not exist, provider must be G+ or fb: " + String.valueOf(user.getPhotoUrl()));
                    Glide.with(ProfileActivity.this).load(user.getPhotoUrl()).into(userProfile);

                } else if (TextUtils.equals(profileImage, "default")) {
                    Log.d("TekCrux", "Profile Pic is default");
                } else {
                    mStorageReference = FirebaseStorage.getInstance().getReference("ProfileImages").child(user.getUid());
                    Log.d("TekCrux", " reference: " + mStorageReference.toString());
                    mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("TekCrux", "Profile pic exists: " + uri.toString());
                            Glide.with(ProfileActivity.this).load(uri).into(userProfile);
                        }
                    });
                }
                Log.d("TekCrux", "Setting Text");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TekCrux", "Cannot fetch data");
            }
        });
    }
}
