package fr.shingle.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema;
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema;
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import fr.shingle.Shingle;
import fr.shingle.jackson.ShingleSchemaFactoryWrapper;

public class Model {

    public interface IModel {
        List<Constraint> getConstraints();

        Map<String, String> getExtensions();
    }

    public final JsonSchema schema;

    public Model(JsonSchema schema) {
        this.schema = schema;
    }

    public Model() {
        this.schema = null;
    }

    public static Model of(Class<?> clazz, Shingle shingle) {
        if (clazz == Void.class) {
            return null;// Or not ?
        }

        ObjectMapper m = new ObjectMapper();
        m.setAnnotationIntrospector(AnnotationIntrospectorPair.create(new JacksonAnnotationIntrospector(), new JaxbAnnotationIntrospector(TypeFactory.defaultInstance())));
        m.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        return create(m.constructType(clazz), shingle, m);
    }

    public static Model of(Type type, Shingle shingle) {
        ObjectMapper m = new ObjectMapper();
        m.setAnnotationIntrospector(AnnotationIntrospectorPair.create(new JacksonAnnotationIntrospector(), new JaxbAnnotationIntrospector(TypeFactory.defaultInstance())));
        m.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        return create(m.constructType(type), shingle, m);
    }

    private static Model create(JavaType type, Shingle shingle, ObjectMapper m) {
        ShingleSchemaFactoryWrapper visitor = new ShingleSchemaFactoryWrapper(shingle);
        try {
            m.acceptJsonFormatVisitor(type, visitor);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
        JsonSchema jsonSchema = visitor.finalSchema();
        return new Model(jsonSchema);
    }

    public static Model empty() {
        return new Model();
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(schema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class NumberModel extends NumberSchema implements IModel {
        private final Shingle shingle;

        @JsonProperty("constraints")
        private List<Constraint> constraints = new ArrayList<>();

        @JsonProperty("extensions")
        private Map<String, String> extensions = new HashMap<>();

        public NumberModel(Shingle shingle) {
            this.shingle = shingle;
        }

        @Override
        public void enrichWithBeanProperty(BeanProperty beanProperty) {
            super.enrichWithBeanProperty(beanProperty);
            shingle.enrichModel(beanProperty, this);
        }

        @Override
        public List<Constraint> getConstraints() {
            return constraints;
        }

        @Override
        public Map<String, String> getExtensions() {
            return extensions;
        }
    }

    public static class IntegerModel extends IntegerSchema implements IModel {
        private final Shingle shingle;

        @JsonProperty("constraints")
        private List<Constraint> constraints = new ArrayList<>();

        @JsonProperty("extensions")
        private Map<String, String> extensions = new HashMap<>();

        public IntegerModel(Shingle shingle) {
            this.shingle = shingle;
        }

        @Override
        public void enrichWithBeanProperty(BeanProperty beanProperty) {
            super.enrichWithBeanProperty(beanProperty);
            shingle.enrichModel(beanProperty, this);
        }

        @Override
        public List<Constraint> getConstraints() {
            return constraints;
        }

        @Override
        public Map<String, String> getExtensions() {
            return extensions;
        }
    }


    public static class StringModel extends StringSchema implements IModel {
        private final Shingle shingle;

        @JsonProperty("constraints")
        private List<Constraint> constraints = new ArrayList<>();

        @JsonProperty("extensions")
        private Map<String, String> extensions = new HashMap<>();

        public StringModel(Shingle shingle) {
            this.shingle = shingle;
        }

        @Override
        public void enrichWithBeanProperty(BeanProperty beanProperty) {
            super.enrichWithBeanProperty(beanProperty);
            shingle.enrichModel(beanProperty, this);
        }

        @Override
        public List<Constraint> getConstraints() {
            return constraints;
        }

        @Override
        public Map<String, String> getExtensions() {
            return extensions;
        }

    }


    public static class BooleanModel extends BooleanSchema implements IModel {
        private final Shingle shingle;

        @JsonProperty("constraints")
        private List<Constraint> constraints = new ArrayList<>();

        @JsonProperty("extensions")
        private Map<String, String> extensions = new HashMap<>();

        public BooleanModel(Shingle shingle) {
            this.shingle = shingle;
        }

        @Override
        public void enrichWithBeanProperty(BeanProperty beanProperty) {
            super.enrichWithBeanProperty(beanProperty);
            shingle.enrichModel(beanProperty, this);
        }

        @Override
        public List<Constraint> getConstraints() {
            return constraints;
        }

        @Override
        public Map<String, String> getExtensions() {
            return extensions;
        }

    }

    public static class ObjectModel extends ObjectSchema implements IModel {
        private final Shingle shingle;

        @JsonProperty("constraints")
        private List<Constraint> constraints = new ArrayList<>();

        @JsonProperty("extensions")
        private Map<String, String> extensions = new HashMap<>();

        public ObjectModel(Shingle shingle) {
            this.shingle = shingle;
        }

        @Override
        public void enrichWithBeanProperty(BeanProperty beanProperty) {
            super.enrichWithBeanProperty(beanProperty);
            shingle.enrichModel(beanProperty, this);
        }

        @Override
        public List<Constraint> getConstraints() {
            return constraints;
        }

        @Override
        public Map<String, String> getExtensions() {
            return extensions;
        }


    }

    public static class ArrayModel extends ArraySchema implements IModel {
        private final Shingle shingle;

        @JsonProperty("constraints")
        private List<Constraint> constraints = new ArrayList<>();

        @JsonProperty("extensions")
        private Map<String, String> extensions = new HashMap<>();

        public ArrayModel(Shingle shingle) {
            this.shingle = shingle;
        }

        @Override
        public void enrichWithBeanProperty(BeanProperty beanProperty) {
            super.enrichWithBeanProperty(beanProperty);
            shingle.enrichModel(beanProperty, this);
        }

        @Override
        public List<Constraint> getConstraints() {
            return constraints;
        }

        @Override
        public Map<String, String> getExtensions() {
            return extensions;
        }

    }
}
