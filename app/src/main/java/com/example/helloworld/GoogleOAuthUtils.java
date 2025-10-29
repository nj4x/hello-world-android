package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Arrays;
import java.util.List;

public class GoogleOAuthUtils {
    
    private static final String TAG = "GoogleOAuthUtils";
    
    /**
     * Create Google Sign-In client with required scopes for Classroom API
     */
    public static GoogleSignInClient getGoogleSignInClient(Context context) {
        // Parse scopes from build config
        String scopesString = BuildConfig.GOOGLE_CLASSROOM_SCOPES;
        String[] scopeArray = scopesString.split(" ");
        
        // Create scope objects
        Scope[] scopes = new Scope[scopeArray.length];
        for (int i = 0; i < scopeArray.length; i++) {
            scopes[i] = new Scope(scopeArray[i]);
        }
        
        // Configure sign-in to request the user's ID, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(scopes[0], Arrays.copyOfRange(scopes, 1, scopes.length))
                .requestIdToken(BuildConfig.GOOGLE_OAUTH_CLIENT_ID)
                .build();
        
        // Build a GoogleSignInClient with the options specified by gso
        return GoogleSignIn.getClient(context, gso);
    }
    
    /**
     * Get Google Account Credential for API calls
     */
    public static GoogleAccountCredential getGoogleAccountCredential(Context context, GoogleSignInAccount account) {
        // Parse scopes from build config
        String scopesString = BuildConfig.GOOGLE_CLASSROOM_SCOPES;
        String[] scopeArray = scopesString.split(" ");
        List<String> scopes = Arrays.asList(scopeArray);
        
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, scopes);
        credential.setSelectedAccount(account.getAccount());
        return credential;
    }
    
    /**
     * Handle the result from Google Sign-In activity
     */
    public static GoogleSignInAccount handleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            return task.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            return null;
        }
    }
    
    /**
     * Get Classroom API service instance
     */
    public static com.google.api.services.classroom.Classroom getClassroomService(GoogleAccountCredential credential) {
        return new com.google.api.services.classroom.Classroom.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("HelloWorld Android App")
                .build();
    }
}
