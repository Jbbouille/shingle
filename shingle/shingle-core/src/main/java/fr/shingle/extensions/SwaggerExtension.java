package fr.shingle.extensions;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.shingle.Extension;
import fr.shingle.model.Constraint;
import fr.shingle.model.Model;
import fr.shingle.model.Parameter;
import fr.shingle.model.Resource;
import fr.shingle.model.Response;
import fr.shingle.utils.ClassUtils;
import fr.shingle.utils.StreamUtils;
import fr.shingle.Shingle;
import com.fasterxml.jackson.databind.BeanProperty;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

public class SwaggerExtension implements Extension {

    @Override
    public void enrich(Method method, Resource.ResourceBuilder builder, Shingle shingle) {
        handleApiResponseAnnotations(method, builder, shingle);
        handleApiOperationAnnotations(method, builder, shingle);
        handleApiAnnotations(method, builder, shingle);
    }

    private void handleApiAnnotations(Method method, Resource.ResourceBuilder builder, Shingle shingle) {
        Optional<Api> annotation = Optional.ofNullable(method.getDeclaringClass().getAnnotation(Api.class));
        annotation.ifPresent(a -> {
            if (a.tags() != null && a.tags().length > 0 && a.tags()[0] != null && !a.tags()[0].isEmpty()) {
                builder.tags(Arrays.asList(a.tags()));
            } else if (a.value() != null && !a.value().isEmpty()) {
                String value = a.value().startsWith("/")
                        ? a.value().substring(1)
                        : a.value();
                builder.tags(Collections.singleton(value));
            }
        });
    }

    private void handleApiOperationAnnotations(Method method, Resource.ResourceBuilder builder, Shingle shingle) {
        Optional<ApiOperation> annotation = ClassUtils.searchAnnotationOfType(method, ApiOperation.class);
        annotation.ifPresent(a -> {

            if (!a.consumes().isEmpty()) {
                builder.consume(a.consumes());
            }

            builder.summary(a.value());
            builder.description(a.notes());
        });
    }

    private void handleApiResponseAnnotations(Method method, Resource.ResourceBuilder builder, Shingle shingle) {
        List<Response> methodResponses = methodResponses(method, shingle).collect(toList());

        Set<Integer> methodStatusCodes = methodResponses.stream().map(r -> r.status).collect(toSet());

        if (methodStatusCodes.size() > 0) {
            builder.getOutputs()
                   .removeIf(o -> o.status == 0);
        }

        Stream<Response> exceptionResponses = exceptionResponses(method, shingle);

        builder.setOutputs(
                StreamUtils.concat(
                        builder.getOutputs().stream().filter(o -> !methodStatusCodes.contains(o.status)),
                        methodResponses.stream(),
                        exceptionResponses.filter(o -> !methodStatusCodes.contains(o.status))
                ).collect(Collectors.toList()));
    }

    private Stream<Response> methodResponses(Method method, Shingle shingle) {
        ApiResponses annotation = method.getAnnotation(ApiResponses.class);
        if (annotation == null) {
            return Stream.empty();
        }

        return Arrays.stream(annotation.value()).map(apiResponse -> {

            Class<?> model = apiResponse.response().equals(Void.TYPE)
                    ? method.getReturnType()
                    : apiResponse.response();

            if (model.equals(javax.ws.rs.core.Response.class)) {
                model = null;
            }

            return Response.builder()
                           .status(apiResponse.code())
                           .description(apiResponse.message())
                           .model(shingle.newModel(model))
                           .build();
        });
    }

    private Stream<Response> exceptionResponses(Method method, Shingle shingle) {
        return Arrays.stream(method.getExceptionTypes()).flatMap(
                ex -> {
                    ApiResponses apiResponses = ex.getAnnotation(ApiResponses.class);
                    if (apiResponses == null) {
                        return Stream.empty();
                    }
                    return Arrays.stream(apiResponses.value()).map(
                            apiResponse -> responseFromException(apiResponse, ex, shingle)
                    );
                });
    }

    private Response responseFromException(ApiResponse apiResponse, Class<?> exception, Shingle shingle) {
        Class<?> responseType = apiResponse.response();
        if (responseType.equals(javax.ws.rs.core.Response.class)) {
            responseType = null;
        }
        return Response.builder()
                       .status(apiResponse.code())
                       .description(apiResponse.message().isEmpty() ? exception.getSimpleName() : apiResponse.message())
                       .model(shingle.newModel(responseType))
                       .build();
    }

    @Override
    public void enrich(java.lang.reflect.Parameter javaParameter, Parameter parameter) {
        Optional<ApiParam> apiParam = Optional.ofNullable(javaParameter.getAnnotation(ApiParam.class));
        apiParam.ifPresent(p -> {
            parameter.description = p.value();
            if (p.required() && parameter.constraints.stream().noneMatch(c -> c.name.equals("NotNull"))) {
                parameter.constraints.add(new Constraint("NotNull", null));
            }

            if (!isNullOrEmpty(p.allowableValues())) {
                if (!p.allowableValues().startsWith("range")) {
                    parameter.allowedValues = Splitter.on(",").trimResults().splitToList(p.allowableValues());
                }
            }
        });
    }

    @Override
    public void enrich(BeanProperty beanProperty, Model.IModel model) {
        ApiModelProperty modelProperty = beanProperty.getAnnotation(ApiModelProperty.class);
        if (modelProperty != null) {
            if (!isNullOrEmpty(modelProperty.value())) {
                model.getExtensions().put("description", modelProperty.value());
            }

            if (!isNullOrEmpty(modelProperty.example())) {
                model.getExtensions().put("example", modelProperty.example());
            }
        }
    }
}
