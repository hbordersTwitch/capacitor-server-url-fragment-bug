package tv.twitch.capacitorspike;

import androidx.annotation.NonNull;

import com.getcapacitor.BridgeActivity;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import tv.twitch.capacitorspike.RecordedRequestServer.ContentMapping;

public class MainActivity extends BridgeActivity {
    @NonNull
    private final MockWebServer mockWebServer = new MockWebServer();

    @Override
    protected void load() {
        @NonNull final RecordedRequestServer recordedRequestServer =
                new RecordedRequestServer(
                        getAssets(),
                        new ContentMapping(
                                "public/index.html",
                                "text/html",
                                "/inner",
                                "/inner/index.html"
                        ),
                        new ContentMapping(
                                "public/assets/favicon.155239cf.ico",
                                "image/x-icon",
                                "/inner/favicon.ico",
                                "/assets/favicon.155239cf.ico"
                        ),
                        new ContentMapping(
                                "public/assets/index.4767dcad.css",
                                "text/css",
                                "/assets/index.4767dcad.css"
                        ),
                        new ContentMapping(
                                "public/assets/index.446fda50.js",
                                "text/javascript",
                                "/assets/index.446fda50.js"
                        ),
                        new ContentMapping(
                                "public/assets/web.439df183.js",
                                "text/javascript",
                                "/assets/web.439df183.js"
                        ),
                        new ContentMapping(
                                "public/cordova_plugins.js",
                                "text/javascript",
                                "/inner/cordova_plugins.js"
                        ),
                        new ContentMapping(
                                "public/cordova.js",
                                "text/javascript",
                                "/inner/cordova.js"
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mockWebServer.start(8080);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }).start();
        super.load();
    }

    @Override
    public void onDestroy() {
        try {
            mockWebServer.close();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        super.onDestroy();
    }
}
