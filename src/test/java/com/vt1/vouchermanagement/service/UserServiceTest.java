package com.vt1.vouchermanagement.service;

import com.vt1.vouchermanagement.dto.CreateUserRequest;
import com.vt1.vouchermanagement.dto.UserResponse;
import com.vt1.vouchermanagement.entity.User;
import com.vt1.vouchermanagement.exception.ApiException;
import com.vt1.vouchermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createSuccess() {
        CreateUserRequest request = new CreateUserRequest("Nguyen Van A", "A@GMAIL.COM", "0909");
        when(userRepository.existsByEmailIgnoreCase("A@GMAIL.COM")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = userService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("a@gmail.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createRejectsDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest("Nguyen Van A", "a@gmail.com", null);
        when(userRepository.existsByEmailIgnoreCase("a@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.getMessage()).isEqualTo("Email already exists");
                });
    }

    @Test
    void createRejectsInvalidEmail() {
        CreateUserRequest request = new CreateUserRequest("Nguyen Van A", "bad-email", null);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(exception.getMessage()).isEqualTo("Email format is invalid");
                });
    }
}
