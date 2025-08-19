# HelloWorldAndroid

This project is a Proof of Concept (POC) demonstrating **Android App Links** and **Custom Tabs** integration with a web app. The main goal is to show seamless interaction between an Android app and a web application using deep linking and custom browser tabs.

## Key Features

- **App Links & Deep Linking:**
  - Handles incoming links from the web using Android App Links and custom URL schemes.
  - Supports deep linking to specific app screens via URLs.
- **Custom Tabs Integration:**
  - Opens the web app in a Chrome Custom Tab for a smooth user experience.
  - Passes data (username) from the app to the web, and receives results back via deep link.
- **Web App Integration:**
  - The Android app interacts with a web app hosted at: [hello-world-web-ivory.vercel.app](https://hello-world-web-ivory.vercel.app) ([GitHub Repo](https://github.com/Rijul1204/hello-world-web))

## How the Flow Works

1. **User enters a username** in the Android app.
2. The app opens the web app in a Custom Tab, passing the username and a return URL (custom scheme: `helloworld://result`).
3. The web app processes the username and, when done, redirects back to the Android app using the custom scheme with result data as query params.
4. The Android app receives the result via deep link and displays it.

## Project Structure

- **app/src/main/java/com/example/helloworld/**
  - `MainActivity.java`: Handles app links, custom tabs, and fragment navigation.
  - `UsernameFragment.java`: Fragment for entering a username.
  - `ResultFragment.java`: Fragment for displaying the result from the web.
- **app/src/main/res/layout/**
  - `activity_main.xml`: Layout for the main activity.
  - `fragment_username.xml`: Layout for the username input fragment.
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

## Requirements
- Android Studio (Arctic Fox or newer recommended)
- Android SDK 21+
- Java 8+

## License
This project is for educational purposes.
