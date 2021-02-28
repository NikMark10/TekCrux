package com.bioscope_vala.tekcrux;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bioscope_vala.tekcrux.ui.preferences.PreferencesFragment;
import com.bioscope_vala.tekcrux.ui.prepare.PrepareFragment;
import com.bioscope_vala.tekcrux.ui.home.HomeFragment;
import com.bioscope_vala.tekcrux.ui.questionAnswers.QuestionAnswersFragment;
import com.bioscope_vala.tekcrux.ui.settings.SettingsFragment;
import com.bioscope_vala.tekcrux.ui.takeTest.TakeTestFragment;
import com.bioscope_vala.tekcrux.ui.vault.VaultFragment;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String username;
    private String name;
    private String provider;
    private String points;
    private String profileImage;

    private ImageView userProfilePicField;
    private ImageView goToProfile;
    private TextView userNameField;
    private TextView nameField;
    private TextView pointsField;
    private TextView userId;
    private TextView userEmail;
    private TextView answer;
    private TextView answerer;
    private TextView answererPoints;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Fragment bottomFragment, leftNavFragment = null;

    public static final String SHARED_PREFS = "sharedprefs";
    public static final String USERID = "userId";
    public static final String NAME_OF_USER = "name";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PROVIDER = "provider";
    public static final String POINTS = "points";
    public static final String PROFILE_IMAGE = "profileImage";

    GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference mReference;
    private FirebaseAuth mAuth;

    StorageReference mStorageReference;
    FirebaseStorage mFirebaseStorage;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    ProfileTracker profileTracker;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myDb = new DatabaseHelper(HomeActivity.this);
        mFirebaseStorage = FirebaseStorage.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        userNameField = (TextView) headerView.findViewById(R.id.nav_header_username);
        nameField = (TextView) headerView.findViewById(R.id.nav_header_name);
        pointsField = (TextView) headerView.findViewById(R.id.nav_header_points);
        userProfilePicField = headerView.findViewById(R.id.userProfile);
        goToProfile = headerView.findViewById(R.id.goToProfile);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setOnScreenFields();

        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, bottomFragment).commit();

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                setOnScreenFields();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setOnScreenFields() {

        Log.d("TekCrux", "setOnScreenFields called");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        } else {

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            if (TextUtils.equals(sharedPreferences.getString(USERID, ""), user.getUid())) {
                mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("name").getValue(String.class);
                        provider = dataSnapshot.child("provider").getValue(String.class);
                        username = dataSnapshot.child("username").getValue(String.class);
                        points = '(' + dataSnapshot.child("points").getValue(String.class) + ')';
                        profileImage = dataSnapshot.child("profileImage").getValue(String.class);

                        if (TextUtils.isEmpty(username)) {
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                        }

                        Log.d("TekCrux", name + " " + provider + " " + username + " " + points);

                        userNameField.setText(username);
                        nameField.setText(name);
                        pointsField.setText(points);

                        Glide.with(HomeActivity.this).load(profileImage).into(userProfilePicField);
                        Log.d("TekCrux", "loaded");
                        editor.putString(USERID, user.getUid());
                        editor.putString(NAME_OF_USER, name);
                        editor.putString(USERNAME, username);
                        editor.putString(PROVIDER, provider);
                        editor.putString(POINTS, points);
                        editor.putString(PROFILE_IMAGE, profileImage);

                        editor.apply();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TekCrux", "Didn't catch that up, mReference failed");
                    }
                });
            } else {
                Log.d("TekCrux","Using shared pref for setOnScreenFields()," + sharedPreferences.getString(PROFILE_IMAGE,"no profile image"));
                profileImage = sharedPreferences.getString(PROFILE_IMAGE, "");
                userNameField.setText(sharedPreferences.getString(USERNAME, ""));
                nameField.setText(sharedPreferences.getString(NAME_OF_USER, ""));
                pointsField.setText(sharedPreferences.getString(POINTS, ""));
                Glide.with(HomeActivity.this).load(profileImage).into(userProfilePicField);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_preferences:
                leftNavFragment = new PreferencesFragment();

                getSupportFragmentManager().beginTransaction().add(R.id.fragment, leftNavFragment).commit();
                hideHamburgerNavigation();
                hideBottomNavigation();
                break;
            case R.id.nav_settings:
                leftNavFragment = new SettingsFragment();

                getSupportFragmentManager().beginTransaction().add(R.id.fragment, leftNavFragment).commit();
                hideHamburgerNavigation();
                hideBottomNavigation();
                break;
                //**********************************************************************
            case R.id.log_out:
                mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        provider = dataSnapshot.child("provider").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (TextUtils.equals(provider, "google")) {

                    Log.d("TekCrux", "google log out");
                    GoogleSignInOptions gso = new GoogleSignInOptions.
                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                            build();

                    GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(getApplicationContext(),gso);
                    googleSignInClient.signOut();
                } else if (TextUtils.equals(provider, "facebook")) {
                    Log.d("TekCrux", "facebook log out");
                    LoginManager.getInstance().logOut();
                } else {
                    Log.d("TekCrux", "normal log out");
                    FirebaseAuth.getInstance().signOut();
                }

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);

                break;
                //**********************************************************************
        }
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.fragment_home:
                            bottomFragment = new HomeFragment();
                            break;
                        case R.id.fragment_prepare:
                            bottomFragment = new PrepareFragment();
                            break;
                        case R.id.fragment_question_answer:
                            bottomFragment = new QuestionAnswersFragment();
                            break;
                        case R.id.fragment_take_test:
                            bottomFragment = new TakeTestFragment();
                            break;
                        case R.id.fragment_vault:
                            bottomFragment = new VaultFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, bottomFragment).commit();
                    return true;
                }
            };

    public void hideHamburgerNavigation() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void showHamburgerNavigation() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void showBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().remove(leftNavFragment).commit();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                showHamburgerNavigation();
                showBottomNavigation();
                actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, mDrawerLayout, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
                actionBarDrawerToggle.syncState();
            }
        });
    }

    public void saveIntoSharePref(String name, String username, String provider, String points, String profileImage) {

    }

    public void hideBottomNavigation() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

}
