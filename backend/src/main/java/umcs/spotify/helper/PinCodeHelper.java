package umcs.spotify.helper;

import java.util.Random;

public final class PinCodeHelper {

    private static final Random random = new Random();
    private static final char[] digits = "0123456789".toCharArray();

    public static String generateRandomPin(int length) {
        var buffer = new char[length];
        for (var i = 0; i < length; ++i)
            buffer[i] = digits[random.nextInt(digits.length)];
        return new String(buffer);
    }
}
