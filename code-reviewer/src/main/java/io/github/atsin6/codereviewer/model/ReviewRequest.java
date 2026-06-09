package io.github.atsin6.codereviewer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewRequest {
    private String language;
    private String code;
}
