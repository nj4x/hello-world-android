package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements UsernameFragment.OnUsernameSubmittedListener {

    private UsernameFragment usernameFragment;
    private ResultFragment resultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize fragments
        usernameFragment = new UsernameFragment();
        usernameFragment.setOnUsernameSubmittedListener(this);
        resultFragment = new ResultFragment();

        // Handle incoming intent (from web app)
        handleIncomingIntent();

        // Show username fragment by default if no intent data
        if (getIntent().getData() == null) {
            showUsernameFragment();
        }
    }

    @Override
    protected void onNewIntent(@NonNull android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent();
    }

    private void handleIncomingIntent() {
        Uri data = getIntent().getData();
        if (data != null) {
            String webResult = data.getQueryParameter("result");
            String username = data.getQueryParameter("username");

            Toast.makeText(this, "Received URL: " + data, Toast.LENGTH_LONG).show();

            if (webResult != null && username != null) {
                // Returning from web app with result
                Toast.makeText(this, "Showing result: " + webResult, Toast.LENGTH_LONG).show();
                showResultFragment(username, webResult);
            } else {
                Toast.makeText(this, "Missing data - username: " + username + ", result: " + webResult, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onUsernameSubmitted(String username) {
        Toast.makeText(this, "Hello " + username + "! Opening web page...", Toast.LENGTH_SHORT).show();
        openWebApp(username);
    }

    public void showUsernameFragment() {
        replaceFragment(usernameFragment);
    }

    private void showResultFragment(String username, String result) {
        resultFragment.setResult(username, result);
        replaceFragment(resultFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void openWebApp(String username) {
        // URL to your deployed Next.js web app on Vercel
//        String webAppUrl = "https://hello-world-web-ivory.vercel.app/process?username=" + username + "&returnApp=helloworld://result";
        
        // Build OAuth URL using configuration class for better maintainability
        String webAppUrl = OAuthConfig.buildAuthUrl(username);

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(this, R.color.purple_500))
                        .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.purple_700))
                        .build())
                .setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .setShowTitle(true)
                .setUrlBarHidingEnabled(false)
                .build();

        customTabsIntent.launchUrl(this, Uri.parse(webAppUrl));
    }
}
