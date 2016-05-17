package org.nyaraka.test;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Group {
    @NotNull
    @JsonProperty(value = "group-name")
    public final String groupName;

    public final List<Child> children;

    @NotNull
    @XmlElement(name = "creation-date")
    public final Date creationDate=new Date();


    public Group(@JsonProperty("group-name") String groupName,
                 @JsonProperty("children")
                 List<Child> children) {
        this.groupName = groupName;
        this.children = children;
    }
}
