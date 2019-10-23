# Shingle

## Introduction 
Shingle is a library design to build a documentation from annotation, it is similar to [Swagger](http://swagger.io/) (more lightweight). It can interact with some of the swagger Annotation. It is fast, clean and extensible (as usual with new libraries). 

## Shingle code organisation
The shingle repository is divided in 4 parts:
- Core: The entry point of the core of shingle is the class `org.shingle.jaxrs.DocumentationBuilder`.
- Shingle-maven-plugin: This a plugin that can be use in java projects in order to build documentation with Maven and Shingle.
- Shingle-UI: The UI.

## How to use shingle-maven-plugin
In order to use it insert this below in your pom.xml:
```xml
<plugin>
<groupId>org.shingle</groupId>
<artifactId>shingle-maven-plugin</artifactId>
<version>${shingle.version}</version>
<executions>
    <execution>
        <id>generate-shingle-doc</id>
        <goals>
            <goal>generate-shingle-doc</goal>
        </goals>
        <phase>compile</phase>
        <configuration>
            <outputPath>/path/to/future/shingle/file.json</outputPath>
            <packagesToScan>
                <packageToScan>org.example.rest.api</packageToScan>
            </packagesToScan>
            <extensions>
                <extension>org.example.my.first.ShingleExtension</extension>
                <extension>org.example.my.second.ShingleExtension</extension>
            </extensions>
        </configuration>
    </execution>
</executions>
</plugin>
```
Available configuration options:
* `outputPath`. Required: **true**. The path where the generated documentation file will go.
* `packagesToScan`. Required: **true**. The package where Shingle will find JAXRS information to scan.
* `apiBasePath`. Required: **false**. Path of the API that don't change.
* `extensions`. Required: **false**. The reference of the personal Shingle extensions.
* `prettyPlease`. Required: **false**. Toggle it with *true* or *false* to indent the json output.

## How to use it as a library
You can take example as below or in the [code](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-maven-plugin/src/main/java/org/shingle/mojo/TheMojo.java#L108-L131).
```java
Shingle shingle = Shingle.builder() // Shingle is the 'context' Object that contains the configuration info for the generated documentation.
                         .extensions(asList(new JavaxValidationExtension(),
                                            new SwaggerExtension())) // Example of an Extension
                         .basePath("myBasePath") // Path of the API that don't change and that cannot be retrieve from @Path Annotation
                         .extensions(Collections.singletonList(new ShingleExtension());)
                         .build();

Documentation = new DocumentationBuilder(shingle) // DocumentationBuilder need shingle 'context' Object
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
Extension `org.shingle.Extension` is an interface that you may use if you want to extend the behavior of Shingle. The interface has 5 default methods for 5 different uses cases:
- `accept(Class clazz)`. To filter the resource classes.
- `accept(Method method)`. To filter the resource methods.
- `enrich(BeanProperty beanProperty, Model.IModel model)`. If you want to improve documentation on the model.
- `enrich(java.lang.reflect.Parameter javaParameter, Parameter parameter)`. If you want to improve documentation on the parameter of the method.
- `enrich(Method method, Resource.ResourceBuilder resourceBuilder, Shingle shingle)`. If you want to improve or add documentation annotated to the method, for example a custom annotation.

### Existing extensions
There is 5 existing extensions that are use internally and enabled by default:
- GenericAnnotationExtension. Used to create extension for custom annotation, example : 
```java
public class MyExtension implements Extension {
    @Override
    public void enrich(Method method, Resource.ResourceBuilder resourceBuilder, Shingle shingle) {
        handleSecurityAnnotations(method, resourceBuilder, shingle);
        handleOtherAnnotations(method, resourceBuilder, shingle);
    }

    private void handleOtherAnnotations(Method method, Resource.ResourceBuilder builder, Shingle shingle) {
        GenericAnnotationExtension.forAnnotation(Legacy.class, "This is a legacy API.").enrich(method, builder, shingle);
        GenericAnnotationExtension.forAnnotation(Deprecated.class).enrich(method, builder, shingle);
    }

    private void handleSecurityAnnotations(Method method, Resource.ResourceBuilder builder, Shingle shingle) {
        GenericAnnotationExtension.forAnnotation(NotSecured.class, "This API is not secured").enrich(method, builder, shingle);
    }
}
```
- JavaxValidationExtension. Used for `javax.validation`. See [code](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-core/src/main/java/org/shingle/extensions/JavaxValidationExtension.java).
- SwaggerExtension. Used for handle [Swagger annotations](https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X).
- ShingleExtension. Used for handle [Shingle annotations](https://github.com/jbbouille/shingle/tree/master/shingle/shingle-annotations/src/main/java/org/shingle/annotations).
- XmlJavaTypeAdapterExtension. Used for generating examples. See [code](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-core/src/main/java/org/shingle/extensions/XmlJavaTypeAdapterExtension.java).

### Extension creation
You may want to create an extension for doing that just implement the `org.shingle.Extension` interface and enrich the ResourceBuilder with the information you need.

With the extension you can:
- Filter some class resource that Shingle will not scan. See [example](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-core/src/test/java/org/shingle/jaxrs/DocumentationBuilderTest.java#L59-L94).
- Filter some method resource that Shingle will not scan. See [example](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-core/src/test/java/org/shingle/jaxrs/DocumentationBuilderTest.java#L98-L133).
- Customize your documentation at the method level. See [example](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-core/src/test/java/org/shingle/jaxrs/DocumentationBuilderTest.java#L798-L823).
- Customize your documentation at parameter level. See [example](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-core/src/test/java/org/shingle/extensions/SwaggerExtensionTest.java#L202-L228).
- Improve documentation on the model. See [example](https://github.com/jbbouille/shingle/blob/master/shingle/shingle-core/src/main/java/org/shingle/extensions/JavaxValidationExtension.java#L28).