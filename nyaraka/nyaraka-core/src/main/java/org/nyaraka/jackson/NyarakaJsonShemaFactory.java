package org.nyaraka.jackson;

import org.nyaraka.Nyaraka;
import org.nyaraka.model.Model;
import com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema;
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;

public class NyarakaJsonShemaFactory extends JsonSchemaFactory {

    private final Nyaraka nyaraka;

    public NyarakaJsonShemaFactory(Nyaraka nyaraka) {
        this.nyaraka = nyaraka;
    }

    @Override
    public StringSchema stringSchema() {
        return new Model.StringModel(nyaraka);
    }

    @Override
    public BooleanSchema booleanSchema() {
        return new Model.BooleanModel(nyaraka);
    }

    @Override
    public IntegerSchema integerSchema() {
        return new Model.IntegerModel(nyaraka);
    }

    @Override
    public NumberSchema numberSchema() {
        return new Model.NumberModel(nyaraka);
    }

    @Override
    public ObjectSchema objectSchema() {
        return new Model.ObjectModel(nyaraka);
    }

    @Override
    public ArraySchema arraySchema() {
        return new Model.ArrayModel(nyaraka);
    }
}
