package org.nyaraka.jaxrs.resources;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nyaraka.Nyaraka;
import org.nyaraka.model.Resource;
import org.nyaraka.utils.ClassUtils;
import org.nyaraka.utils.PathUtils;

abstract class CompositeResource implements WrapperResource {
    private final AnnotatedElement annotatedElement;
    private final Method[] methods;
    private final WrapperResource parent;
    private final Nyaraka nyaraka;

    public CompositeResource(AnnotatedElement annotatedElement, Method[] methods, WrapperResource parent, Nyaraka nyaraka) {
        this.annotatedElement = annotatedElement;
        this.methods = methods;
        this.parent = parent;
        this.nyaraka = nyaraka;
    }

    public List<Resource> resources() {
        List<Resource> res = new ArrayList<>();

        for (Method method : methods) {
            if (isResource(method) && nyaraka.accept(method)) {
                res.add(new MethodResource(method, this, nyaraka).resource());
                continue;
            }

            if (isSubResource(method) && nyaraka.accept(method.getReturnType())) {
                res.addAll(new SubResource(method, this, nyaraka).resources());
            }
        }
        return res;
    }

    private boolean isResource(Method method) {
        return ClassUtils.applyOnMetaAnnotation(method, HttpMethod.class, (m, a) -> m).isPresent();
    }

    private boolean isSubResource(Method method) {
        return method.getAnnotation(Path.class) != null;
    }

    @Override
    public String path() {
        String thisPath = "";

        Path pathAnnotation = annotatedElement.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            thisPath = pathAnnotation.value();
        }

        return PathUtils.join(parent.path(), thisPath);
    }

    @Override
    public Collection<String> consumes() {
        Collection<String> res = emptyList();

        Consumes consumesAnnotation = annotatedElement.getAnnotation(Consumes.class);
        if (consumesAnnotation != null) {
            res = asList(consumesAnnotation.value());
        }

        return res;
    }

    @Override
    public Collection<String> produces() {
        Collection<String> res = emptyList();

        Produces producesAnnotation = annotatedElement.getAnnotation(Produces.class);
        if (producesAnnotation != null) {
            res = asList(producesAnnotation.value());
        }

        return res;
    }
}
