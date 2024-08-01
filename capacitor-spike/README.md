# Demonstrates Capacitor working normally with a server.url

For Capacitor issue: https://github.com/ionic-team/capacitor/issues/7596

## Running the project

This project uses an external server to serve web content.

### Run the server

```
cd android
./gradlew :server:assemble
java -jar server/build/libs/server-all.jar
```

### Run the Android client

Run the Capacitor Android project in an emulator with a WebView that supports [addDocumentStartJavaScript](https://developer.android.com/reference/androidx/webkit/WebViewCompat#addDocumentStartJavaScript(android.webkit.WebView,java.lang.String,java.util.Set%3Cjava.lang.String%3E)) (I used a Play Store API 34 emulator).

## Observing Normal Behavior

- Observe that the Capacitor example works normally.
- Open the Chrome web inspector
  - Observe that `Capacitor.platform` returns `android`.
  - Observe logged call and result for `SplashScreen.hide` 

