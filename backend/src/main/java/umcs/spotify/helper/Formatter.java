package umcs.spotify.helper;

public class Formatter {

    public static String format(String message, Object... replacements) {
        for (Object replacement : replacements) {
            message = message.replaceFirst("\\{}", replacement.toString());
        }
        return message;
    }

}
