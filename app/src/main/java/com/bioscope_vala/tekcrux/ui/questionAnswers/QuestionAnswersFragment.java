package com.bioscope_vala.tekcrux.ui.questionAnswers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bioscope_vala.tekcrux.HomeActivity;
import com.bioscope_vala.tekcrux.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class QuestionAnswersFragment extends Fragment {

    private EditText doubtQue;
    private Button saveData;
    private DatabaseReference mReference;
    private QuestionAnswersViewModel mQuestionAnswersViewModel;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String username;
    String date = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault()).format(new Date());

    public QuestionAnswersFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity) requireActivity()).getSupportActionBar().hide();
        View root = inflater.inflate(R.layout.fragment_question_answer, container, false);

        doubtQue = root.findViewById(R.id.doubtInput);
        saveData = root.findViewById(R.id.post);

        mReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TekCrux", "Cannot fetch data");
            }
        });
        mReference = FirebaseDatabase.getInstance().getReference("Doubts");
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReference.child("CuriousUsername").setValue(username);
                mReference.child("DateOfCreation").setValue(date);
                mReference.child("QuestionText").setValue(doubtQue.getText().toString());
                Toast.makeText(getApplicationContext(),"Question Posted.",Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }
}