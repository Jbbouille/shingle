package fr.shingle.jaxrs.resources;

import fr.shingle.Shingle;

public class ClassResource extends CompositeResource {

    public ClassResource(Class<?> clazz, WrapperResource parent, Shingle shingle) {
        super(clazz, clazz.getMethods(), parent, shingle);
    }
}
