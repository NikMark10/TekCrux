package com.bioscope_vala.tekcrux;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizQuestionAdapterView extends RecyclerView.Adapter<QuizQuestionAdapterView.QuizQuestionViewHolder>{

    private List<QuizQuestion> mQuizQuestionList;
    Context mContext;

    public QuizQuestionAdapterView(Context context, List<QuizQuestion> quizQuestionList) {
        mQuizQuestionList = quizQuestionList;
        mContext = context;
    }

    @NonNull
    @Override
    public QuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_quiz_question, parent, false);
        return new QuizQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizQuestionViewHolder holder, int position) {
        QuizQuestion quizQuestion = mQuizQuestionList.get(position);

        holder.quiz.setText(quizQuestion.getQuestion());
        holder.optA.setText(quizQuestion.getOptA());
        holder.optB.setText(quizQuestion.getOptB());
        holder.optC.setText(quizQuestion.getOptC());
        holder.optD.setText(quizQuestion.getOptD());
        holder.answer.setText(quizQuestion.getRa());
    }

    @Override
    public int getItemCount() {
        return mQuizQuestionList.size();
    }

    public class QuizQuestionViewHolder extends RecyclerView.ViewHolder {

        TextView quiz;
        TextView optA;
        TextView optB;
        TextView optC;
        TextView optD;
        TextView answer;

        CardView quizQuestionCardView;

        public QuizQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            quizQuestionCardView = (CardView)itemView.findViewById(R.id.cardView);
            quiz = itemView.findViewById(R.id.quizQuestion);
            optA = itemView.findViewById(R.id.button1);
            optB = itemView.findViewById(R.id. button2);
            optC = itemView.findViewById(R.id.button3);
            optD = itemView.findViewById(R.id.button4);
            answer = itemView.findViewById(R.id.ans);

            optA.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    if(TextUtils.equals(b.getText().toString(), answer.getText().toString())) {
                        optA.setBackgroundResource(R.drawable.rounded_border_correct);
                    } else {
                        optA.setBackgroundResource(R.drawable.rounded_border_wrong);
                    }
                    answer.setVisibility(View.VISIBLE);
                    optA.setEnabled(false);
                    optB.setEnabled(false);
                    optC.setEnabled(false);
                    optD.setEnabled(false);
                }
            });

            optB.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    if(TextUtils.equals(b.getText().toString(), answer.getText().toString())) {
                        optB.setBackgroundResource(R.drawable.rounded_border_correct);
                    } else {
                        optB.setBackgroundResource(R.drawable.rounded_border_wrong);
                    }
                    answer.setVisibility(View.VISIBLE);
                    optA.setEnabled(false);
                    optB.setEnabled(false);
                    optC.setEnabled(false);
                    optD.setEnabled(false);
                }
            });

            optC.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    if(TextUtils.equals(b.getText().toString(), answer.getText().toString())) {
                        optC.setBackgroundResource(R.drawable.rounded_border_correct);
                    } else {
                        optC.setBackgroundResource(R.drawable.rounded_border_wrong);
                    }
                    answer.setVisibility(View.VISIBLE);
                    optA.setEnabled(false);
                    optB.setEnabled(false);
                    optC.setEnabled(false);
                    optD.setEnabled(false);
                }
            });

            optD.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    if(TextUtils.equals(b.getText().toString(), answer.getText().toString())) {
                        optD.setBackgroundResource(R.drawable.rounded_border_correct);
                    } else {
                        optD.setBackgroundResource(R.drawable.rounded_border_wrong);
                    }
                    answer.setVisibility(View.VISIBLE);
                    optA.setEnabled(false);
                    optB.setEnabled(false);
                    optC.setEnabled(false);
                    optD.setEnabled(false);
                }
            });
        }
    }
}
