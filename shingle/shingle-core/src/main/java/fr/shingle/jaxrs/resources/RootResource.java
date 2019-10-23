package fr.shingle.jaxrs.resources;

import fr.shingle.Shingle;

public class RootResource implements WrapperResource {
    private final Shingle shingle;

    public RootResource(Shingle shingle) {
        this.shingle = shingle;
    }

    @Override
    public String path() {
        return shingle.basePath;
    }
}
