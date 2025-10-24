package com.example.helloworld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ResultFragment extends Fragment {

    private TextView resultText;
    private TextView usernameText;
    private Button backButton;
    
    // Store data until view is created
    private String storedUsername;
    private String storedWebResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        
        resultText = view.findViewById(R.id.resultText);
        usernameText = view.findViewById(R.id.usernameText);
        backButton = view.findViewById(R.id.backButton);
        
        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showUsernameFragment();
            }
        });
        
        // Apply stored data if available
        if (storedUsername != null && storedWebResult != null) {
            updateUI(storedUsername, storedWebResult);
        }
        
        return view;
    }

    public void setResult(String username, String webResult) {
        storedUsername = username;
        storedWebResult = webResult;
        
        // Update UI immediately if view is ready
        if (usernameText != null && resultText != null) {
            updateUI(username, webResult);
        }
    }
    
    private void updateUI(String username, String webResult) {
        if (usernameText != null) {
            usernameText.setText(getString(R.string.username_label, username));
        }
        if (resultText != null) {
            resultText.setText(getString(R.string.result_label, webResult));
        }
    }
}
