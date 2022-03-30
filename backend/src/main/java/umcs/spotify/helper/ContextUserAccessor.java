package umcs.spotify.helper;

import org.springframework.security.core.context.SecurityContextHolder;

public final class ContextUserAccessor {

    public static String getCurrentUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

}