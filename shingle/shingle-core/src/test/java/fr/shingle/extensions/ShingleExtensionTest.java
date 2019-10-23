package fr.shingle.extensions;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import fr.shingle.annotations.ApiResponse;
import fr.shingle.model.Resource;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import fr.shingle.Shingle;
import fr.shingle.jaxrs.DocumentationBuilder;
import fr.shingle.model.Documentation;

public class ShingleExtensionTest {

    @Test
    public void api_response_on_method_replaces_status_code() throws Exception {
        // Given
        @Path("/")
        class TestResource {
            @GET
            @ApiResponse(statusCode = 201, message = "created", contentType = "application/shingle")
            public Response resourceMethod() {
                return null;
            }
        }
        Shingle shingle = Shingle.builder().extension(new ShingleExtension()).build();

        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(asList(TestResource.class));

        // Then
        assertThat(documentation.getResources()).hasSize(1);

        Resource resource = documentation.getResources().get(0);

        Assertions.assertThat(resource.outputs).extracting(r -> r.status).containsExactly(201);
        Assertions.assertThat(resource.outputs).extracting(r -> r.description).containsExactly("created");
        Assertions.assertThat(resource.outputs).extracting(r -> r.contentTypes).containsOnly(asList("application/shingle"));
    }

    @Test
    public void api_response_on_exceptions_are_additive() throws Exception {
        // Given
        @ApiResponse(statusCode = 400, message = "e1", contentType = "application/shingle.1")
        class E1 extends RuntimeException {
        }
        @ApiResponse(statusCode = 400, message = "e2", contentType = "application/shingle.2")
        class E2 extends RuntimeException {
        }
        @ApiResponse(statusCode = 401, message = "e3", contentType = "application/shingle.3")
        class E3 extends RuntimeException {
        }
        @Path("/")
        class TestResource {
            @GET
            @ApiResponse(statusCode = 201, message = "created", contentType = "application/shingle")
            public Response resourceMethod() throws E1, E2, E3 {
                return null;
            }
        }
        Shingle shingle = Shingle.builder().extension(new ShingleExtension()).build();

        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(asList(TestResource.class));

        // Then
        assertThat(documentation.getResources()).hasSize(1);

        Resource resource = documentation.getResources().get(0);

        Assertions.assertThat(resource.outputs).extracting(r -> r.status).containsOnly(201, 400, 400, 401);
        Assertions.assertThat(resource.outputs).extracting(r -> r.description).containsOnly("created", "e1", "e2", "e3");
        Assertions.assertThat(resource.outputs).extracting(r -> r.contentTypes).containsOnly(
                asList("application/shingle"), asList("application/shingle.1"), asList("application/shingle.2"), asList("application/shingle.3"));
    }


    @Test
    public void method_apiResponse_annotation_should_override_exception_apiResponse_annotation() throws Exception {
        // Given
        @ApiResponse(statusCode = 400, message = "e1", contentType = "application/shingle.1")
        class E1 extends RuntimeException {
        }
        @ApiResponse(statusCode = 400, message = "e2", contentType = "application/shingle.2")
        class E2 extends RuntimeException {
        }
        @ApiResponse(statusCode = 401, message = "e3", contentType = "application/shingle.3")
        class E3 extends RuntimeException {
        }
        @Path("/")
        class TestResource {
            @GET
            @ApiResponse(statusCode = 201, message = "created", contentType = "application/shingle")
            @ApiResponse(statusCode = 400, message = "m", contentType = "application/method")
            public Response resourceMethod() throws E1, E2, E3 {
                return null;
            }
        }
        Shingle shingle = Shingle.builder().extension(new ShingleExtension()).build();

        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(asList(TestResource.class));

        // Then
        assertThat(documentation.getResources()).hasSize(1);

        Resource resource = documentation.getResources().get(0);

        Assertions.assertThat(resource.outputs).extracting(r -> r.status).containsOnly(201, 400, 401);
        Assertions.assertThat(resource.outputs).extracting(r -> r.description).containsOnly("created", "m", "e3");
        Assertions.assertThat(resource.outputs).extracting(r -> r.contentTypes).containsOnly(
                asList("application/shingle"), asList("application/method"), asList("application/shingle.3"));
    }

    @Test
    public void api_response_on_method_are_additive() throws Exception {
        // Given
        @Path("/")
        class TestResource {
            @GET
            @ApiResponse(statusCode = 204, message = "m", contentType = "application/method")
            @ApiResponse(statusCode = 400, message = "m1", contentType = "application/method1")
            @ApiResponse(statusCode = 400, message = "m2", contentType = "application/method2")
            public void resourceMethod() {

            }
        }
        Shingle shingle = Shingle.builder().extension(new ShingleExtension()).build();

        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(asList(TestResource.class));

        // Then
        assertThat(documentation.getResources()).hasSize(1);

        Resource resource = documentation.getResources().get(0);

        Assertions.assertThat(resource.outputs).extracting(r -> r.status).containsOnly(204, 400, 400);
        Assertions.assertThat(resource.outputs).extracting(r -> r.description).containsOnly("m", "m1", "m2");
        Assertions.assertThat(resource.outputs).extracting(r -> r.contentTypes).containsOnly(
                asList("application/method"), asList("application/method1"), asList("application/method2"));
    }


    @Test
    public void should_remove_default_response_when_other_response_is_present() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            @ApiResponse(statusCode = 200, message = "Orange is the new Black")
            public javax.ws.rs.core.Response aaaMethod(String body, @PathParam("path") String path) {
                return javax.ws.rs.core.Response.ok().build();
            }
        }

        List<Class<?>> classes = singletonList(Aaa.class);

        Shingle shingle = Shingle.builder().extension(new ShingleExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<fr.shingle.model.Response> responses = documentation.getResources()
                                                                  .stream()
                                                                  .flatMap(r -> r.outputs.stream())
                                                                  .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(200);
        assertThat(responses).extracting(r -> r.description).containsOnly("Orange is the new Black");
    }

    @Test
    public void should_not_duplicate_content_type() throws Exception {
        // Given
        @Path("/")
        class TestResource {
            @GET
            @Produces("application/shingle")
            @ApiResponse(statusCode = 200, message = "created", contentType = {"application/shingle", "application/testTest"})
            public String resourceMethod() {
                return null;
            }
        }
        Shingle shingle = Shingle.builder().extension(new ShingleExtension()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(shingle, "1.1");

        // When
        List<String> contentTypes = documentationBuilder.execute(Collections.singletonList(TestResource.class))
                                                        .getResources()
                                                        .stream()
                                                        .flatMap(r -> r.outputs.stream())
                                                        .flatMap(r -> r.contentTypes.stream())
                                                        .collect(Collectors.toList());

        // Then
        assertThat(contentTypes).containsOnly("application/testTest", "application/shingle");
    }
}