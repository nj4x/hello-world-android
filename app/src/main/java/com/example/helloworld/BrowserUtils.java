package com.example.helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsService;

import java.util.List;

public class BrowserUtils {
    
    private static final String CHROME_PACKAGE = "com.android.chrome";
    private static final String CHROME_BETA_PACKAGE = "com.chrome.beta";
    private static final String CHROME_DEV_PACKAGE = "com.chrome.dev";
    
    public interface EphemeralSupportCallback {
        void onResult(boolean isSupported);
        void onError(String error);
    }
    
    /**
     * Check if Custom Tabs are supported on this device
     */
    public static boolean isCustomTabsSupported(Context context) {
        Intent serviceIntent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(serviceIntent, 0);
        return resolveInfos != null && !resolveInfos.isEmpty();
    }
    
    /**
     * Get the preferred Custom Tabs provider package name
     */
    public static String getCustomTabsPackage(Context context) {
        // Try Chrome first, then fallback to other browsers
        String[] packages = {CHROME_PACKAGE, CHROME_BETA_PACKAGE, CHROME_DEV_PACKAGE};
        
        for (String packageName : packages) {
            Intent serviceIntent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(packageName);
            if (context.getPackageManager().resolveService(serviceIntent, 0) != null) {
                return packageName;
            }
        }
        
        // Fallback to any available Custom Tabs provider
        Intent serviceIntent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        if (resolveInfos != null && !resolveInfos.isEmpty()) {
            return resolveInfos.get(0).serviceInfo.packageName;
        }
        
        return null;
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
     * Check if ephemeral browsing is supported using the official API
     */
    public static void checkEphemeralBrowsingSupport(Context context, EphemeralSupportCallback callback) {
        String packageName = getCustomTabsPackage(context);
        if (packageName == null) {
            callback.onError("No Custom Tabs provider found");
            return;
        }
        
        // Check using the static method (Chrome 137+)
        try {
            boolean isSupported = CustomTabsClient.isEphemeralBrowsingSupported(context, packageName);
            callback.onResult(isSupported);
        } catch (Exception e) {
            // If the API is not available, assume not supported
            callback.onResult(false);
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