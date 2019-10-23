package fr.shingle.jaxrs;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Path;

import fr.shingle.Shingle;
import fr.shingle.jaxrs.resources.ClassResource;
import fr.shingle.jaxrs.resources.RootResource;
import fr.shingle.model.Documentation;
import fr.shingle.model.Resource;

public class DocumentationBuilder {

    private final Shingle shingle;
    private final String version;

    public DocumentationBuilder(Shingle shingle, String version) {
        this.shingle = shingle;
        this.version = version;
    }

    public Documentation execute(Collection<Class<?>> classes) {
        RootResource root = new RootResource(shingle);
        List<Resource> resources = classes.stream()
                                          .filter(shingle::accept)
                                          .filter(this::hasNoPathAnnotation)
                                          .map(c -> new ClassResource(c, root, shingle))
                                          .flatMap(r -> r.resources().stream())
                                          .collect(Collectors.toList());

        return Documentation.builder()
                            .resources(resources)
                            .version(version)
                            .build();
    }

    private boolean hasNoPathAnnotation(Class<?> c) {
        return c.getAnnotation(Path.class) != null;
    }
}
