package org.shingle.jackson;

import org.shingle.Shingle;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public class ShingleSchemaFactoryWrapper extends SchemaFactoryWrapper {
    public ShingleSchemaFactoryWrapper(Shingle shingle) {
        this(shingle, null);
    }

    public ShingleSchemaFactoryWrapper(Shingle shingle, SerializerProvider provider) {
        super(provider, new ShingleWrapperFactory(shingle));
        schemaProvider = new ShingleJsonShemaFactory(shingle);
    }
}
