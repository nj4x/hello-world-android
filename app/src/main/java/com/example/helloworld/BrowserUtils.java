package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrowserUtils {
    
    public interface EphemeralSupportCallback {
        void onResult(boolean isSupported);
        void onError(String error);
    }
    
    /**
     * Check if the default browser supports Custom Tabs
     */
    public static boolean isDefaultBrowserCustomTabsSupported(Context context) {
        String packageName = CustomTabsClient.getPackageName(context, Collections.emptyList());
        return packageName != null;
    }
    
    /**
     * Check if any browser on the device supports Custom Tabs
     */
    public static boolean isCustomTabsSupported(Context context) {
        // First check default browser
        if (isDefaultBrowserCustomTabsSupported(context)) {
            return true;
        }
        
        // Get all apps that can handle VIEW intents
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL);
        
        // Extract package names from ResolveInfo objects
        List<String> packageNames = new ArrayList<>();
        for (ResolveInfo info : resolveInfos) {
            packageNames.add(info.activityInfo.packageName);
        }
        
        // Get a package that supports Custom Tabs
        String packageName = CustomTabsClient.getPackageName(context, packageNames, true /* ignore default */);
        return packageName != null;
    }
    
    /**
     * Get the preferred Custom Tabs provider package name
     */
    public static String getCustomTabsPackage(Context context) {
        // First try default browser
        String packageName = CustomTabsClient.getPackageName(context, Collections.emptyList());
        if (packageName != null) {
            return packageName;
        }
        
        // Get all apps that can handle VIEW intents
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL);
        
        // Extract package names from ResolveInfo objects
        List<String> packageNames = new ArrayList<>();
        for (ResolveInfo info : resolveInfos) {
            packageNames.add(info.activityInfo.packageName);
        }
        
        // Get a package that supports Custom Tabs
        return CustomTabsClient.getPackageName(context, packageNames, true /* ignore default */);
    }
    
    /**
     * Check if Auth Tab is supported using the official API
     * Note: This is a placeholder implementation as the API may not be available in current version
     */
    public static boolean isAuthTabSupported(Context context) {
        String packageName = getCustomTabsPackage(context);
        if (packageName == null) {
            return false;
        }
        
        try {
            // Try to check if Auth Tab is supported (API may not be available yet)
            // For now, we assume it's supported if Custom Tabs are supported
            return isCustomTabsSupported(context);
        } catch (Exception e) {
            // If the API is not available, assume not supported
            return false;
        }
    }

    /**
     * Check if ephemeral browsing is supported using the static API (Chrome 137+)
     */
    public static void checkEphemeralBrowsingSupport(Context context, EphemeralSupportCallback callback) {
        String packageName = getCustomTabsPackage(context);
        if (packageName == null) {
            callback.onError("No Custom Tabs provider found");
            return;
        }
        
        try {
            // First try the static method (Chrome 137+)
            boolean isSupported = CustomTabsClient.isEphemeralBrowsingSupported(context, packageName);
            callback.onResult(isSupported);
        } catch (Exception e) {
            // If static API is not available, try service connection method
            checkEphemeralBrowsingSupportViaService(context, packageName, callback);
        }
    }
    
    /**
     * Check ephemeral browsing support via service connection (fallback method)
     * Note: The session-based API is not yet available in the current browser library version
     */
    private static void checkEphemeralBrowsingSupportViaService(Context context, String packageName, EphemeralSupportCallback callback) {
        // The CustomTabsSession.isEphemeralBrowsingSupported method is not available yet
        // in the current browser library version. For now, we'll assume not supported
        // when the static API fails.
        callback.onResult(false);
    }
    
    /**
     * Synchronous check for ephemeral browsing support (use with caution)
     * This method only works with the static API (Chrome 137+)
     */
    public static boolean isEphemeralBrowsingSupported(Context context) {
        String packageName = getCustomTabsPackage(context);
        if (packageName == null) {
            return false;
        }
        
        try {
            return CustomTabsClient.isEphemeralBrowsingSupported(context, packageName);
        } catch (Exception e) {
            // If the static API is not available, return false
            // For a complete check, use the async method above
            return false;
        }
    }
    
    /**
     * Get a fallback intent for browsers that don't support Custom Tabs
     */
    public static Intent getFallbackIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
    
    /**
     * Check if a browser app can handle the given intent
     */
    public static boolean canHandleIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        return resolveInfos != null && !resolveInfos.isEmpty();
    }
}