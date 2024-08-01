# Demonstrates Capacitor bug with a server.url that contains a fragment

For Capacitor issue: https://github.com/ionic-team/capacitor/issues/7596

## Running the project

This project uses an external server to serve web content.

### Run the server

Running the server requires `sudo` because the server must run on port 80.
If you run the server on a non-default port, setting the allowed origin rule throws an exception.

```
cd android
./gradlew :server:assemble
sudo java -jar server/build/libs/server-all.jar
```

### Run the Android client

Run the Capacitor Android project in an emulator with a WebView that supports [addDocumentStartJavaScript](https://developer.android.com/reference/androidx/webkit/WebViewCompat#addDocumentStartJavaScript(android.webkit.WebView,java.lang.String,java.util.Set%3Cjava.lang.String%3E)) (I used a Play Store API 34 emulator).

## Observing Buggy Behavior

- Open the Chrome web inspector
  - Observe that `Capacitor.platform` returns `web`.
  - Observe no logged call or result for `SplashScreen.hide`
