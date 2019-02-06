package org.shingle.jaxrs.resources;

import org.shingle.Shingle;

public class ClassResource extends CompositeResource {

    public ClassResource(Class<?> clazz, WrapperResource parent, Shingle shingle) {
        super(clazz, clazz.getMethods(), parent, shingle);
    }
}
