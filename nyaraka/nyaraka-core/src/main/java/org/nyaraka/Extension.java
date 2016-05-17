package org.nyaraka;

import java.lang.reflect.Method;
import org.nyaraka.model.Model;
import com.fasterxml.jackson.databind.BeanProperty;
import org.nyaraka.model.Parameter;
import org.nyaraka.model.Resource;

public interface Extension {
    default boolean accept(Class<?> clazz) {
        return true;
    }

    default boolean accept(Method method) {
        return true;
    }

    default void enrich(Method method, Resource.ResourceBuilder resourceBuilder, Nyaraka nyaraka) {
    }

    default void enrich(java.lang.reflect.Parameter javaParameter, Parameter parameter) {
    }

    default void enrich(BeanProperty beanProperty, Model.IModel model) {
    }

}
