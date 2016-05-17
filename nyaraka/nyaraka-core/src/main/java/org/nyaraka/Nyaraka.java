package org.nyaraka;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.nyaraka.model.Model;
import com.fasterxml.jackson.databind.BeanProperty;
import org.nyaraka.model.Parameter;
import org.nyaraka.model.Resource;
import lombok.Builder;
import lombok.Singular;

@Builder
public class Nyaraka {
    @Singular
    private List<Extension> extensions = new ArrayList<>();

    public String basePath;

    public Model newModel(Class<?> clazz) {
        return Model.of(clazz, this);
    }
    public Model newModel(Type type) {
        return Model.of(type, this);
    }

    public void enrichResource(Method method, Resource.ResourceBuilder builder) {
        for (Extension extension : extensions) {
            extension.enrich(method, builder, this);
        }
    }

    public void enrichModel(BeanProperty beanProperty, Model.IModel model) {
        for (Extension modelExtension : extensions) {
            modelExtension.enrich(beanProperty, model);
        }
    }

    public void enrichInput(java.lang.reflect.Parameter javaParameter, Parameter parameter) {
        for (Extension parameterExtension : extensions) {
            parameterExtension.enrich(javaParameter, parameter);
        }
    }

    public boolean accept(Class<?> clazz) {
        return extensions.stream().allMatch(e->e.accept(clazz));
    }

    public boolean accept(Method method) {
        return extensions.stream().allMatch(e->e.accept(method));
    }
}
