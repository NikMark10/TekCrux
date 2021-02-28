package com.bioscope_vala.tekcrux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private EditText username;
    private EditText email;
    private EditText password;
    private ImageView add;
    private ImageView userProfile;

    Button signUp;

    FirebaseAuth mAuth;

    DatabaseReference mReference;
    StorageReference mStorageReference;

    Uri profileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signUp = findViewById(R.id.signUp);
        add = findViewById(R.id.add);
        userProfile = findViewById(R.id.userProfile);

        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nameText = name.getText().toString();
                final String usernameText = username.getText().toString();
                final String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(emailText) ||
                        TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(RegisterActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else if (passwordText.length() < 6) {
                    password.setError("Password is too short");
                } else {
                    Log.d("TekCrux", "Registering..");
                    register(nameText, usernameText, emailText, passwordText);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            profileUri = data.getData();
            userProfile.setImageURI(profileUri);
        }
    }

    private void register(final String nameText, final String usernameText, String emailText, String passwordText) {
        final String email = emailText;
        mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    final String userId = user.getUid();

                    mReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("name", nameText);
                    hashMap.put("username", usernameText);
                    hashMap.put("email", email);
                    hashMap.put("search", usernameText);
                    hashMap.put("provider", "firebase");
                    hashMap.put("points", "0");
                    if (profileUri != null) {
                        hashMap.put("profileImage", profileUri.toString());
                        uploadProfileImage(userId);
                    } else {
                        hashMap.put("profileImage", "default");
                    }

                    mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                startActivity(intent);
                            } else {
                                Log.d("TekCrux", "" + task.getException().getMessage());
                            }
                        }
                    });

                } else {
                    Log.d("TekCrux", "onComplete: Failed " + task.getException().getMessage());
                    Toast.makeText(RegisterActivity.this, "Sorry, couldn't register now.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}