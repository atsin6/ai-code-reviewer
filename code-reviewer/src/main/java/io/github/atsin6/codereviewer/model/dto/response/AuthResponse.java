package io.github.atsin6.codereviewer.model.dto.response;

public record AuthResponse(
        String token,
        String email
) {
}
