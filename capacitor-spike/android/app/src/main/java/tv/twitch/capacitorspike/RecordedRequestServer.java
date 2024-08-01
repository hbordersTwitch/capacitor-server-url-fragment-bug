package tv.twitch.capacitorspike;

import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;

public final class RecordedRequestServer {
    public static final class ContentMapping {
        @NonNull
        public final String filePath;
        @NonNull
        public final String contentType;
        @NonNull
        public final List<String> httpPaths;

        public ContentMapping(
                @NonNull String filePath,
                @NonNull String contentType,
                @NonNull String... httpPaths
        ) {
            this.filePath = filePath;
            this.contentType = contentType;
            this.httpPaths = Arrays.asList(httpPaths);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ContentMapping that = (ContentMapping) o;
            return Objects.equals(filePath, that.filePath) && Objects.equals(contentType, that.contentType) && Objects.equals(httpPaths, that.httpPaths);
        }

        @Override
        public int hashCode() {
            return Objects.hash(filePath, contentType, httpPaths);
        }

        @NonNull
        @Override
        public String toString() {
            return "ContentMapping{" +
                    "filePath='" + filePath + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", httpPaths=" + httpPaths +
                    '}';
        }
    }

    @NonNull
    private final AssetManager assetManager;

    @NonNull
    private final HashMap<
            String,
            ContentMapping
            > contentMappingsByHttpPath;

    public RecordedRequestServer(
            @NonNull AssetManager assetManager,
            @NonNull ContentMapping... contentMappings
    ) {
        this.assetManager = assetManager;
        contentMappingsByHttpPath = new HashMap<>(contentMappings.length);
        for (@NonNull final ContentMapping contentMapping : contentMappings) {
            for (@NonNull final String httpPath : contentMapping.httpPaths) {
                @Nullable final ContentMapping existingContentMapping =
                        contentMappingsByHttpPath.get(httpPath);
                if (existingContentMapping == null) {
                    contentMappingsByHttpPath.put(
                            httpPath,
                            contentMapping
                    );
                } else {
                    throw new IllegalArgumentException(
                            "Duplicate contentMapping httpPath. " + existingContentMapping + " " + contentMapping
                    );
                }
            }
        }
    }

    @NonNull
    public MockResponse serve(@NonNull RecordedRequest recordedRequest) {
        try {
            @Nullable final String httpPath = recordedRequest.getPath();
            if (httpPath == null) {
                throw new IllegalArgumentException("No Http Path in " + recordedRequest);
            } else {
                @Nullable final ContentMapping contentMapping =
                        contentMappingsByHttpPath.get(httpPath);
                if (contentMapping == null) {
                    System.err.println("No content mapping for: " + recordedRequest);
                    return new MockResponse();
                } else {
                    try (
                            final InputStream inputStream = assetManager.open(contentMapping.filePath);
                            final BufferedSource bufferedSource = Okio.buffer(Okio.source(inputStream))
                    ) {
                        byte[] bytes = bufferedSource.readByteArray();
                        @NonNull final MockResponse response = new MockResponse();
                        try (@NonNull final Buffer body = new Buffer().write(bytes)) {
                            response.setBody(body);
                        }
                        response.setHeader("Content-Type", contentMapping.contentType);
                        return response;
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }
}
