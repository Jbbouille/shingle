package org.nyaraka.jaxrs.resources;

import static org.nyaraka.utils.ClassUtils.applyOnMetaAnnotation;
import static java.util.Arrays.asList;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.nyaraka.Nyaraka;
import org.nyaraka.annotations.UniqueIdentifier;
import org.nyaraka.utils.PathUtils;
import org.nyaraka.model.Parameter;
import org.nyaraka.model.Resource;
import org.nyaraka.model.Response;

public class MethodResource {
    private final Method method;
    private final WrapperResource parent;
    private final Nyaraka nyaraka;

    public MethodResource(Method method, WrapperResource parent, Nyaraka nyaraka) {
        this.method = method;
        this.parent = parent;
        this.nyaraka = nyaraka;
    }

    public Resource resource() {
        Resource.ResourceBuilder builder = Resource.builder()
                                                   .path(path())
                                                   .verb(verb())
                                                   .inputs(inputs())
                                                   .addOutputs(outputs())
                                                   .consumes(consumes());

        UniqueIdentifier uniqueIdentifier = method.getAnnotation(UniqueIdentifier.class);
        if (uniqueIdentifier != null) {
            builder.id(uniqueIdentifier.value());
        }

        nyaraka.enrichResource(method, builder);

        return builder.build();
    }

    private String verb() {
        return applyOnMetaAnnotation(method, HttpMethod.class, (m, a) -> a.value())
                .orElseThrow(() -> new IllegalStateException("Should be unreachable, Method should be annotated with an annotation annotated with HttpMethod"));
    }

    public String path() {
        String thisPath = "";
        Path pathAnnotation = method.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            thisPath = pathAnnotation.value();
        }
        return PathUtils.join(parent.path(), thisPath);
    }

    public Collection<String> consumes() {
        Consumes pathAnnotation = method.getAnnotation(Consumes.class);
        if (pathAnnotation != null) {
            return asList(pathAnnotation.value());
        }
        return parent.consumes();
    }

    private List<Parameter> inputs() {
        return Arrays.stream(method.getParameters())
                     .flatMap(this::createParameterInputs)
                     .collect(Collectors.toList());
    }

    private Stream<Parameter> createParameterInputs(java.lang.reflect.Parameter p) {
        Parameter simpleInput = createSimpleInput(p, p.getType());
        if (simpleInput != null) {
            nyaraka.enrichInput(p, simpleInput);
            return Stream.of(simpleInput);
        }

        BeanParam beanParam = p.getAnnotation(BeanParam.class);
        if (beanParam != null) {
            return createBeanParamInputs(p, nyaraka);
        }

        Parameter.BodyParameter bodyParameter = new Parameter.BodyParameter(p.getName(), nyaraka.newModel(p.getParameterizedType()));
        nyaraka.enrichInput(p, bodyParameter);
        return Stream.of(bodyParameter);
    }

    private Parameter createSimpleInput(AnnotatedElement annotatedElement, Class<?> modelType) {
        String defaultValue = null;
        DefaultValue defaultValueAnnotation = annotatedElement.getAnnotation(DefaultValue.class);
        if (defaultValueAnnotation != null) {
            defaultValue = defaultValueAnnotation.value();
        }
        PathParam pathParam = annotatedElement.getAnnotation(PathParam.class);
        if (pathParam != null) {
            return new Parameter.PathParameter(pathParam.value(), nyaraka.newModel(modelType))
                    .withDefaultValue(defaultValue);
        }
        QueryParam queryParam = annotatedElement.getAnnotation(QueryParam.class);
        if (queryParam != null) {
            return new Parameter.QueryParameter(queryParam.value(), nyaraka.newModel(modelType))
                    .withDefaultValue(defaultValue);
        }
        HeaderParam headerParam = annotatedElement.getAnnotation(HeaderParam.class);
        if (headerParam != null) {
            return new Parameter.HeaderParameter(headerParam.value(), nyaraka.newModel(modelType))
                    .withDefaultValue(defaultValue);
        }
        CookieParam cookieParam = annotatedElement.getAnnotation(CookieParam.class);
        if (cookieParam != null) {
            return new Parameter.CookieParameter(cookieParam.value(), nyaraka.newModel(modelType))
                    .withDefaultValue(defaultValue);
        }
        FormParam formParam = annotatedElement.getAnnotation(FormParam.class);
        if (formParam != null) {
            return new Parameter.FormParameter(formParam.value(), nyaraka.newModel(modelType))
                    .withDefaultValue(defaultValue);
        }
        MatrixParam matrixParam = annotatedElement.getAnnotation(MatrixParam.class);
        if (matrixParam != null) {
            return new Parameter.MatrixParameter(matrixParam.value(), nyaraka.newModel(modelType))
                    .withDefaultValue(defaultValue);
        }

        return null;
    }

    private Stream<Parameter> createBeanParamInputs(java.lang.reflect.Parameter p, Nyaraka nyaraka) {
        Class<?> bean = p.getType();
        Stream<Parameter> fields = Arrays.stream(bean.getDeclaredFields())
                                         .map(f -> createSimpleInput(f, f.getType()));

        Stream<Parameter> methods = Arrays.stream(bean.getDeclaredMethods())
                                          .filter(m -> m.getParameterCount() > 0)
                                          .map(m -> createSimpleInput(m, m.getParameters()[0].getType()));

        return Stream.concat(fields, methods)
                     .filter(param -> param != null)
                     .peek(classicalParam -> nyaraka.enrichInput(p, classicalParam));
    }

    private List<Response> outputs() {
        ArrayList<Response> responses = new ArrayList<>();

        Class<?> returnedClass = method.getReturnType();

        if (returnedClass.equals(Void.TYPE)) {
            responses.add(Response.builder().status(204).description("No Content").build());
            return responses;
        }

        if (returnedClass.equals(javax.ws.rs.core.Response.class)) {
            responses.add(Response.builder().status(0).build());
            return responses;
        }

        responses.add(Response.builder()
                              .status(200)
                              .contentTypes(produces())
                              .model(nyaraka.newModel(method.getGenericReturnType()))
                              .build());

        return responses;
    }

    public Collection<String> produces() {
        Produces producesAnnotation = method.getAnnotation(Produces.class);
        if (producesAnnotation != null) {
            return asList(producesAnnotation.value());
        }
        return parent.produces();
    }
}
