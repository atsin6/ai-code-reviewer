package io.github.atsin6.codereviewer.security;

import io.github.atsin6.codereviewer.model.entity.User;
import io.github.atsin6.codereviewer.repositories.UserRepository;
import io.github.atsin6.codereviewer.services.CodeReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String validToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        User user = User.builder()
                .email("testsecurity@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .build();
        userRepository.save(user);

        validToken = jwtUtil.generateToken(user.getEmail());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @MockitoBean
    private CodeReviewService codeReviewService;

    @Test
    void shouldPermitHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedReviewRequest() throws Exception {
        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"print('hello')\"}"))
                .andExpect(status().isForbidden()); // or isUnauthorized depending on entry point
    }

    @Test
    void shouldAcceptAuthenticatedReviewRequest() throws Exception {
        Mockito.when(codeReviewService.reviewCode(Mockito.any())).thenReturn(
                io.github.atsin6.codereviewer.model.dto.response.ReviewResponse.builder().bugs("Looks good").build()
        );

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"print('hello')\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer " + validToken + "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"print('hello')\"}"))
                .andExpect(status().isForbidden());
    }
}
