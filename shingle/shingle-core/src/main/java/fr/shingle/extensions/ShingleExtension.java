package fr.shingle.extensions;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.shingle.annotations.ApiResponse;
import fr.shingle.annotations.ApiResponses;
import fr.shingle.model.Resource;
import fr.shingle.model.Response;
import fr.shingle.utils.StreamUtils;
import fr.shingle.Extension;
import fr.shingle.Shingle;

public class ShingleExtension implements Extension {

    @Override
    public void enrich(Method method, Resource.ResourceBuilder resourceBuilder, Shingle shingle) {
        handleApiResponseAnnotations(method, resourceBuilder, shingle);
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
        return apiResponses(method).map(apiResponse -> {

            Class<?> model = apiResponse.response().equals(Void.TYPE)
                    ? method.getReturnType()
                    : apiResponse.response();

            if (model.equals(javax.ws.rs.core.Response.class)) {
                model = null;
            }

            return Response.builder()
                           .status(apiResponse.statusCode())
                           .description(apiResponse.message())
                           .model(shingle.newModel(model))
                           .contentTypes(Arrays.asList(apiResponse.contentType()))
                           .build();
        });
    }

    private Stream<Response> exceptionResponses(Method method, Shingle shingle) {
        return Arrays.stream(method.getExceptionTypes()).flatMap(
                ex -> apiResponses(ex).map(
                        apiResponse -> responseFromException(apiResponse, ex, shingle)
                ));
    }

    private Response responseFromException(ApiResponse apiResponse, Class<?> exception, Shingle shingle) {
        Class<?> responseType = apiResponse.response();
        if (responseType.equals(javax.ws.rs.core.Response.class)) {
            responseType = null;
        }
        return Response.builder()
                       .status(apiResponse.statusCode())
                       .description(apiResponse.message().isEmpty() ? exception.getSimpleName() : apiResponse.message())
                       .model(shingle.newModel(responseType))
                       .contentTypes(asList(apiResponse.contentType()))
                       .build();
    }

    private Stream<ApiResponse> apiResponses(AnnotatedElement element) {
        ApiResponse apiResponse = element.getAnnotation(ApiResponse.class);
        if (apiResponse != null) {
            return Stream.of(apiResponse);
        }

        ApiResponses apiResponses = element.getAnnotation(ApiResponses.class);
        if (apiResponses != null) {
            return Arrays.stream(apiResponses.value());
        }

        return Stream.empty();
    }
}
