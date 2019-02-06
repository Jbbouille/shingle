package org.shingle.jackson;

import org.shingle.Shingle;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public class NyarakaSchemaFactoryWrapper extends SchemaFactoryWrapper {
    public NyarakaSchemaFactoryWrapper(Shingle shingle) {
        this(shingle, null);
    }

    public NyarakaSchemaFactoryWrapper(Shingle shingle, SerializerProvider provider) {
        super(provider, new NyarakaWrapperFactory(shingle));
        schemaProvider = new NyarakaJsonShemaFactory(shingle);
    }
}
