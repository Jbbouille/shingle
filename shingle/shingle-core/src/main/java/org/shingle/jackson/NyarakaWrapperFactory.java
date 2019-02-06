package org.shingle.jackson;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;
import org.shingle.Shingle;

public class NyarakaWrapperFactory extends WrapperFactory {
    private final Shingle shingle;

    public NyarakaWrapperFactory(Shingle shingle) {
        this.shingle = shingle;
    }

    public SchemaFactoryWrapper getWrapper(SerializerProvider provider) {
        return new NyarakaSchemaFactoryWrapper(shingle, provider);
    }

    public SchemaFactoryWrapper getWrapper(SerializerProvider provider, VisitorContext rvc) {
        NyarakaSchemaFactoryWrapper wrapper = new NyarakaSchemaFactoryWrapper(shingle, provider);
        wrapper.setVisitorContext(rvc);
        return wrapper;
    }
}