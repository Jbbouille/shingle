package fr.shingle;

import java.lang.reflect.Method;

import fr.shingle.model.Model;
import fr.shingle.model.Parameter;
import fr.shingle.model.Resource;
import com.fasterxml.jackson.databind.BeanProperty;

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
