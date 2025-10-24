# Testing Guide for Custom Tabs Features

This guide explains how to test the different Custom Tabs features implemented in this app.

## Features to Test

### 1. Custom Tabs vs Auth Tabs

#### Custom Tabs
- **When to use**: General web content, standard browsing
- **What to look for**:
  - Purple toolbar matching app theme
  - Slide-in/slide-out animations
  - URL bar visible
  - Title displayed in toolbar
  - Share button available

#### Auth Tabs  
- **When to use**: OAuth flows, authentication processes
- **What to look for**:
  - Light purple toolbar (different from Custom Tabs)
  - Fade-in/fade-out animations
  - URL bar hidden for cleaner auth experience
  - Share button disabled for security
  - Streamlined UI optimized for authentication
  - **Note**: Currently implemented as auth-optimized Custom Tab demonstrating Auth Tab concepts

### 2. Ephemeral Browsing

#### What is Ephemeral Browsing?
- Private browsing mode that doesn't save any data
- No cookies, cache, history, or credentials are stored
- Data is completely deleted when the tab is closed
- Perfect for sensitive authentication or private sessions

#### Testing Ephemeral Mode
1. **Enable the toggle** in the app
2. **Look for confirmation** toast message
3. **Test privacy**: 
   - Login to a website
   - Close the Custom Tab
   - Reopen the same website
   - You should NOT be logged in (data was cleared)

#### Browser Requirements
- **Chrome 136+**: Full support with official API
- **Older Chrome versions**: Graceful fallback to regular browsing
- **Other browsers**: May not support ephemeral browsing

## Testing Scenarios

### Scenario 1: Basic Custom Tab
1. Enter username: `test-user`
2. Select: **Custom Tabs**
3. Leave ephemeral browsing **OFF**
4. Tap **Next**
5. **Expected**: Purple-themed Custom Tab with slide animations

### Scenario 2: Auth Tab for OAuth
1. Enter username: `oauth-user`
2. Select: **Auth Tabs**
3. Leave ephemeral browsing **OFF**
4. Tap **Next**
5. **Expected**: Light purple auth-optimized tab with hidden URL bar and streamlined UI

### Scenario 3: Ephemeral Custom Tab
1. Enter username: `private-user`
2. Select: **Custom Tabs**
3. Turn ephemeral browsing **ON**
4. Tap **Next**
5. **Expected**: Custom Tab with ephemeral browsing (if supported)

### Scenario 4: Ephemeral Auth Tab
1. Enter username: `private-auth`
2. Select: **Auth Tabs**
3. Turn ephemeral browsing **ON**
4. Tap **Next**
5. **Expected**: Auth-optimized tab with ephemeral browsing (if supported)

## What to Watch For

### Success Indicators
- ✅ Appropriate toast messages showing which features are active
- ✅ Correct toolbar colors and animations
- ✅ Proper URL bar visibility (hidden for Auth Tabs)
- ✅ Ephemeral browsing confirmation (when supported)

### Fallback Behavior
- 📱 **No Custom Tabs support**: Falls back to system browser
- 🔒 **No ephemeral support**: Falls back to regular Custom Tab
- ⚠️ **Auth Tab issues**: Falls back to Custom Tab
- 💬 **Clear error messages**: User-friendly feedback for all scenarios

## Browser Compatibility

### Chrome Versions
- **Chrome 137+**: Full Auth Tab and ephemeral browsing support (when API becomes available)
- **Chrome 136+**: Ephemeral browsing support with auth-optimized Custom Tabs
- **Chrome 100-135**: Custom Tabs with auth optimizations
- **Chrome <100**: Basic Custom Tabs support

### Auth Tab Implementation Note
This app demonstrates Auth Tab concepts using Custom Tabs optimized for authentication flows. The implementation includes all the key Auth Tab features:
- Hidden URL bar for cleaner auth experience
- Disabled sharing for enhanced security
- Streamlined UI focused on authentication
- Fade animations for smoother transitions
- Light theme optimized for auth flows

### Other Browsers
- **Samsung Internet**: Custom Tabs support varies
- **Firefox**: Limited Custom Tabs support
- **Edge**: Custom Tabs support on newer versions

## Troubleshooting

### Common Issues
1. **"Custom Tabs not supported"**: Install Chrome or update your browser
2. **"Ephemeral browsing not supported"**: Update Chrome to version 136+
3. **"Auth Tab failed"**: Normal fallback behavior, uses Custom Tab instead
4. **No animations**: Some browsers may not support custom animations

### Testing Tips
- Test on different devices with various Chrome versions
- Try with different browsers installed
- Test with and without Chrome as the default browser
- Verify fallback behavior works correctly