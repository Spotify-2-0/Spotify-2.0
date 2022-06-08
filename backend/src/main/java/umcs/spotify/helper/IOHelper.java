package umcs.spotify.helper;

import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaObject;

import java.awt.*;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IOHelper {

   public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/spotify2/";

   public static Optional<Duration> getDurationOfMediaFile(File file) {
      try {
         var object = new MultimediaObject(file);
         var multimediaInfo = object.getInfo();
         var duration = multimediaInfo.getDuration();
         return Optional.of(Duration.ofMillis(duration));
      } catch (Exception e) {
         e.printStackTrace();
      }
      return Optional.empty();
   }

   public static boolean isFileJpeg(MultipartFile multipartFile) {
      try (var is = new BufferedInputStream(multipartFile.getInputStream())) {
         var mimeType = URLConnection.guessContentTypeFromStream(is);
         return mimeType.equalsIgnoreCase("image/jpeg");
      } catch (IOException e) {
         return false;
      }
   }

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

   public static File multipartToTempFile(MultipartFile multipartFile) throws IOException {
      var name = Formatter.format("{}_{}", System.currentTimeMillis(), multipartFile.getOriginalFilename());
      var tempFile = new File(TEMP_DIR, name);
      if (!tempFile.getParentFile().exists()) {
         tempFile.mkdirs();
      }
      multipartFile.transferTo(tempFile);
      return tempFile;
   }

   public static Font loadFont(String resourceName) {
      try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
         return Font.createFont(Font.TRUETYPE_FONT, stream);
      } catch (IOException | FontFormatException e) {
         throw new RuntimeException("Could not load " + resourceName, e);
      }
   }

   public static InputStream getResource(String resourceName) throws IOException {
      return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
   }

   public static boolean deleteDir(File file) {
      var contents = file.listFiles();
      if (contents != null) {
         for (var f : contents) {
            deleteDir(f);
         }
      }
      return file.delete();
   }


   public static byte[] readAllBytesResource(String resource) {
      try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
         return stream.readAllBytes();
      } catch (IOException e) {
         throw new RuntimeException("Could not load " + resource, e);
      }
   }

   public static List<String> readAllLinesResource(String resource) {
      try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
         return readAllLines(stream);
      } catch (IOException e) {
         throw new RuntimeException("Could not load " + resource, e);
      }
   }

   public static List<String> readAllLines(InputStream stream) {
      return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.toList());
   }
}