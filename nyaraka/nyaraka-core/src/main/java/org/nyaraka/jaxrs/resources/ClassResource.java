package org.nyaraka.jaxrs.resources;

import org.nyaraka.Nyaraka;

public class ClassResource extends CompositeResource {

    public ClassResource(Class<?> clazz, WrapperResource parent, Nyaraka nyaraka) {
        super(clazz, clazz.getMethods(), parent, nyaraka);
    }
}
