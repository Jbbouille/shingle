package org.nyaraka.test;

import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.nyaraka.Nyaraka;
import org.nyaraka.extensions.JavaxValidationExtension;
import org.nyaraka.extensions.nyarakaExtension;
import org.nyaraka.extensions.SwaggerExtension;
import org.nyaraka.jaxrs.DocumentationBuilder;
import org.nyaraka.model.Documentation;
import org.nyaraka.model.Resource;

public class Runner {

    public static void main(String[] args) {

        Nyaraka nyaraka = Nyaraka.builder() // nyaraka is the 'context' Object that contain necessary information for the parsing.
                                 .extensions(asList(new JavaxValidationExtension(),
                                                    new NyarakaExtension(),
                                                    new SwaggerExtension())) // Example of an Extension
                                 .basePath("myBasePath") // Path of the API that dont change and that cannot be retrieve from @Path Annotation
                                 .build();

        DocumentationBuilder documentationBuilder = new DocumentationBuilder(nyaraka, "1"); // DocumentationBuilder need nyaraka 'context' Object
        Documentation documentation = documentationBuilder.execute(Collections.<Class<?>>singleton(Bar.class));// Extraction of the documentation from the Bar.class

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
                    .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        try {
            System.out.println(objectMapper.writeValueAsString(documentation)); // Use Jackson to serialize the resources
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
