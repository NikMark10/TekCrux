package com.bioscope_vala.tekcrux.ui.home;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bioscope_vala.tekcrux.DatabaseHelper;
import com.bioscope_vala.tekcrux.HomeActivity;
import com.bioscope_vala.tekcrux.ProfileActivity;
import com.bioscope_vala.tekcrux.Question;
import com.bioscope_vala.tekcrux.QuestionAdapterView;
import com.bioscope_vala.tekcrux.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DatabaseReference mReference;
    RecyclerView recyclerView;
    List<Question> mQuestionList;
    private StorageReference mStorageReference;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private HomeViewModel homeViewModel;

    String profilePic, username, points, date, questionType, questionText, answer, answerer, answererPoints;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity) requireActivity()).getSupportActionBar().hide();
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mQuestionList = new ArrayList<>();

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        mReference = FirebaseDatabase.getInstance().getReference("Questions");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numberOfQuestions = (int)dataSnapshot.child("java").getChildrenCount();
                Log.d("TekCrux", "number of questions just counted" + numberOfQuestions);

                while (numberOfQuestions> 0) {
                    mReference = FirebaseDatabase.getInstance().getReference("Questions").child("java").child(String.valueOf(numberOfQuestions));
                    Log.d("TekCrux", "number Of Questions" + numberOfQuestions);
                    mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            profilePic = dataSnapshot.child("profileImage").getValue(String.class);
                            username = dataSnapshot.child("CuriousUsername").getValue(String.class);
                            points = dataSnapshot.child("CuriousPoints").getValue(String.class);
                            date = dataSnapshot.child("DateOfCreation").getValue(String.class);
                            questionType = dataSnapshot.child("QuestionType").getValue(String.class);
                            questionText = dataSnapshot.child("QuestionText").getValue(String.class);
                            answer = dataSnapshot.child("Answer").getValue(String.class);
                            answerer = dataSnapshot.child("AnswerBy").getValue(String.class);
                            answererPoints = dataSnapshot.child("AnswererPoints").getValue(String.class);
                            mQuestionList.add(
                                    new Question(profilePic, username, points, date, questionType, questionText,
                                            answer, answerer, answererPoints)
                            );
                            Log.d("TekCrux", "added to list, " + username + " " + points + " " + date + " " + questionType + " " + questionText);
                            Log.d("TekCrux", "Size of list before passing: " + mQuestionList.size());
                            recyclerView.setAdapter(new QuestionAdapterView(getContext(), mQuestionList));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    numberOfQuestions--;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }
}