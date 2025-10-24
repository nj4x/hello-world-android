package com.example.helloworld;

/**
 * Simple test class to verify TabConfig functionality
 * This would normally be in src/test, but keeping it simple for the POC
 */
public class TabConfigTest {
    
    public static void testTabConfig() {
        // Test Custom Tab configuration
        UsernameFragment.TabConfig customConfig = new UsernameFragment.TabConfig(
            "testuser", 
            UsernameFragment.TabType.CUSTOM_TAB, 
            false
        );
        
        assert customConfig.username.equals("testuser");
        assert customConfig.tabType == UsernameFragment.TabType.CUSTOM_TAB;
        assert !customConfig.isEphemeral;
        
        // Test Auth Tab with ephemeral configuration
        UsernameFragment.TabConfig authConfig = new UsernameFragment.TabConfig(
            "authuser", 
            UsernameFragment.TabType.AUTH_TAB, 
            true
        );
        
        assert authConfig.username.equals("authuser");
        assert authConfig.tabType == UsernameFragment.TabType.AUTH_TAB;
        assert authConfig.isEphemeral;
        
        System.out.println("All TabConfig tests passed!");
    }
}