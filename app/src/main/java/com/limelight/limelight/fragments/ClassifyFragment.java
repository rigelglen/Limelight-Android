package com.limelight.limelight.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.limelight.limelight.R;
import com.limelight.limelight.dialogs.ClassificationDialog;

import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ClassifyFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button analyzeButton = view.findViewById(R.id.analyze_button);
        TextInputEditText classifyUrl = view.findViewById(R.id.classify_url);

        analyzeButton.setOnClickListener((v) -> {
            String url = "";
            if (classifyUrl.getText() != null)
                url = classifyUrl.getText().toString();
            if (isValid(url)) {
                ClassificationDialog dialog = new ClassificationDialog(getActivity(), url);
                dialog.show();
            } else {
                Toast.makeText(getActivity(), "Invalid URL", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private boolean isValid(String url) {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }
}
