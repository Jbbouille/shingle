package org.nyaraka.jaxrs.resources;

import org.nyaraka.Nyaraka;

public class RootResource implements WrapperResource {
    private final Nyaraka nyaraka;

    public RootResource(Nyaraka nyaraka) {
        this.nyaraka = nyaraka;
    }

    @Override
    public String path() {
        return nyaraka.basePath;
    }
}
