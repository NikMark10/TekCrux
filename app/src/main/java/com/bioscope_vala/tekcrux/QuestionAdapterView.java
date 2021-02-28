package com.bioscope_vala.tekcrux;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bioscope_vala.tekcrux.ui.home.HomeFragment;
import com.bioscope_vala.tekcrux.ui.home.HomeViewModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class QuestionAdapterView extends RecyclerView.Adapter<QuestionAdapterView.QuestionViewHolder> {

    private List<Question> mQuestionList;
    Context mContext;

    public QuestionAdapterView(Context context, List<Question> questionList) {
        mQuestionList = questionList;
        mContext = context;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = mQuestionList.get(position);

//        loadImage(profileUri, holder.profilePicCurious);
        Glide.with(mContext).load(question.getProfilePicUri()).into(holder.profilePicCuriousField);
        holder.usernameCuriousField.setText(question.getUsernameCurious());
        holder.pointsOfCuriousField.setText(question.getPointsCurious());
        holder.dateOfCreationField.setText(question.getDateOfCreation());
        holder.questionTypeField.setText(question.getQuestionType());
        holder.questionTextField.setText(question.getQuestionText());
        holder.answerTextField.setText(question.getAnswer());
        holder.answererTextField.setText(question.getAnswerer());
        holder.answererPointsTextField.setText(question.getAnswererPoints());
    }

    @Override
    public int getItemCount() {
        Log.d("TekCrux","size of list, " + mQuestionList.size());
        return mQuestionList.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicCuriousField;
        TextView usernameCuriousField;
        TextView pointsOfCuriousField;
        TextView dateOfCreationField;
        TextView questionTypeField;
        TextView questionTextField;
        TextView answerTextField;
        TextView answererTextField;
        TextView answererPointsTextField;

        CardView questionCardView;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionCardView = (CardView)itemView.findViewById(R.id.cardView);
            profilePicCuriousField = itemView.findViewById(R.id.curiousProfilePic);
            usernameCuriousField = itemView.findViewById(R.id.username);
            pointsOfCuriousField = itemView.findViewById(R.id.pointsOfCurious);
            dateOfCreationField = itemView.findViewById(R.id.date);
            questionTypeField = itemView.findViewById(R.id.questionType);
            questionTextField = itemView.findViewById(R.id.question);
            answerTextField = itemView.findViewById(R.id.answers);
            answererTextField = itemView.findViewById(R.id.answerByUsername);
            answererPointsTextField = itemView.findViewById(R.id.pointsOfReplier);
        }
    }
}
