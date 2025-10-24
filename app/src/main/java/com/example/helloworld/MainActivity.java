package com.example.helloworld;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
    public void onUsernameSubmitted(UsernameFragment.TabConfig config) {
        String tabTypeText = config.tabType == UsernameFragment.TabType.CUSTOM_TAB ? "Custom Tab" : "Auth Tab";
        String ephemeralText = config.isEphemeral ? " (Ephemeral)" : "";
        
        Toast.makeText(this, "Hello " + config.username + "! Opening " + tabTypeText + ephemeralText + "...", Toast.LENGTH_SHORT).show();
        openWebApp(config);
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

    private void openWebApp(UsernameFragment.TabConfig config) {
        String webAppUrl = OAuthConfig.buildAuthUrl(config.username);
        Uri uri = Uri.parse(webAppUrl);

        if (config.tabType == UsernameFragment.TabType.AUTH_TAB) {
            openAuthTab(uri, config.isEphemeral);
        } else {
            openCustomTab(uri, config.isEphemeral);
        }
    }

    private void openCustomTab(Uri uri, boolean isEphemeral) {
        // Check if Custom Tabs are supported
        if (!BrowserUtils.isCustomTabsSupported(this)) {
            Toast.makeText(this, "Custom Tabs not supported, opening in browser", Toast.LENGTH_SHORT).show();
            // Use fallback intent with proper error handling
            Intent fallbackIntent = BrowserUtils.getFallbackIntent(uri);
            if (BrowserUtils.canHandleIntent(this, fallbackIntent)) {
                startActivity(fallbackIntent);
            } else {
                Toast.makeText(this, "No browser available to handle the URL", Toast.LENGTH_LONG).show();
            }
            return;
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(this, R.color.purple_500))
                        .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.purple_700))
                        .build())
                .setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .setShowTitle(true)
                .setUrlBarHidingEnabled(false);

        // Add ephemeral browsing if requested
        if (isEphemeral) {
            // First try quick synchronous check (Chrome 137+)
            if (BrowserUtils.isEphemeralBrowsingSupported(this)) {
                try {
                    CustomTabsIntent customTabsIntent = builder
                            .setEphemeralBrowsingEnabled(true)
                            .build();
                    customTabsIntent.launchUrl(this, uri);
                    Toast.makeText(this, "Opened with ephemeral browsing", Toast.LENGTH_SHORT).show();
                    return;
                } catch (Exception e) {
                    Log.e("MainActivity", "Ephemeral browsing failed", e);
                    Toast.makeText(this, "Ephemeral browsing failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            
            // Fallback to async check for older versions
            BrowserUtils.checkEphemeralBrowsingSupport(this, new BrowserUtils.EphemeralSupportCallback() {
                @Override
                public void onResult(boolean isSupported) {
                    runOnUiThread(() -> {
                        if (isSupported) {
                            try {
                                CustomTabsIntent customTabsIntent = builder
                                        .setEphemeralBrowsingEnabled(true)
                                        .build();
                                customTabsIntent.launchUrl(MainActivity.this, uri);
                                Toast.makeText(MainActivity.this, "Opened with ephemeral browsing (via service)", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("MainActivity", "Ephemeral browsing failed (via service)", e);
                                Toast.makeText(MainActivity.this, "Ephemeral browsing failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                launchRegularCustomTab(builder, uri);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Ephemeral browsing not supported, using regular Custom Tab", Toast.LENGTH_SHORT).show();
                            launchRegularCustomTab(builder, uri);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error checking ephemeral support: " + error, Toast.LENGTH_SHORT).show();
                        launchRegularCustomTab(builder, uri);
                    });
                }
            });
        } else {
            launchRegularCustomTab(builder, uri);
        }
    }

    private void launchRegularCustomTab(CustomTabsIntent.Builder builder, Uri uri) {
        try {
            CustomTabsIntent customTabsIntent = builder.build();
            
            // Set the package name for better reliability
            String packageName = BrowserUtils.getCustomTabsPackage(this);
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName);
            }
            
            customTabsIntent.launchUrl(this, uri);
        } catch (Exception e) {
            Log.e("MainActivity", "Failed to open Custom Tab", e);
            Toast.makeText(this, "Failed to open Custom Tab: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Intent fallbackIntent = BrowserUtils.getFallbackIntent(uri);
            if (BrowserUtils.canHandleIntent(this, fallbackIntent)) {
                startActivity(fallbackIntent);
            } else {
                Toast.makeText(this, "No browser available to handle the URL", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openAuthTab(Uri uri, boolean isEphemeral) {
        // Check if Auth Tab is supported first
        if (!BrowserUtils.isAuthTabSupported(this)) {
            Toast.makeText(this, "Auth Tab not supported, falling back to Custom Tab", Toast.LENGTH_SHORT).show();
            openCustomTab(uri, isEphemeral);
            return;
        }

        // Create a Custom Tab optimized for authentication flows
        // This demonstrates Auth Tab concepts using available APIs
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(this, R.color.purple_200))
                        .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.purple_300))
                        .build())
                .setShowTitle(true)
                .setUrlBarHidingEnabled(true) // Hide URL bar for auth flows
                .setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF); // Disable sharing for auth flows

        // Add ephemeral browsing if requested
        if (isEphemeral) {
            // First try quick synchronous check (Chrome 137+)
            if (BrowserUtils.isEphemeralBrowsingSupported(this)) {
                try {
                    CustomTabsIntent customTabsIntent = builder
                            .setEphemeralBrowsingEnabled(true)
                            .build();
                    customTabsIntent.launchUrl(this, uri);
                    Toast.makeText(this, "Opened Auth-optimized Tab with ephemeral browsing", Toast.LENGTH_SHORT).show();
                    return;
                } catch (Exception e) {
                    Log.e("MainActivity", "Ephemeral Auth Tab failed", e);
                    Toast.makeText(this, "Ephemeral Auth Tab failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            
            // Fallback to async check for older versions
            BrowserUtils.checkEphemeralBrowsingSupport(this, new BrowserUtils.EphemeralSupportCallback() {
                @Override
                public void onResult(boolean isSupported) {
                    runOnUiThread(() -> {
                        if (isSupported) {
                            try {
                                CustomTabsIntent customTabsIntent = builder
                                        .setEphemeralBrowsingEnabled(true)
                                        .build();
                                customTabsIntent.launchUrl(MainActivity.this, uri);
                                Toast.makeText(MainActivity.this, "Opened Auth-optimized Tab with ephemeral browsing (via service)", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("MainActivity", "Ephemeral Auth Tab failed (via service)", e);
                                Toast.makeText(MainActivity.this, "Ephemeral Auth Tab failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                launchRegularAuthTab(builder, uri);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Ephemeral browsing not supported, using regular Auth Tab", Toast.LENGTH_SHORT).show();
                            launchRegularAuthTab(builder, uri);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error checking ephemeral support: " + error, Toast.LENGTH_SHORT).show();
                        launchRegularAuthTab(builder, uri);
                    });
                }
            });
        } else {
            launchRegularAuthTab(builder, uri);
        }
    }

    private void launchRegularAuthTab(CustomTabsIntent.Builder builder, Uri uri) {
        try {
            CustomTabsIntent customTabsIntent = builder.build();
            
            // Set the package name for better reliability
            String packageName = BrowserUtils.getCustomTabsPackage(this);
            if (packageName != null) {
                customTabsIntent.intent.setPackage(packageName);
            }
            
            customTabsIntent.launchUrl(this, uri);
            Toast.makeText(this, "Opened Auth-optimized Tab", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("MainActivity", "Auth Tab failed", e);
            Toast.makeText(this, "Auth Tab failed: " + e.getMessage() + ", using Custom Tab instead", Toast.LENGTH_LONG).show();
            openCustomTab(uri, false); // Fallback without ephemeral to avoid infinite loop
        }
    }
}
