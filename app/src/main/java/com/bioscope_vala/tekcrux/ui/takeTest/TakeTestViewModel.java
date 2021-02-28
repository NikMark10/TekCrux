package com.bioscope_vala.tekcrux.ui.takeTest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class TakeTestViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public TakeTestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Take Test fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
