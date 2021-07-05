package com.bioscope_vala.tekcrux.ui.prepare;

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
import com.bioscope_vala.tekcrux.QuizQuestion;
import com.bioscope_vala.tekcrux.QuizQuestionAdapterView;
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

public class PrepareFragment extends Fragment {

    private DatabaseReference mReference;
    RecyclerView recyclerView;
    List<QuizQuestion> mQuizQuestionList;
    private StorageReference mStorageReference;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private PrepareViewModel prepareViewModel;

    String quizQuestion, optA, optB, optC, optD, ra;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity) requireActivity()).getSupportActionBar().hide();
        View root = inflater.inflate(R.layout.fragment_prepare, container, false);
        mQuizQuestionList = new ArrayList<>();

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        mReference = FirebaseDatabase.getInstance().getReference("Quiz");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numberOfQuestions = (int)dataSnapshot.child("java").getChildrenCount();
                Log.d("TekCrux", "number of questions just counted" + numberOfQuestions);

                while (numberOfQuestions> 0) {
                    mReference = FirebaseDatabase.getInstance().getReference("Quiz").child("java").child(String.valueOf(numberOfQuestions));
                    Log.d("TekCrux", "number Of Questions" + numberOfQuestions);
                    mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            quizQuestion = dataSnapshot.child("QuizQuestion").getValue(String.class);
                            optA = dataSnapshot.child("optA").getValue(String.class);
                            optB = dataSnapshot.child("optB").getValue(String.class);
                            optC = dataSnapshot.child("optC").getValue(String.class);
                            optD = dataSnapshot.child("optD").getValue(String.class);
                            ra = dataSnapshot.child("rightans").getValue(String.class);
                            mQuizQuestionList.add(
                                    new QuizQuestion(quizQuestion, optA, optB, optC, optD, ra)
                            );
                            Log.d("TekCrux", "added to list, " + quizQuestion + " " + optA + " " + optB + " " + optC + " " + optD);
                            Log.d("TekCrux", "Size of list before passing: " + mQuizQuestionList.size());
                            recyclerView.setAdapter(new QuizQuestionAdapterView(getContext(), mQuizQuestionList));
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