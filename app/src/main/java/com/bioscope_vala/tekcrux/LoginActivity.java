package com.bioscope_vala.tekcrux;

import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmailView;
    private EditText mPasswordView;
    SignInButton mSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mSignInButton = findViewById(R.id.sign_in_button);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mEmailView = (EditText) findViewById(R.id.Email);
        mPasswordView = (EditText) findViewById(R.id.Password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 100 || actionId == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d("TekCrux","onStart running, checking for existing user.");
        updateUI(account);
    }

    public void onClick(View v) {
        Log.d("TekCrux","Click captured");
        Log.d("TekCrux","Google Intent starting...");
        signInWithGoogle();
    }

    private void login() {
        String username = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if(username.equals("") || password.equals("")) return;

        Toast.makeText(this, "Signing in..", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()) {
                    Log.d("TekCrux", "Problem signing in..");
                    showErrorDialog("You sure you typed correctly ?");
                } else {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    public void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d("TekCrux","hope it worked...");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TekCrux", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            finish();
            startActivity(intent);
        } else {
            Log.d("TekCrux","User does not exists initially. ");
        }
    }

    private void showErrorDialog (String msg) {
        Log.d("TekCrux","oops");
        new AlertDialog.Builder(this)
                .setTitle("!o.o")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
