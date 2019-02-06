package org.shingle.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Documentation {
    private List<Resource> resources;
    private String version;
}
