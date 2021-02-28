package com.bioscope_vala.tekcrux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FirstTimeLoginActivity extends AppCompatActivity {

    DatabaseReference mReference;

    private Button done;
    private EditText username;
    static boolean checkUsernameUpload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        done = findViewById(R.id.done);
        username = findViewById(R.id.username);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals("")) {
                    username.setError("You might wanna fill them first ?");
                } else {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", user.getDisplayName());
                    hashMap.put("id", user.getUid());
                    hashMap.put("username", username.getText().toString());
                    hashMap.put("search", username.getText().toString());
                    hashMap.put("points", "0");
                    hashMap.put("profileImage", user.getPhotoUrl().toString());
                    if (getIntent().getStringExtra("provider").equals("google")) {
                        hashMap.put("provider", "google");
                    } else {
                        hashMap.put("provider", "facebook");
                    }
                    mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(FirstTimeLoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                startActivity(intent);
                            }
                        }
                    });
                    checkUsernameUpload = true;
                }
            }
        });
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (!checkUsernameUpload) {
//            Log.d("TekCrux", "deleting account");
//            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//
//                        Log.d("TekCrux", "OK! User deleted cuz they did not setup username");
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e("TekCrux", "Could not delete due to : ", e);
//                }
//            });
//            FirebaseAuth.getInstance().signOut();
//
//            GoogleSignInOptions gso = new GoogleSignInOptions.
//                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
//                    build();
//            GoogleSignInClient googleClient = GoogleSignIn.getClient(FirstTimeLoginActivity.this, gso);
//            googleClient.signOut();
//
//            LoginManager.getInstance().logOut();
//        }
//    }
}