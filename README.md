# React Native Project

This is a new [**React Native**](https://reactnative.dev) project, bootstrapped using [`@react-native-community/cli`](https://github.com/react-native-community/cli).

> **Note**: Make sure you have completed the [Set Up Your Environment](https://reactnative.dev/docs/set-up-your-environment) guide before proceeding.

## Running as React Native Project

### Method 1: Using Command Line

1. Run `yarn install` in the root folder
2. Execute the following command:
   ```sh
   yarn android
   ```

### Method 2: Using Android Studio

#### Prerequisites
In `MainActivity.kt` file, there are two entry points defined:
- Default: Runs as React Native project
- Standalone: To run as standalone Android app (comment lines 1-22 and uncomment lines 25-78)

#### Steps
1. Run `yarn install` in the root folder
2. Open the `android` folder in Android Studio
3. Wait for Gradle sync to complete
4. Run the app using the play button
5. Once installed on emulator, run the Metro bundler:
   ```sh
   npx react-native start
   ```

## Troubleshooting

If you encounter any issues, refer to the [Troubleshooting](https://reactnative.dev/docs/troubleshooting) page.

## Additional Resources

- [React Native Website](https://reactnative.dev) - Official React Native documentation
- [Getting Started](https://reactnative.dev/docs/environment-setup) - Environment setup guide
- [Learn the Basics](https://reactnative.dev/docs/getting-started) - React Native fundamentals
- [Blog](https://reactnative.dev/blog) - Latest React Native updates
- [GitHub Repository](https://github.com/facebook/react-native) - React Native source code
