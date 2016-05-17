# Nyaraka

## Nyaraka 
Nyaraka is a library design to build a documentation from annotation, it is similar to [Swagger](http://swagger.io/) (more lightweight). It can interact with some of the swagger Annotation. It is fast, clean and extensible (as usual with new libraries). 

## Nyaraka code organisation
The nyaraka repository is divided in 4 parts:
- Core: The entry point of the core of nyaraka is the class `org.nyaraka.jaxrs.DocumentationBuilder`.
- Nyaraka-maven-plugin: This a plugin that can be use in java projects in order to build documentation with Maven and Nyaraka.
- test: The test of the library without maven. It contains also Nyaraka utilisation examples and extensions utilisation example.
- UI: The nyaraka's UI. 

## Nyaraka-maven-plugin example
Configuration for server-auth:
```xml
<plugin>
<groupId>org</groupId>
<artifactId>nyaraka-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>generate-nyaraka-doc</id>
            <goals>
                <goal>generate-nyaraka-doc</goal>
            </goals>
            <phase>verify</phase>
            <configuration>
                <nyarakaFilePath>~/server-auth/api/target/auth.json</nyarakaFilePath>
                <packagesToScan>
                    <packageToScan>org.server.auth.api.resource.oauth</packageToScan>
                </packagesToScan>
            </configuration>
        </execution>
        <execution>
            <id>generate-nyaraka-doc-admin</id>
            <goals>
                <goal>generate-nyaraka-doc</goal>
            </goals>
            <phase>verify</phase>
            <configuration>
                <nyarakaFilePath>~/server-auth/api/target/auth-admin.json</nyarakaFilePath>
                <basePath>/admin</basePath>
                <packagesToScan>
                    <packageToScan>org.server.auth.api.resource.admin</packageToScan>
                </packagesToScan>
                <prettyPlease>true</prettyPlease>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## How to use it as a library

```java
Nyaraka nyaraka = Nyaraka.builder() // Nyaraka is the 'context' Object that contains the configuration info for the generated documentation.
                         .extensions(asList(new JavaxValidationExtension(),
                                            new SwaggerExtension())) // Example of an Extension
                         .basePath("myBasePath") // Path of the API that dont change and that cannot be retrieve from @Path Annotation
                         .build();

Documentation = new DocumentationBuilder(nyaraka) // DocumentationBuilder need nyaraka 'context' Object
        .execute(Collections.<Class<?>>singleton(Bar.class)); // Extraction of the documentation from the Bar.class

ObjectMapper objectMapper = new ObjectMapper();
objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
try {
    System.out.println(objectMapper.writeValueAsString(resources)); // Use Jackson to serialize the resources
} catch (JsonProcessingException e) {
    throw new RuntimeException(e);
}
```

## Extensions

### Info
Extension `org.nyaraka.Extension` is an interface tha you may use when you want to extend the behavior of nyaraka. The interface has 3 default method for 3 different cases:
- `accept(Class clazz)` To filter the resource classes.
- `accept(Method method)` To filter the resource methods.
- `enrich(BeanProperty beanProperty, Model.IModel model)` If you want to improve documentation on the model.
- `enrich(java.lang.reflect.Parameter javaParameter, Parameter parameter)` If you want to improve documentation on the parameter of the method.
- `enrich(Method method, Resource.ResourceBuilder resourceBuilder, Nyaraka nyaraka)` If you want to improve or add documentation annotated to the method, for example a custom annotation.

### Existing Extensions
There is 3 existing extensions:
- GenericAnnotationExtension
- JavaxValidationExtension
- SwaggerExtension

### Creation
You may want to create an extension for doing that just implement the `org.nyaraka.Extension` interface and enrich the ResourceBuilder with the information you need. 