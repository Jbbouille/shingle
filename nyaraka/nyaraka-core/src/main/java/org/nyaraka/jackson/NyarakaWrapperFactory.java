package org.nyaraka.jackson;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;
import org.nyaraka.Nyaraka;

public class NyarakaWrapperFactory extends WrapperFactory {
    private final Nyaraka nyaraka;

    public NyarakaWrapperFactory(Nyaraka nyaraka) {
        this.nyaraka = nyaraka;
    }

    public SchemaFactoryWrapper getWrapper(SerializerProvider provider) {
        return new NyarakaSchemaFactoryWrapper(nyaraka, provider);
    }

    public SchemaFactoryWrapper getWrapper(SerializerProvider provider, VisitorContext rvc) {
        NyarakaSchemaFactoryWrapper wrapper = new NyarakaSchemaFactoryWrapper(nyaraka, provider);
        wrapper.setVisitorContext(rvc);
        return wrapper;
    }
}