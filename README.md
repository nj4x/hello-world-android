# HelloWorldAndroid

This project is a Proof of Concept (POC) demonstrating **Android App Links**, **Custom Tabs**, **Auth Tabs**, and **Ephemeral Browsing** integration with a web app. The main goal is to show seamless interaction between an Android app and a web application using deep linking and various browser tab types.

## Key Features

- **App Links & Deep Linking:**
  - Handles incoming links from the web using Android App Links and custom URL schemes.
  - Supports deep linking to specific app screens via URLs.
- **Custom Tabs Integration:**
  - Opens the web app in a Chrome Custom Tab for a smooth user experience.
  - Passes data (username) from the app to the web, and receives results back via deep link.
- **Auth Tabs Support:**
  - Specialized tabs optimized for OAuth and authentication flows.
  - Enhanced security features with hidden URL bar and disabled sharing.
- **Ephemeral Browsing:**
  - Private browsing mode that doesn't save browsing data (Android 12+ required).
  - Works with both Custom Tabs and Auth Tabs.
- **Web App Integration:**
  - The Android app interacts with a web app hosted at: [hello-world-web-ivory.vercel.app](https://hello-world-web-ivory.vercel.app) ([GitHub Repo](https://github.com/Rijul1204/hello-world-web))

## How the Flow Works

1. **User enters a username** in the Android app.
2. **User selects tab type**: Custom Tab or Auth Tab.
3. **User toggles ephemeral browsing** (optional, Android 12+ only).
4. The app opens the web app in the selected tab type, passing the username and a return URL (custom scheme: `helloworld://result`).
5. The web app processes the username and, when done, redirects back to the Android app using the custom scheme with result data as query params.
6. The Android app receives the result via deep link and displays it.

## Tab Types & Features

### Custom Tabs
- **Best for**: General web content and standard browsing
- **Features**: Custom toolbar colors, smooth animations, title display
- **UI**: Purple theme matching app design

### Auth Tabs  
- **Best for**: OAuth flows and authentication processes
- **Features**: Hidden URL bar, disabled sharing, fade animations, streamlined UI
- **UI**: Light purple theme for auth flows
- **Security**: Optimized for sensitive authentication data
- **Implementation**: Auth-optimized Custom Tab (demonstrates Auth Tab concepts)

### Ephemeral Browsing
- **Availability**: Chrome 136+ (uses official AndroidX Browser Library API)
- **Privacy**: Browsing data not saved to device (cookies, cache, history, etc.)
- **Compatibility**: Works with both Custom Tabs and Auth Tabs
- **Detection**: Automatic capability detection using `CustomTabsClient.isEphemeralBrowsingSupported()`
- **Fallback**: Automatic fallback to regular browsing on unsupported browsers

## Project Structure

- **app/src/main/java/com/example/helloworld/**
  - `MainActivity.java`: Handles app links, custom tabs, auth tabs, and fragment navigation.
  - `UsernameFragment.java`: Fragment for entering username and selecting tab options.
  - `ResultFragment.java`: Fragment for displaying the result from the web.
  - `BrowserUtils.java`: Utility class for browser capability detection and fallbacks.
  - `OAuthConfig.java`: Configuration for OAuth URLs and parameters.
- **app/src/main/res/layout/**
  - `activity_main.xml`: Layout for the main activity.
  - `fragment_username.xml`: Enhanced layout with tab type selection and ephemeral toggle.
  - `fragment_result.xml`: Layout for the result fragment.
- **app/src/main/AndroidManifest.xml**: Application manifest with intent filters for app links and custom schemes.

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd HelloWorldAndroid
   ```
2. **Open in Android Studio:**
   - Open Android Studio.
   - Select "Open an existing project" and choose this directory.
3. **Build and Run:**
   - Click the Run button or use `Shift+F10` to build and run the app on an emulator or device.

## Implementation Details

### Official APIs Used
- **AndroidX Browser Library 1.9.0-alpha05**: Latest version with ephemeral browsing support
- **CustomTabsIntent.Builder.setEphemeralBrowsingEnabled()**: Official API for ephemeral browsing
- **CustomTabsClient.isEphemeralBrowsingSupported()**: Runtime capability detection
- **Auth Tab Concepts**: Demonstrates Auth Tab principles using Custom Tabs optimized for authentication

### Browser Compatibility Detection
The app includes comprehensive browser capability detection:
- Automatic Custom Tabs provider detection (Chrome, Chrome Beta, Chrome Dev)
- Runtime ephemeral browsing support checking
- Graceful fallbacks with user feedback
- Error handling for unsupported features

### Testing Features
- **Tab Type Toggle**: Switch between Custom Tabs and Auth Tabs
- **Ephemeral Mode Toggle**: Enable/disable private browsing
- **Real-time Feedback**: Toast messages showing which features are active
- **Automatic Fallbacks**: Seamless fallback to supported alternatives

## Requirements
- Android Studio (Arctic Fox or newer recommended)
- Android SDK 21+
- Java 11+
- Chrome 136+ for ephemeral browsing (automatically detected)
- Chrome 137+ for full Auth Tab support (when available)

## License
This project is for educational purposes.
