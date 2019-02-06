package org.shingle.jackson;

import org.shingle.Shingle;
import org.shingle.model.Model;
import com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema;
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;

public class NyarakaJsonShemaFactory extends JsonSchemaFactory {

    private final Shingle shingle;

    public NyarakaJsonShemaFactory(Shingle shingle) {
        this.shingle = shingle;
    }

    @Override
    public StringSchema stringSchema() {
        return new Model.StringModel(shingle);
    }

    @Override
    public BooleanSchema booleanSchema() {
        return new Model.BooleanModel(shingle);
    }

    @Override
    public IntegerSchema integerSchema() {
        return new Model.IntegerModel(shingle);
    }

    @Override
    public NumberSchema numberSchema() {
        return new Model.NumberModel(shingle);
    }

    @Override
    public ObjectSchema objectSchema() {
        return new Model.ObjectModel(shingle);
    }

    @Override
    public ArraySchema arraySchema() {
        return new Model.ArrayModel(shingle);
    }
}
