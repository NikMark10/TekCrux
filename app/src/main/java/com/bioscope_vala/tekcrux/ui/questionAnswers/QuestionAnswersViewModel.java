package com.bioscope_vala.tekcrux.ui.questionAnswers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuestionAnswersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QuestionAnswersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is questionAnswers fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}