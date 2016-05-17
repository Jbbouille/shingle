package org.nyaraka.jaxrs.resources;

import static java.util.Collections.emptyList;
import java.util.Collection;

public interface WrapperResource {
    default String path() {
        return "";
    }

    default Collection<String> consumes() {
        return emptyList();
    }

    default Collection<String> produces() {
        return emptyList();
    }
}
