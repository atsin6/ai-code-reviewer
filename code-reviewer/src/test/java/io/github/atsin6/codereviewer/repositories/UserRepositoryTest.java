package io.github.atsin6.codereviewer.repositories;

import io.github.atsin6.codereviewer.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserAndFindById() {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindUserByEmail() {
        User user = User.builder()
                .email("test2@example.com")
                .passwordHash("hashedpassword")
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test2@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getPasswordHash()).isEqualTo("hashedpassword");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertThat(foundUser).isEmpty();
    }
}
