package com.example.helloworld;

/**
 * Configuration class for OAuth settings.
 * Values are injected from BuildConfig for security and maintainability.
 */
public class OAuthConfig {
    
    public static final String BASE_URL = BuildConfig.OAUTH_BASE_URL;
    public static final String CLIENT_ID = BuildConfig.OAUTH_CLIENT_ID;
    public static final String STATE = BuildConfig.OAUTH_STATE;
    public static final String REDIRECT_URI = BuildConfig.OAUTH_REDIRECT_URI;
    public static final String SCOPE = BuildConfig.OAUTH_SCOPE;
    public static final String RESPONSE_TYPE = "code";
    
    /**
     * Builds the complete OAuth URL with the provided username.
     * 
     * @param username The username to include as login_hint
     * @return Complete OAuth authorization URL
     */
    public static String buildAuthUrl(String username) {
        return BASE_URL +
                "?login_hint=" + username +
                "&response_type=" + RESPONSE_TYPE +
                "&state=" + STATE +
                "&client_id=" + CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&scope=" + SCOPE;
    }
}