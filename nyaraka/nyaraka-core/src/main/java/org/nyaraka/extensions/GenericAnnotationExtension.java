package org.nyaraka.extensions;

import static org.nyaraka.utils.ClassUtils.searchAnnotationOfType;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.nyaraka.Extension;
import org.nyaraka.Nyaraka;
import org.nyaraka.model.ModelExtension;
import org.nyaraka.model.Resource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class GenericAnnotationExtension implements Extension {
    private final Class<?> clazz;
    private final String description;

    private GenericAnnotationExtension(Class<?> clazz, String description) {
        this.clazz = clazz;
        this.description = description;
    }

    public static Extension forAnnotation(Class<?> annot) {
        return new GenericAnnotationExtension(annot, null);
    }

    public static Extension forAnnotation(Class<?> annot, String description) {
        return new GenericAnnotationExtension(annot, description);
    }

    @Override
    public void enrich(Method method, Resource.ResourceBuilder builder, Nyaraka nyaraka) {
        Optional<?> annotation = searchAnnotationOfType(method, clazz);
        annotation.ifPresent(a -> {
            ImmutableMap.Builder<String, Object> values = ImmutableMap.<String, Object>builder();
            Arrays.stream(a.getClass().getDeclaredMethods())
                  .forEach(m -> findFieldsToAdd(a, values, m));
            builder.extension(new ModelExtension(clazz.getSimpleName(), description, values.build()));
        });
    }

    private static void findFieldsToAdd(Object a, ImmutableMap.Builder<String, Object> values, Method m) {
        if (isObjectMethod(m.getName())) {
            return;
        }

        try {
            Object local = m.invoke(a);
            if (local.getClass().isArray()) {
                values.put(m.getName(), Arrays.stream((Object[]) local).map(Object::toString).collect(Collectors.joining(", ")));
            } else {
                values.put(m.getName(), local.toString());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isObjectMethod(String methodName) {
        Set<String> listObject = Sets.newHashSet("equals", "toString", "hashCode", "annotationType");
        return listObject.contains(methodName);
    }

    public static <T extends Annotation> List<T> searchAnnotationsInException(Method method, Class<T> clazz) {
        return Arrays.stream(method.getExceptionTypes())
                     .filter(exception -> exception.getAnnotation(clazz) != null)
                     .map(exception -> exception.getAnnotation(clazz))
                     .collect(Collectors.toList());
    }
}
