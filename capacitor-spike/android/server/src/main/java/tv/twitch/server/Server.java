package tv.twitch.server;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import tv.twitch.server.RecordedRequestServer.ContentMapping;

public class Server {
    public static void main(String... args) throws Throwable {
        try (@NonNull final MockWebServer mockWebServer = new MockWebServer()) {
            @NonNull final RecordedRequestServer recordedRequestServer =
                    new RecordedRequestServer(
                            new ContentMapping(
                                    "/public/index.html",
                                    "text/html",
                                    "/",
                                    "/index.html"
                            ),
                            new ContentMapping(
                                    "/public/assets/favicon.155239cf.ico",
                                    "image/x-icon",
                                    "/favicon.ico",
                                    "/assets/favicon.155239cf.ico"
                            ),
                            new ContentMapping(
                                    "/public/assets/index.4767dcad.css",
                                    "text/css",
                                    "/assets/index.4767dcad.css"
                            ),
                            new ContentMapping(
                                    "/public/assets/index.446fda50.js",
                                    "text/javascript",
                                    "/assets/index.446fda50.js"
                            ),
                            new ContentMapping(
                                    "/public/assets/web.439df183.js",
                                    "text/javascript",
                                    "/assets/web.439df183.js"
                            ),
                            new ContentMapping(
                                    "/public/cordova_plugins.js",
                                    "text/javascript",
                                    "/cordova_plugins.js"
                            ),
                            new ContentMapping(
                                    "/public/cordova.js",
                                    "text/javascript",
                                    "/cordova.js"
                            )
                    );
            mockWebServer.setDispatcher(new Dispatcher() {
                @NonNull
                @Override
                public MockResponse dispatch(
                        @NonNull RecordedRequest recordedRequest) {
                    return recordedRequestServer.serve(recordedRequest);
                }
            });

            @NonNull final CountDownLatch countDownLatch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    countDownLatch.countDown();
                    try {
                        mockWebServer.shutdown();
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                    }
                }
            }));

            mockWebServer.start(8080);

            countDownLatch.await();
        }
    }
}
