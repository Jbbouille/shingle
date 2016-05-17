# Nyaraka

## Introduction 
Nyaraka is a library design to build a documentation from annotation, it is similar to [Swagger](http://swagger.io/) (more lightweight). It can interact with some of the swagger Annotation. It is fast, clean and extensible (as usual with new libraries). 

## Nyaraka code organisation
The nyaraka repository is divided in 4 parts:
- Core: The entry point of the core of nyaraka is the class `org.nyaraka.jaxrs.DocumentationBuilder`.
- Nyaraka-maven-plugin: This a plugin that can be use in java projects in order to build documentation with Maven and Nyaraka.
- test: The test of the library without maven. It contains also Nyaraka utilisation examples and extensions utilisation example.
- UI: The nyaraka's UI. 

## How to use nyaraka-maven-plugin
In order to use it insert this bellow in your pom.xml:
```xml
<plugin>
<groupId>org.nyaraka</groupId>
<artifactId>nyaraka-maven-plugin</artifactId>
<version>${nyaraka.version}</version>
<executions>
    <execution>
        <id>generate-nyaraka-doc</id>
        <goals>
            <goal>generate-nyaraka-doc</goal>
        </goals>
        <phase>compile</phase>
        <configuration>
            <nyarakaFilePath>/path/to/future/nyaraka/file.json</nyarakaFilePath>
            <packagesToScan>
                <packageToScan>org.example.rest.api</packageToScan>
            </packagesToScan>
            <extensions>
                <extension>org.example.my.first.NyarakaExtension</extension>
                <extension>org.example.my.second.NyarakaExtension</extension>
            </extensions>
        </configuration>
    </execution>
</executions>
</plugin>
```
Available configuration options:
* `nyarakaFilePath`. Required: **true**. The path where the generated documentation file will go.
* `packagesToScan`. Required: **true**. The package where Nyaraka will find JAXRS information to scan.
* `basePath`. Required: **false**. Path of the API that don't change.
* `extensions`. Required: **false**. The reference of the personal Nyaraka extensions.
* `prettyPlease`. Required: **false**. Toggle it with *true* or *false* to indent the json output.

## How to use it as a library
You can take example as bellow or in the [code](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-maven-plugin/src/main/java/org/nyaraka/mojo/TheMojo.java#L108-L131).
```java
Nyaraka nyaraka = Nyaraka.builder() // Nyaraka is the 'context' Object that contains the configuration info for the generated documentation.
                         .extensions(asList(new JavaxValidationExtension(),
                                            new SwaggerExtension())) // Example of an Extension
                         .basePath("myBasePath") // Path of the API that don't change and that cannot be retrieve from @Path Annotation
                         .extensions(Collections.singletonList(new NyarakaExtension());)
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
Extension `org.nyaraka.Extension` is an interface that you may use if you want to extend the behavior of Nyaraka. The interface has 5 default methods for 5 different uses cases:
- `accept(Class clazz)`. To filter the resource classes.
- `accept(Method method)`. To filter the resource methods.
- `enrich(BeanProperty beanProperty, Model.IModel model)`. If you want to improve documentation on the model.
- `enrich(java.lang.reflect.Parameter javaParameter, Parameter parameter)`. If you want to improve documentation on the parameter of the method.
- `enrich(Method method, Resource.ResourceBuilder resourceBuilder, Nyaraka nyaraka)`. If you want to improve or add documentation annotated to the method, for example a custom annotation.

### Existing extensions
There is 5 existing extensions that are use internally and enabled by default:
- GenericAnnotationExtension. Used to create extension for custom annotation, example : 
```java
public class MyExtension implements Extension {
    @Override
    public void enrich(Method method, Resource.ResourceBuilder resourceBuilder, Nyaraka nyaraka) {
        handleSecurityAnnotations(method, resourceBuilder, nyaraka);
        handleOtherAnnotations(method, resourceBuilder, nyaraka);
    }

    private void handleOtherAnnotations(Method method, Resource.ResourceBuilder builder, Nyaraka nyaraka) {
        GenericAnnotationExtension.forAnnotation(Legacy.class, "This is a legacy API.").enrich(method, builder, nyaraka);
        GenericAnnotationExtension.forAnnotation(Deprecated.class).enrich(method, builder, nyaraka);
    }

    private void handleSecurityAnnotations(Method method, Resource.ResourceBuilder builder, Nyaraka nyaraka) {
        GenericAnnotationExtension.forAnnotation(NotSecured.class, "This API is not secured").enrich(method, builder, nyaraka);
    }
}
```
- JavaxValidationExtension. Used for `javax.validation`. See [code](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-core/src/main/java/org/nyaraka/extensions/JavaxValidationExtension.java).
- SwaggerExtension. Used for handle [Swagger annotations](https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X).
- NyarakaExtension. Used for handle [Nyaraka annotations](https://github.com/jawher/nyaraka/tree/master/nyaraka/nyaraka-annotations/src/main/java/org/nyaraka/annotations).
- XmlJavaTypeAdapterExtension. Used for generating examples. See [code](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-core/src/main/java/org/nyaraka/extensions/XmlJavaTypeAdapterExtension.java).

### Extension creation
You may want to create an extension for doing that just implement the `org.nyaraka.Extension` interface and enrich the ResourceBuilder with the information you need.

With the extension you can:
- Filter some class resource that Nyaraka will not scan. See [example](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-core/src/test/java/org/nyaraka/jaxrs/DocumentationBuilderTest.java#L59-L94).
- Filter some method resource that Nyaraka will not scan. See [example](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-core/src/test/java/org/nyaraka/jaxrs/DocumentationBuilderTest.java#L98-L133).
- Customize your documentation at the method level. See [example](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-core/src/test/java/org/nyaraka/jaxrs/DocumentationBuilderTest.java#L798-L823).
- Customize your documentation at parameter level. See [example](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-core/src/test/java/org/nyaraka/extensions/SwaggerExtensionTest.java#L202-L228).
- Improve documentation on the model. See [example](https://github.com/jawher/nyaraka/blob/master/nyaraka/nyaraka-core/src/main/java/org/nyaraka/extensions/JavaxValidationExtension.java#L28).