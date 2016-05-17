package org.nyaraka.test;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;
import io.swagger.annotations.ApiModelProperty;

public class Child {
    @NotNull
    @Size(min = 2)
    @ApiModelProperty(example = "lol")
    @Pattern(regexp = "[a-z]+")
    @NotBlank
    public final String childName;

    public Child(String childName) {
        this.childName = childName;
    }
}
