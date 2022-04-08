package umcs.spotify.helper;

import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamHelper {

    public static ByteArrayInputStream copyInputStreamRange(InputStream in, long start, long end) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            var bytesToCopy = end - start + 1;
            var skipped = in.skip(start);

            var buffer = new byte[StreamUtils.BUFFER_SIZE];
            while (bytesToCopy > 0) {
                var newBytesRead = in.read(buffer);
                if (bytesToCopy - newBytesRead > 0) {
                    out.write(buffer, 0, newBytesRead);
                } else {
                    out.write(buffer, 0, (int) (bytesToCopy));
                }

                bytesToCopy -= newBytesRead;
            }

            try (var rangedIn = new ByteArrayInputStream(out.toByteArray())) {
                return rangedIn;
            }
        }
    }
}
