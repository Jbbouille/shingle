package org.nyaraka.jackson;

import org.nyaraka.Nyaraka;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public class NyarakaSchemaFactoryWrapper extends SchemaFactoryWrapper {
    public NyarakaSchemaFactoryWrapper(Nyaraka nyaraka) {
        this(nyaraka, null);
    }

    public NyarakaSchemaFactoryWrapper(Nyaraka nyaraka, SerializerProvider provider) {
        super(provider, new NyarakaWrapperFactory(nyaraka));
        schemaProvider = new NyarakaJsonShemaFactory(nyaraka);
    }
}
