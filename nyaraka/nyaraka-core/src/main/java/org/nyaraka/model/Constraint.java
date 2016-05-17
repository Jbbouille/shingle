package org.nyaraka.model;

import java.util.Map;

public class Constraint {
    public String name;
    public Map<String, String> args;

    public Constraint(String name, Map<String, String> args) {
        this.name = name;
        this.args = args;
    }


}
