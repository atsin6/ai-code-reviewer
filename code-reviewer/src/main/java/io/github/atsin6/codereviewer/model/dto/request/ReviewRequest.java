package io.github.atsin6.codereviewer.model.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewRequest {
    private String language;
    private String code;
}
