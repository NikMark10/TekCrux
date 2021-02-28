package com.bioscope_vala.tekcrux.ui.prepare;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrepareViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PrepareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is prepare fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}