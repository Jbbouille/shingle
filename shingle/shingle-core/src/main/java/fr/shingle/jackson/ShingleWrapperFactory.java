package fr.shingle.jackson;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;
import com.fasterxml.jackson.module.jsonSchema.factories.WrapperFactory;
import fr.shingle.Shingle;

public class ShingleWrapperFactory extends WrapperFactory {
    private final Shingle shingle;

    public ShingleWrapperFactory(Shingle shingle) {
        this.shingle = shingle;
    }

    public SchemaFactoryWrapper getWrapper(SerializerProvider provider) {
        return new ShingleSchemaFactoryWrapper(shingle, provider);
    }

    public SchemaFactoryWrapper getWrapper(SerializerProvider provider, VisitorContext rvc) {
        ShingleSchemaFactoryWrapper wrapper = new ShingleSchemaFactoryWrapper(shingle, provider);
        wrapper.setVisitorContext(rvc);
        return wrapper;
    }
}