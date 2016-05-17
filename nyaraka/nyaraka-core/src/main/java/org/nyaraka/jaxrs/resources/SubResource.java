package org.nyaraka.jaxrs.resources;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.nyaraka.Nyaraka;

public class SubResource extends CompositeResource {

    public SubResource(Method method, WrapperResource parent, Nyaraka nyaraka) {
        super(method, methods(method), parent, nyaraka);
    }

    public static Method[] methods(Method method) {
        if (method.getReturnType().equals(Class.class) && method.getGenericReturnType() instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) method.getGenericReturnType();

            Type methodGenericType = type.getActualTypeArguments()[0];
            if (methodGenericType instanceof Class) {
                return ((Class<?>) methodGenericType).getMethods();
            }

            if (methodGenericType instanceof TypeVariable) {
                Class<?> aClass = (Class<?>) ((TypeVariable) methodGenericType).getBounds()[0];
                return aClass.getMethods();
            }

            return new Method[0];
        }

        return method.getReturnType().getMethods();
    }
}
