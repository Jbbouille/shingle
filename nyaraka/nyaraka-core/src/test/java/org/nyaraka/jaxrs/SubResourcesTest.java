package org.nyaraka.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.junit.Test;
import org.nyaraka.Nyaraka;
import org.nyaraka.model.Documentation;

public class SubResourcesTest {

    @Test
    public void should_handle_sub_resources_like_jersey() throws Exception {
        // Given

        @Path("aleph")
        class ShittyResource {
            @Path("/bet")
            @Produces("jojo/custom") // ignored
            public SubResource subResource() {
                return new SubResource();
            }

            @GET
            @Produces("jojo/custom")
            public String getIt() {
                return "Got it!";
            }

            @Path("/gimel") // ignored
            @Produces("jojo/custom2")
            @Consumes("jojo/custom2")
            class SubResource {
                @GET
                @Path("/he")
                public String getIt() {
                    return "Got it!";
                }

                @Path("/vav")
                public SubSubResource subResource() {
                    return new SubSubResource();
                }
            }

            class SubSubResource {
                @GET
                @Path("/zayin")
                public String getIt() {
                    return "Got it!";
                }
            }
        }

        List<Class<?>> classes = Collections.singletonList(ShittyResource.class);
        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path)
                                                .containsOnly("/aleph", "/aleph/bet/he", "/aleph/bet/vav/zayin");
    }

    @Test
    public void should_handle_sub_resources_return_class() throws Exception {
        // Given

        @Path("aleph")
        class ShittyResource {

            @Path("/bet")
            public Class<SubResource> subResource() {
                return SubResource.class;
            }

            class SubResource {
                @GET
                @Path("/he")
                public String getIt() {
                    return "Got it!";
                }
            }
        }

        List<Class<?>> classes = Collections.singletonList(ShittyResource.class);
        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.0");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).containsOnly("/aleph/bet/he");
    }

    @Test
    public void should_handle_sub_resources_return_class_with_generic() throws Exception {
        // Given

        @Path("aleph")
        class ShittyResource {

            @Path("/bet")
            public <E extends SubResource> Class<E> subResource() {
                return null;
            }

            class SubResource {
                @GET
                @Path("/he")
                public String getIt() {
                    return "Got it!";
                }
            }
        }

        List<Class<?>> classes = Collections.singletonList(ShittyResource.class);
        Nyaraka nyaraka = Nyaraka.builder().build();
        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1.4");

        // When
        Documentation documentation = documentationBuilder.execute(classes);

        // Then
        assertThat(documentation.getResources()).extracting(r -> r.path).containsOnly("/aleph/bet/he");
    }
}

