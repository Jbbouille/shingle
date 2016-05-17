package org.nyaraka.jaxrs;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.nyaraka.model.Parameter.Type.body;
import static org.nyaraka.model.Parameter.Type.cookie;
import static org.nyaraka.model.Parameter.Type.form;
import static org.nyaraka.model.Parameter.Type.header;
import static org.nyaraka.model.Parameter.Type.matrix;
import static org.nyaraka.model.Parameter.Type.path;
import static org.nyaraka.model.Parameter.Type.query;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.assertj.core.api.Condition;
import org.junit.Test;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nyaraka.Extension;
import org.nyaraka.Nyaraka;
import org.nyaraka.annotations.UniqueIdentifier;
import org.nyaraka.model.Documentation;
import org.nyaraka.model.Parameter;
import org.nyaraka.model.Resource;
import org.nyaraka.model.Response;

public class DocumentationBuilderTest {

    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod("SMELL")
    @interface SMELL {
    }

    @Test
    public void should_exclude_some_classes_from_documentation() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa")
            public String aaaMethod() {
                return "aa";
            }
        }

        @Path("/")
        class Bbb {
            @PUT
            @Path("Bbb")
            public String bbbMethod() {
                return "bb";
            }
        }
        List<Class<?>> classes = Arrays.asList(Aaa.class, Bbb.class);
        Nyaraka nyaraka = Nyaraka.builder()
                                 .extension(new Extension() {
                                     @Override
                                     public boolean accept(Class<?> clazz) {
                                         return !clazz.getSimpleName().startsWith("A");
                                     }
                                 })
                                 .build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).doesNotContain("/Aaa");
    }

    @Test
    public void should_exclude_some_method_from_documentation() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa")
            public String aaaMethod() {
                return "aa";
            }
        }
        @Path("/")
        class Bbb {
            @PUT
            @Path("Bbb")
            public String bbbMethod() {
                return "bb";
            }
        }

        List<Class<?>> classes = Arrays.asList(Aaa.class, Bbb.class);
        Nyaraka nyaraka = Nyaraka.builder()
                                 .extension(new Extension() {
                                     @Override
                                     public boolean accept(Method method) {
                                         return !method.getName().equals("bbbMethod");
                                     }
                                 })
                                 .build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).doesNotContain("/Bbb");
    }

    @Test
    public void should_only_create_documentation_for_method_with_annotation_HttpMethod() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa")
            public String aaaMethod() {
                return "aa";
            }
        }
        @Path("/")
        class Ccc {
            @Path("Ccc")
            public String cccMethod() {
                return "cc";
            }
        }
        List<Class<?>> classes = Arrays.asList(Aaa.class, Ccc.class);
        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).doesNotContain("/Ccc");
    }

    //--------------------PATH----------------------------------
    @Test
    public void should_prepend_basepath_to_path_of_method() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa")
            public String aaaMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);
        Nyaraka nyaraka = Nyaraka.builder().basePath("myBasePath").build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).isEqualTo(singletonList("/myBasePath/Aaa"));
    }

    @Test
    public void should_prepend_basepath_to_path_of_class() throws Exception {
        // Given
        @Path("Aaa")
        class Aaa {
            @GET
            public String aaaMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);
        Nyaraka nyaraka = Nyaraka.builder().basePath("myBasePath").build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).isEqualTo(singletonList("/myBasePath/Aaa"));
    }

    @Test
    public void should_not_prepend_null_basepath_to_path() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa")
            public String aaaMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);
        Nyaraka nyaraka = Nyaraka.builder().basePath(null).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).isEqualTo(singletonList("/Aaa"));
    }

    @Test
    public void should_concatenate_class_path_and_method_path_annotation_to_path() throws Exception {
        // Given
        @Path("A")
        class Aaa {
            @GET
            @Path("aa")
            public String aaaMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);
        Nyaraka nyaraka = Nyaraka.builder().basePath("myBasePath").build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).isEqualTo(singletonList("/myBasePath/A/aa"));
    }

    @Test
    public void should_not_have_double_slash_in_path() throws Exception {
        @Path("/A/")
        class Aaa {
            @GET
            @Path("/aa")
            public String aaaMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);
        // Given
        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path)
                                                .are(new Condition<String>() {
                                                    @Override
                                                    public boolean matches(String value) {
                                                        return !value.contains("//");
                                                    }
                                                });
    }

    @Test
    public void should_not_create_doc_for_class_without_path_annotation() throws Exception {
        // Given
        class Aaa {
            @GET
            @Path("aa")
            public String aaaMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);
        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).isEmpty();
    }

    //--------------------VERB----------------------------------
    @Test
    public void should_create_documentation_with_http_verbs() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa")
            public String getMethod() {
                return "aa";
            }

            @POST
            @Path("Aaa")
            public String postMethod() {
                return "aa";
            }

            @PUT
            @Path("Aaa")
            public String putMethod() {
                return "aa";
            }

            @HEAD
            @Path("Aaa")
            public String headMethod() {
                return "aa";
            }

            @DELETE
            @Path("Aaa")
            public String deleteMethod() {
                return "aa";
            }

            @OPTIONS
            @Path("Aaa")
            public String optionsMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.verb).containsOnly("PUT", "GET", "DELETE", "POST", "HEAD", "OPTIONS");
    }

    @Test
    public void should_create_documentation_with_custom_http_verbs() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @SMELL
            @Path("Aaa")
            public String aaaMethod() {
                return "aa";
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.verb).containsOnly("SMELL");
    }

    //--------------------INPUTS----------------------------------
    @Test
    public void should_handle_classical_parameters() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @POST
            @Path("Aaa/{pathParam}")
            public void pathMethod(@PathParam("pathParam") String pathParam) {
            }

            @PUT
            @Path("Aaa")
            public void queryMethod(@QueryParam("queryParam") String queryParam) {
            }

            @PUT
            @Path("Aaa")
            public void headerMethod(@HeaderParam("headerParam") String headerParam) {
            }

            @PUT
            @Path("Aaa")
            public void cookieMethod(@CookieParam("cookieClicker") String cookieParam) {
            }

            @PUT
            @Path("Aaa")
            public void formMethod(@FormParam("formParam") String formParam) {
            }

            @PUT
            @Path("Aaa")
            public void matrixMethod(@MatrixParam("matrixParam") String matrixParam) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Parameter> parameters = documentation.getResources()
                                                  .stream()
                                                  .flatMap(r -> r.inputs.stream())
                                                  .collect(Collectors.toList());

        // Then
        assertThat(parameters).extracting(p -> p.type).containsOnly(path, query, header, cookie, form, matrix);
        assertThat(parameters).extracting(p -> p.name).containsOnly("pathParam", "queryParam", "headerParam", "cookieClicker", "formParam", "matrixParam");
    }

    @Test
    public void should_handle_bean_parameters() throws Exception {
        // Given
        @Path("/")
        class MyBean {
            @QueryParam("myData")
            private String data;

            @HeaderParam("myHeader")
            private String header;

            @FormParam("myForm")
            private String form;

            private String id;

            @PathParam("id")
            public void getToto(String id) {
                this.id = id;
            }
        }
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{id}")
            public void method(@BeanParam MyBean myBean) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Parameter> parameters = documentation.getResources()
                                                  .stream()
                                                  .flatMap(r -> r.inputs.stream())
                                                  .collect(Collectors.toList());

        // Then
        assertThat(parameters).extracting(p -> p.name).containsOnly("myData", "myHeader", "myForm", "id");
        assertThat(parameters).extracting(p -> p.type).containsOnly(query, header, form, path);
    }

    @Test
    public void should_handle_default_value_on_classical_parameters() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            public void aaaMethod(@PathParam("path") @DefaultValue("def") String path) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Parameter> parameters = documentation.getResources()
                                                  .stream()
                                                  .flatMap(r -> r.inputs.stream())
                                                  .collect(Collectors.toList());

        // Then
        assertThat(parameters).extracting(p -> p.defaultValue).containsOnly("def");
    }

    @Test
    public void should_handle_body_parameter() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa")
            public void aaaMethod(String body) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Parameter> parameters = documentation.getResources()
                                                  .stream()
                                                  .flatMap(r -> r.inputs.stream())
                                                  .collect(Collectors.toList());

        // Then
        assertThat(parameters).extracting(p -> p.name).containsOnly("arg0");
        assertThat(parameters).extracting(p -> p.type).containsOnly(body);
    }

    @Test
    public void should_handle_body_parameter_with_classical_parameters() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            public void aaaMethod(String body, @PathParam("path") String path) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Parameter> parameters = documentation.getResources()
                                                  .stream()
                                                  .flatMap(r -> r.inputs.stream())
                                                  .collect(Collectors.toList());

        // Then
        assertThat(parameters).extracting(p -> p.name).containsOnly("arg0", "path");
        assertThat(parameters).extracting(p -> p.type).containsOnly(body, path);
    }

    //--------------------OUTPUTS----------------------------------
    @Test
    public void should_add_in_outputs_Produces_annotation_result() throws Exception {
        // Given
        class MyAAA {
        }
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            public MyAAA aaaMethod(String body, @PathParam("path") String path) {
                return new MyAAA();
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<String> contentTypes = documentation.getResources()
                                                 .stream()
                                                 .flatMap(r -> r.outputs.stream())
                                                 .flatMap(r -> r.contentTypes.stream())
                                                 .collect(Collectors.toList());

        // Then
        assertThat(contentTypes).containsOnly("application/json");
    }

    @Test
    public void should_create_empty_response_204_for_Void_return_type() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            public void aaaMethod(String body, @PathParam("path") String path) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(204);
        assertThat(responses).extracting(r -> r.description).containsOnly("No Content");
    }

    @Test
    public void should_add_default_response_for_javax_Response_return_type() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            public javax.ws.rs.core.Response aaaMethod(String body, @PathParam("path") String path) {
                return javax.ws.rs.core.Response.ok().build();
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(0);
    }

    @Test
    public void should_add_method_return_types_in_resource_outputs() throws Exception {
        // Given
        class MyBean {
            public String myField;
        }
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            public MyBean aaaMethod(String body, @PathParam("path") String path) {
                return new MyBean();
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(this::convert)
                             .contains("{\"type\":\"object\",\"id\":\"urn:jsonschema:org:nyaraka:jaxrs:DocumentationBuilderTest:2MyBean\",\"properties\":{\"myField\":{\"type\":\"string\"}}}");
    }

    @Test
    public void should_remove_default_response_when_other_response_is_present() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Produces("application/json")
            public javax.ws.rs.core.Response aaaMethod(String body, @PathParam("path") String path) {
                return javax.ws.rs.core.Response.ok().build();
            }
        }

        class MyExt implements Extension {
            @Override
            public void enrich(Method method, Resource.ResourceBuilder resourceBuilder, Nyaraka nyaraka) {
                resourceBuilder.getOutputs().remove(0);
                resourceBuilder.output(Response.builder().status(200).description("MIB").build());
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().extension(new MyExt()).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<Response> responses = documentation.getResources()
                                                .stream()
                                                .flatMap(r -> r.outputs.stream())
                                                .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(r -> r.status).containsOnly(200);
    }

    //--------------------CONSUME----------------------------------
    @Test
    public void should_add_in_resource_consume_annotation_values_of_method() throws Exception {
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            @Consumes("application/json")
            public void aaaMethod(String body, @PathParam("path") String path) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<String> responses = documentation.getResources()
                                              .stream()
                                              .flatMap(r -> r.consumes.stream())
                                              .collect(Collectors.toList());

        // Then
        assertThat(responses).contains("application/json");
    }

    @Test
    public void should_add_in_resource_consume_annotation_values_of_class() throws Exception {
        // Given
        @Consumes("application/json")
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            public void aaaMethod(String body, @PathParam("path") String path) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);
        List<String> responses = documentation.getResources()
                                              .stream()
                                              .flatMap(r -> r.consumes.stream())
                                              .collect(Collectors.toList());

        // Then
        assertThat(responses).contains("application/json");
    }

    @Test
    public void should_add_unique_id_when_specified() throws Exception {
        // Given
        @Path("/")
        class AResource {
            @GET
            @Path("a")
            @UniqueIdentifier("a1")
            public void a1() {
            }

            @GET
            @Path("a")
            @UniqueIdentifier("a2")
            public void a2() {
            }
        }
        List<Class<?>> classes = singletonList(AResource.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting("id").containsOnly("a1", "a2");
    }

    //--------------------EXTENSIONS----------------------------------
    @Test
    public void should_handle_extensions() throws Exception {
        // Given
        @Consumes("application/json")
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            public void aaaMethod(@PathParam("path") String path) {
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().extension(new Extension() {
            @Override
            public void enrich(Method method, Resource.ResourceBuilder resourceBuilder, Nyaraka nyaraka) {
                resourceBuilder.path(resourceBuilder.getPath() + "/prepend/special/path");
            }
        }).build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).containsOnly("/Aaa/{path}/prepend/special/path");
    }

    @Test
    public void should_manage_XmlElementWrapper() throws Exception {
        class TypeModel {
        }
        class Types {
            @XmlElementWrapper(name = "identifier-list")
            @XmlElement(name = "identifier")
            private List<TypeModel> typeModel = new ArrayList<>();
        }
        // Given
        @Path("/")
        class Aaa {
            @GET
            @Path("Aaa/{path}")
            public Types aaaMethod(@PathParam("path") String path) {
                return null;
            }
        }
        List<Class<?>> classes = singletonList(Aaa.class);

        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        List<Response> responses = documentationBuilder.execute(classes).getResources().stream()
                                                       .flatMap(r -> r.outputs.stream())
                                                       .collect(Collectors.toList());

        // Then
        assertThat(responses).extracting(this::convert)
                             .are(new Condition<String>() {
                                 @Override
                                 public boolean matches(String value) {
                                     return value.contains("identifier-list");
                                 }
                             });
    }

    private String convert(Response r) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            return objectMapper.writeValueAsString(r.model.schema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}