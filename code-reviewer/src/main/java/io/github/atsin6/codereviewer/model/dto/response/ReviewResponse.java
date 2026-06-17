package io.github.atsin6.codereviewer.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String bugs;
    private String performance;
    private String bestPractices;
    private String improvedCode;
}
