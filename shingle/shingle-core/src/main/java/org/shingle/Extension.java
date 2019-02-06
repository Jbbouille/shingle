package org.shingle;

import java.lang.reflect.Method;
import org.shingle.model.Model;
import com.fasterxml.jackson.databind.BeanProperty;
import org.shingle.model.Parameter;
import org.shingle.model.Resource;

public interface Extension {
    default boolean accept(Class<?> clazz) {
        return true;
    }

    default boolean accept(Method method) {
        return true;
    }

    default void enrich(Method method, Resource.ResourceBuilder resourceBuilder, Shingle shingle) {
    }

    default void enrich(java.lang.reflect.Parameter javaParameter, Parameter parameter) {
    }

    default void enrich(BeanProperty beanProperty, Model.IModel model) {
    }

}
