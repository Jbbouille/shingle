package fr.shingle.model;

import java.util.Map;
import lombok.Getter;

@Getter
public class ModelExtension {
    private final String name;
    private String description;
    private Map<String, Object> values;

    public ModelExtension(String name) {
        this.name = name;
    }

    public ModelExtension(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ModelExtension(String name, Map<String, Object> values) {
        this.name = name;
        this.values = values;
    }

    public ModelExtension(String name, String description, Map<String, Object> values) {
        this.name = name;
        this.description = description;
        this.values = values;
    }

    @Override
    public String toString() {
        return "{ name=" + name + "description=" + description +
                (values == null || values.isEmpty() ? " }" : ", values=" + values + " }");
    }
}