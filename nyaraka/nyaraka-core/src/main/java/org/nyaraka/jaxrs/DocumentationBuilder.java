package org.nyaraka.jaxrs;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Path;
import org.nyaraka.Nyaraka;
import org.nyaraka.jaxrs.resources.ClassResource;
import org.nyaraka.jaxrs.resources.RootResource;
import org.nyaraka.model.Documentation;
import org.nyaraka.model.Resource;

public class DocumentationBuilder {

    private final Nyaraka nyaraka;
    private final String version;

    public DocumentationBuilder(Nyaraka nyaraka, String version) {
        this.nyaraka = nyaraka;
        this.version = version;
    }

    public Documentation execute(Collection<Class<?>> classes) {
        RootResource root = new RootResource(nyaraka);
        List<Resource> resources = classes.stream()
                                          .filter(nyaraka::accept)
                                          .filter(this::hasNoPathAnnotation)
                                          .map(c -> new ClassResource(c, root, nyaraka))
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
