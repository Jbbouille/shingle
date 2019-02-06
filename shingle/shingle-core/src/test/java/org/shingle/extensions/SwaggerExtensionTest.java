package org.shingle.extensions;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.junit.Test;
import org.shingle.Shingle;
import org.shingle.jaxrs.DocumentationBuilder;
import org.shingle.model.Documentation;
import org.shingle.model.Parameter;
import org.shingle.model.Response;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

public class SwaggerExtensionTest {
    @Test
    public void should_remove_default_response_with_code_0_when_other_response_is_present() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            @ApiResponses({
                    @ApiResponse(code = 200, message = "Orange is the new Black")
            })
            public javax.ws.rs.core.Response aaaMethod(String body, @PathParam("path") String path) {
                return javax.ws.rs.core.Response.ok().build();
            }
        }

        List<Class<?>> classes = singletonList(Aaa.class);

        Shingle shingle = Shingle.builder().extension(new SwaggerExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(200);
        assertThat(responses).extracting(r -> r.description).containsOnly("Orange is the new Black");
    }

    @Test
    public void should_remove_default_response_when_other_response_with_same_status_code_is_present() throws Exception {
        // Given
        @Path("/")
        class Resource {
            @GET
            @ApiResponses({
                    @ApiResponse(code = 200, message = "Orange is the new Black")
            })
            public String resource() {
                return "";
            }
        }

        List<Class<?>> classes = singletonList(Resource.class);

        Shingle shingle = Shingle.builder().extension(new SwaggerExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(200);
        assertThat(responses).extracting(r -> r.description).containsOnly("Orange is the new Black");
    }

    @Test
    public void should_accumulate_exceptions() throws Exception {
        @ApiResponses({
                @ApiResponse(code = 400, message = "e1")
        })
        class E1 extends Exception {
        }

        @ApiResponses({
                @ApiResponse(code = 400, message = "e2")
        })
        class E2 extends Exception {
        }

        // Given
        @Path("/")
        class Resource {
            @GET
            @ApiResponses({
                    @ApiResponse(code = 200, message = "Orange is the new Black")
            })
            public String resource() throws E1, E2 {
                return "";
            }
        }

        List<Class<?>> classes = singletonList(Resource.class);

        Shingle shingle = Shingle.builder().extension(new SwaggerExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(200, 400, 400);
        assertThat(responses).extracting(r -> r.description).containsOnly("Orange is the new Black", "e1", "e2");
    }

    @Test
    public void api_response_on_exceptions_are_superseeded_by_method() throws Exception {
        @ApiResponses({
                @ApiResponse(code = 400, message = "e")
        })
        class E extends Exception {
        }


        // Given
        @Path("/")
        class Resource {
            @GET
            @ApiResponses({
                    @ApiResponse(code = 400, message = "m"),
                    @ApiResponse(code = 200, message = "Orange is the new Black")
            })
            public String resource() throws E {
                return "";
            }
        }

        List<Class<?>> classes = singletonList(Resource.class);

        Shingle shingle = Shingle.builder().extension(new SwaggerExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(200, 400);
        assertThat(responses).extracting(r -> r.description).containsOnly("Orange is the new Black", "m");
    }

    @Test
    public void api_response_on_method_are_additive() throws Exception {
        // Given
        @Path("/")
        class Resource {
            @GET
            @ApiResponses({
                    @ApiResponse(code = 400, message = "m1"),
                    @ApiResponse(code = 400, message = "m2"),
                    @ApiResponse(code = 200, message = "Orange is the new Black")
            })
            public String resource() {
                return "";
            }
        }

        List<Class<?>> classes = singletonList(Resource.class);

        Shingle shingle = Shingle.builder().extension(new SwaggerExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(200, 400, 400);
        assertThat(responses).extracting(r -> r.description).containsOnly("Orange is the new Black", "m1", "m2");
    }

    @Test
    public void should_add_api_param() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            public javax.ws.rs.core.Response aaaMethod(@ApiParam(value = "This is the default path") String body, @PathParam("path") String path) {
                return javax.ws.rs.core.Response.ok().build();
            }
        }

        List<Class<?>> classes = singletonList(Aaa.class);

        Shingle shingle = Shingle.builder().extension(new SwaggerExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Parameter> parameters = documentation.getResources()
                                                  .stream()
                                                  .flatMap(r -> r.inputs.stream())
                                                  .collect(Collectors.toList());

        // Then
        assertThat(parameters).extracting(p -> p.description).contains("This is the default path");
    }
}