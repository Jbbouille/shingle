package org.nyaraka.model;

import java.util.ArrayList;
import java.util.List;

public class Parameter {
    public enum Type {
        query, path, header, form, cookie, body, matrix
    }

    public Type type;
    public String name;
    public List<Constraint> constraints = new ArrayList<>();
    public Model model;
    public String description;
    public String defaultValue;
    public List<String> allowedValues;

    @Override
    public String toString() {
        return "Parameter{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", constraints=" + constraints +
                ", model=" + model +
                '}';
    }

    public Parameter(Type type, String name, Model model) {
        this.type = type;
        this.name = name;
        this.model = model;
    }

    public Parameter withDefaultValue(String value) {
        this.defaultValue = value;
        return this;
    }

    public static class QueryParameter extends Parameter {

        public QueryParameter(String name, Model model) {
            super(Type.query, name, model);
        }
    }

    public static class PathParameter extends Parameter {

        public PathParameter(String name, Model model) {
            super(Type.path, name, model);
        }
    }

    public static class HeaderParameter extends Parameter {

        public HeaderParameter(String name, Model model) {
            super(Type.header, name, model);
        }
    }

    public static class FormParameter extends Parameter {

        public FormParameter(String name, Model model) {
            super(Type.form, name, model);
        }
    }

    public static class MatrixParameter extends Parameter {

        public MatrixParameter(String name, Model model) {
            super(Type.matrix, name, model);
        }
    }

    public static class CookieParameter extends Parameter {

        public CookieParameter(String name, Model model) {
            super(Type.cookie, name, model);
        }
    }

    public static class BodyParameter extends Parameter {
        public BodyParameter(String name, Model model) {
            super(Type.body, name, model);
        }
    }


}