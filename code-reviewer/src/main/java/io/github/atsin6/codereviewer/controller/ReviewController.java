package io.github.atsin6.codereviewer.controller;

import io.github.atsin6.codereviewer.model.dto.request.ReviewRequest;
import io.github.atsin6.codereviewer.model.dto.response.ReviewResponse;
import io.github.atsin6.codereviewer.services.CodeReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final CodeReviewService codeReviewService;

    public ReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    @PostMapping("/review")
    public ResponseEntity<ReviewResponse> reviewCode(@RequestBody ReviewRequest request) {
        ReviewResponse response = codeReviewService.reviewCode(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Server is running!");
    }
}
