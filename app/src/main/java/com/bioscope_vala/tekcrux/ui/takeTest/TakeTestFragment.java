package com.bioscope_vala.tekcrux.ui.takeTest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bioscope_vala.tekcrux.R;
import com.bioscope_vala.tekcrux.ui.takeTest.TakeTestViewModel;

public class TakeTestFragment extends Fragment {
    private TakeTestViewModel mTakeTestViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mTakeTestViewModel =
                ViewModelProviders.of(this).get(TakeTestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_take_test, container, false);
        final TextView textView = root.findViewById(R.id.text_take_test);
        mTakeTestViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
