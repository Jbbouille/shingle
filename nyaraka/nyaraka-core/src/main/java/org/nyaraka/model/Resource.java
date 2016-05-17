package org.nyaraka.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public class Resource {
    public String id;
    public String path;
    public String verb;
    public String summary;
    public String description;
    public List<Parameter> inputs;
    public List<String> tags;
    public List<Response> outputs;
    public List<String> consumes;
    public List<ModelExtension> extensions;

    Resource(String id, String path, String verb, String summary, String description, List<Parameter> inputs, List<String> tags, List<Response> outputs, List<String> consumes, List<ModelExtension> extensions) {
        this.id = id;
        this.path = path;
        this.verb = verb;
        this.summary = summary;
        this.description = description;
        this.inputs = inputs;
        this.tags = tags;
        this.outputs = outputs;
        this.consumes = consumes;
        this.extensions = extensions;
    }

    public static ResourceBuilder builder() {
        return new ResourceBuilder();
    }

    @Override
    public String toString() {
        return "Resource{" +
                "  id='" + id + '\'' +
                "  path='" + path + '\'' +
                ", verb='" + verb + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", extensions={" + extensions + '}' +
                '}';
    }

    @Getter
    public static class ResourceBuilder {
        private String id;
        private String path;
        private String verb;
        private String summary;
        private String description;
        private List<Parameter> inputs;
        private ArrayList<String> tags;
        private ArrayList<Response> outputs;
        private ArrayList<String> consumes;
        private ArrayList<ModelExtension> extensions;

        ResourceBuilder() {
        }

        public Resource.ResourceBuilder id(String id) {
            this.id = id;
            return this;
        }

        public Resource.ResourceBuilder path(String path) {
            this.path = path;
            return this;
        }

        public Resource.ResourceBuilder verb(String verb) {
            this.verb = verb;
            return this;
        }

        public Resource.ResourceBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Resource.ResourceBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Resource.ResourceBuilder inputs(List<Parameter> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Resource.ResourceBuilder tag(String tag) {
            if (this.tags == null) this.tags = new ArrayList<String>();
            this.tags.add(tag);
            return this;
        }

        public Resource.ResourceBuilder tags(Collection<String> tags) {
            if (this.tags == null) this.tags = new ArrayList<String>();
            this.tags.addAll(tags);
            return this;
        }

        public Resource.ResourceBuilder output(Response output) {
            if (this.outputs == null) this.outputs = new ArrayList<Response>();
            this.outputs.add(output);
            return this;
        }

        public Resource.ResourceBuilder addOutputs(Collection<Response> outputs) {
            if (this.outputs == null) this.outputs = new ArrayList<Response>();
            this.outputs.addAll(outputs);
            return this;
        }

        public Resource.ResourceBuilder setOutputs(Collection<Response> outputs) {
            if (this.outputs == null) this.outputs = new ArrayList<Response>();
            this.outputs = new ArrayList<Response>(outputs);
            return this;
        }

        public Resource.ResourceBuilder consume(String consume) {
            if (this.consumes == null) this.consumes = new ArrayList<String>();
            this.consumes.add(consume);
            return this;
        }

        public Resource.ResourceBuilder consumes(Collection<String> consumes) {
            if (this.consumes == null) this.consumes = new ArrayList<String>();
            this.consumes.addAll(consumes);
            return this;
        }

        public Resource.ResourceBuilder extension(ModelExtension extension) {
            if (this.extensions == null) this.extensions = new ArrayList<ModelExtension>();
            this.extensions.add(extension);
            return this;
        }

        public Resource.ResourceBuilder extensions(Collection<ModelExtension> extensions) {
            if (this.extensions == null) this.extensions = new ArrayList<ModelExtension>();
            this.extensions.addAll(extensions);
            return this;
        }

        public Resource build() {
            Collections.sort(outputs, (o1, o2) -> o1.status - o2.status);

            return new Resource(id, path, verb, summary, description, inputs, tags, outputs, consumes, extensions);
        }

        public String toString() {
            return "Resource.ResourceBuilder(path=" + this.path + ", verb=" + this.verb + ", summary=" + this.summary + ", description=" + this.description + ", inputs=" + this.inputs + ", tags=" + this.tags + ", outputs=" + this.outputs + ", consumes=" + this.consumes + ", extensions=" + this.extensions + ")";
        }
    }

}
