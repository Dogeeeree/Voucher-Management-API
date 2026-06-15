package com.vt1.vouchermanagement.service;

import com.vt1.vouchermanagement.dto.CreateUserRequest;
import com.vt1.vouchermanagement.dto.PageResponse;
import com.vt1.vouchermanagement.dto.UserResponse;
import com.vt1.vouchermanagement.entity.User;
import com.vt1.vouchermanagement.exception.ApiException;
import com.vt1.vouchermanagement.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(userRepository.findAll(pageable).map(UserResponse::from));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (request.email() == null || !EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Email format is invalid");
        }

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPhone(normalizeNullable(request.phone()));

        return UserResponse.from(userRepository.save(user));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
