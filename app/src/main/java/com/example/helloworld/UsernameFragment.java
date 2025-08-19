package com.example.helloworld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;

public class UsernameFragment extends Fragment {

    private TextInputEditText usernameInput;
    private Button nextButton;
    private OnUsernameSubmittedListener listener;

    public interface OnUsernameSubmittedListener {
        void onUsernameSubmitted(String username);
    }

    public void setOnUsernameSubmittedListener(OnUsernameSubmittedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username, container, false);
        
        usernameInput = view.findViewById(R.id.usernameInput);
        nextButton = view.findViewById(R.id.nextButton);
        
        nextButton.setOnClickListener(v -> handleNextClick());
        
        return view;
    }

    private void handleNextClick() {
        String username = usernameInput.getText().toString().trim();
        
        if (username.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) {
            listener.onUsernameSubmitted(username);
        }
    }
}
