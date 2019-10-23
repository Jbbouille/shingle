package fr.shingle.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class ClassUtils {
    public static <T, A> Optional<T> applyOnMetaAnnotation(Method method, Class<A> metaAnnotation, BiFunction<Method, A, T> function) {
        Annotation[] methodAnnotations = method.getAnnotations();
        for (Annotation methodAnnotation : methodAnnotations) {
            Annotation[] annotationAnnotations = methodAnnotation.annotationType().getAnnotations();
            for (Annotation annotAnnot : annotationAnnotations) {
                if (annotAnnot.annotationType() == metaAnnotation) {
                    return Optional.of(function.apply(method, metaAnnotation.cast(annotAnnot)));
                }
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<T> searchAnnotationOfType(Method method, Class<T> clazz) {
        return Stream.concat(Arrays.stream(method.getAnnotations()),
                             Arrays.stream(method.getDeclaringClass().getAnnotations()))
                     .filter(a -> a.annotationType() == clazz)
                     .map(clazz::cast)
                     .findAny();
    }

}
