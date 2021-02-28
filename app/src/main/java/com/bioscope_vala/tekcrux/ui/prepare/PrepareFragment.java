package com.bioscope_vala.tekcrux.ui.prepare;

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

public class PrepareFragment extends Fragment {

    private PrepareViewModel mPrepareViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mPrepareViewModel =
                ViewModelProviders.of(this).get(PrepareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prepare, container, false);
        final TextView textView = root.findViewById(R.id.text_prepare);
        mPrepareViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}