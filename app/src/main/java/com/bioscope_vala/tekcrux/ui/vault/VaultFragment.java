package com.bioscope_vala.tekcrux.ui.vault;

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

public class VaultFragment extends Fragment{
    private VaultViewModel mVaultViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mVaultViewModel =
                ViewModelProviders.of(this).get(VaultViewModel.class);
        View root = inflater.inflate(R.layout.fragment_vault, container, false);
        final TextView textView = root.findViewById(R.id.text_vault);
        mVaultViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
