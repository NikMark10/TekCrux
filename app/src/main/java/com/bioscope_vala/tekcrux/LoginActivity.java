package com.bioscope_vala.tekcrux;

import android.content.Intent;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView forgotPassword;
    private TextView createNewAccount;
    private SignInButton mGoogleSignInButton;
    private Button logInButton;

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    DatabaseReference mReference;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        logInButton = findViewById(R.id.logInButton);

        forgotPassword = findViewById(R.id.forgotPassword);
        createNewAccount = findViewById(R.id.createNewAccount);

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInButton = findViewById(R.id.google_sign_in_button);

        callbackManager = CallbackManager.Factory.create();

        enableAllButtons();


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 100 || actionId == EditorInfo.IME_NULL) {
                    disableAllButtons();
                    String username = mEmailView.getText().toString();
                    String password = mPasswordView.getText().toString();
                    login(username, password);
                    return true;
                }
                return false;
            }
        });

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtons();
                Log.d("TekCrux", "Click captured");
                Log.d("TekCrux", "Google Intent starting...");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAllButtons();
                String username = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                login(username, password);
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser(v);
            }
        });


    }

    public void createNewUser(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            disableAllButtons();
            mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    Log.d("TekCrux", "onStart() username string: " + username);

                    if (TextUtils.isEmpty(username)) {
                        String provider = dataSnapshot.child("provider").getValue(String.class);
                        Log.d("TekCrux", "onStart() provider string: " + provider);
                        Intent intent;
                        intent = new Intent(LoginActivity.this, FirstTimeLoginActivity.class);
                        assert provider != null;
                        if (provider.equals("google")) {
                            intent.putExtra("provider", "google");
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("TekCrux", "Didn't catch that up, onStart databaseListener failed");
                    Toast.makeText(LoginActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void login(String username, String password) {
        if (username.equals("") || password.equals("")) {
            enableAllButtons();
            return;
        }

        Toast.makeText(this, "Signing in..", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    enableAllButtons();
                    Log.d("TekCrux", "Problem signing in..");
                    showErrorDialog("You sure you typed correctly ?");
                } else {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        // which is in signInWithGoogle, this onActivityResult gets called when that intent closes.
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //    Google sign in here
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("TekCrux", "signInWithCredential Google:success");
                        final FirebaseUser user = mAuth.getCurrentUser();
                        mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String provider = dataSnapshot.child("provider").getValue(String.class);
                                Log.d("TekCrux", "provider string: " + provider);
                                Intent intent;
                                if (TextUtils.isEmpty(provider)) {
                                    intent = new Intent(LoginActivity.this, FirstTimeLoginActivity.class);
                                    intent.putExtra("provider", "google");

                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("provider", "google");
                                    mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TekCrux","uploaded provider in database");
                                            } else {
                                                Log.d("TekCrux", "" + task.getException().getMessage());
                                            }
                                        }
                                    });

                                } else {
                                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("TekCrux", "Didn't catch that up, mReference failed");
                                Toast.makeText(LoginActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.w("TekCrux", "signInWithCredential Google:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (ApiException e) {
            enableAllButtons();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("TekCrux", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            finish();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Log.d("TekCrux", "User does not exists initially. ");
        }
    }

    private void disableAllButtons() {
        mEmailView.setEnabled(false);
        mPasswordView.setEnabled(false);
        forgotPassword.setEnabled(false);
        logInButton.setEnabled(false);
        mGoogleSignInButton.setEnabled(false);
        createNewAccount.setEnabled(false);
    }

    private void enableAllButtons() {
        mEmailView.setEnabled(true);
        mPasswordView.setEnabled(true);
        forgotPassword.setEnabled(true);
        logInButton.setEnabled(true);
        mGoogleSignInButton.setEnabled(true);
        createNewAccount.setEnabled(true);
    }

    private void showErrorDialog(String msg) {
        Log.d("TekCrux", "oops");
        new AlertDialog.Builder(this)
                .setTitle("!o.o")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
