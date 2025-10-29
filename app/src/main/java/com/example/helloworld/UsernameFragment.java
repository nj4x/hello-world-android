package com.example.helloworld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class UsernameFragment extends Fragment {

    private TextInputEditText usernameInput;
    private Button nextButton;
    private RadioGroup tabTypeRadioGroup;
    private RadioButton customTabRadio;
    private RadioButton authTabRadio;
    private SwitchMaterial ephemeralSwitch;
    private OnUsernameSubmittedListener listener;

    public enum TabType {
        CUSTOM_TAB,
        AUTH_TAB
    }

    public static class TabConfig {
        public final String username;
        public final TabType tabType;
        public final boolean isEphemeral;

        public TabConfig(String username, TabType tabType, boolean isEphemeral) {
            this.username = username;
            this.tabType = tabType;
            this.isEphemeral = isEphemeral;
        }
    }

    public interface OnUsernameSubmittedListener {
        void onUsernameSubmitted(TabConfig config);
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
        tabTypeRadioGroup = view.findViewById(R.id.tabTypeRadioGroup);
        customTabRadio = view.findViewById(R.id.customTabRadio);
        authTabRadio = view.findViewById(R.id.authTabRadio);
        ephemeralSwitch = view.findViewById(R.id.ephemeralSwitch);
        
        nextButton.setOnClickListener(v -> handleNextClick());
        
        return view;
    }

    private void handleNextClick() {
        String username = usernameInput.getText().toString().trim();
        
        // Special case for Google Classroom integration
        if (username.equals("google_classroom")) {
            if (listener != null) {
                listener.onUsernameSubmitted(new TabConfig(username, TabType.CUSTOM_TAB, false));
            }
            return;
        }
        
        if (username.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        TabType tabType = customTabRadio.isChecked() ? TabType.CUSTOM_TAB : TabType.AUTH_TAB;
        boolean isEphemeral = ephemeralSwitch.isChecked();

        if (listener != null) {
            listener.onUsernameSubmitted(new TabConfig(username, tabType, isEphemeral));
        }
    }
}
