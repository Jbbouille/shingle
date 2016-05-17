package org.nyaraka.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {
    public int status;
    public String description;
    public Model model;
    @Singular
    public List<String> contentTypes;
}