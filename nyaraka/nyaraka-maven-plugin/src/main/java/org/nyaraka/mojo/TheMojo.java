package org.nyaraka.mojo;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.Path;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.nyaraka.jaxrs.DocumentationBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.reflect.ClassPath;
import org.nyaraka.Extension;
import org.nyaraka.Nyaraka;
import org.nyaraka.extensions.JavaxValidationExtension;
import org.nyaraka.extensions.NyarakaExtension;
import org.nyaraka.extensions.SwaggerExtension;
import org.nyaraka.extensions.XmlJavaTypeAdapterExtension;
import org.nyaraka.model.Documentation;

@Mojo(name = "generate-nyaraka-doc", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class TheMojo extends AbstractMojo {

    private Log log = getLog();

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(required = true)
    private String nyarakaFilePath;

    @Parameter
    private String basePath;

    @Parameter
    private Set<String> extensions;

    @Parameter(required = true)
    private Set<String> packagesToScan;

    @Parameter(defaultValue = "false")
    private boolean prettyPlease;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            doExecute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doExecute() throws Exception {
        URLClassLoader classLoader = createClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        Set<Class<?>> classes = findAnnotatedClasses(classLoader);

        generateNyarakaDoc(classes.stream().collect(toSet()), classLoader);
    }

    private List<Extension> extensions(URLClassLoader classLoader) {
        if (extensions == null || extensions.isEmpty()) {
            return Collections.<Extension>emptyList();
        }

        try {
            return ClassPath.from(classLoader)
                            .getAllClasses()
                            .stream()
                            .filter(c -> extensions.contains(c.getName()))
                            .map(ClassPath.ClassInfo::load)
                            .map(c -> {
                                try {
                                    return (Extension) c.newInstance();
                                } catch (InstantiationException | IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toList());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNyarakaDoc(Set<Class<?>> classes, URLClassLoader classLoader) {
        Nyaraka nyaraka = Nyaraka.builder()
                                 .extensions(asList(new JavaxValidationExtension(),
                                                    new XmlJavaTypeAdapterExtension(),
                                                    new SwaggerExtension(),
                                                    new NyarakaExtension()))
                                 .extensions(extensions(classLoader))
                                 .basePath(basePath)
                                 .build();

        Documentation documentation = new DocumentationBuilder(nyaraka, majorVersion()).execute(classes);

        ObjectMapper objectMapper = new ObjectMapper();

        if (prettyPlease) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            objectMapper.writeValue(new File(nyarakaFilePath), documentation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String majorVersion() {
        String version = project.getPlugin("org.nyaraka:nyaraka-maven-plugin").getVersion();
        return version.split("\\.")[0];
    }

    private Set<Class<?>> findAnnotatedClasses(URLClassLoader classLoader) throws IOException {
        return ClassPath.from(classLoader)
                        .getAllClasses()
                        .stream()
                        .filter(this::isInScannedPackages)
                        .filter(c -> carriesTargetAnnotation(c, Path.class))
                        .map(ClassPath.ClassInfo::load)
                        .collect(toSet());
    }

    private boolean isInScannedPackages(ClassPath.ClassInfo classInfo) {
        String pack = classInfo.getPackageName();
        for (String packageToScan : packagesToScan) {
            if (pack.startsWith(packageToScan)) {
                return true;
            }
        }
        return false;
    }

    private boolean carriesTargetAnnotation(ClassPath.ClassInfo classInfo, Class<?> targetAnnotation) {
        try {
            Class<?> clazz = classInfo.load();
            Collection<Class<?>> parents = classAndSuperClassesAndInterfaces(clazz);
            return parents.stream()
                          .filter(c -> Arrays.stream(c.getAnnotations())
                                             .filter(a -> a.annotationType().equals(targetAnnotation))
                                             .findAny().isPresent())
                          .findAny()
                          .isPresent();
        } catch (NoClassDefFoundError e) {
            log.warn("Skipping class " + classInfo.getName() + ", cause: " + e.getMessage());
            return false;
        }
    }

    private Collection<Class<?>> classAndSuperClassesAndInterfaces(Class<?> clazz) {
        Queue<Class<?>> parents = new LinkedList<>();
        do {
            parents.add(clazz);
            parents.addAll(Arrays.asList(clazz.getInterfaces()));
        } while ((clazz = clazz.getSuperclass()) != null);
        return parents;
    }

    private URLClassLoader createClassLoader() throws DependencyResolutionRequiredException {
        List<URL> compiledElements = compiledElements()
                .map(this::createURL)
                .collect(Collectors.toList());

        URL[] array = compiledElements.toArray(new URL[compiledElements.size()]);

        return new URLClassLoader(array, Thread.currentThread().getContextClassLoader());
    }

    private URL createURL(String url) {
        try {
            return new File(url).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<String> compiledElements() throws DependencyResolutionRequiredException {
        return project.getCompileClasspathElements().stream();
    }
}
