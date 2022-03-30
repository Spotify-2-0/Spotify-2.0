package umcs.spotify.helper;

import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;

public final class AnnotationHelper {

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotation) {
        try {
            Class.forName("org.springframework.core.annotation.AnnotatedElementUtils");
            return AnnotatedElementUtils.getMergedAnnotation(clazz, annotation);
        } catch (ClassNotFoundException e) {
            return clazz.getAnnotation(annotation);
        }
    }

}
