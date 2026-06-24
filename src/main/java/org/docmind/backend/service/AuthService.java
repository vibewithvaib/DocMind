package org.docmind.backend.service;

import lombok.RequiredArgsConstructor;
import org.docmind.backend.dto.AuthResponse;
import org.docmind.backend.dto.LoginRequest;
import org.docmind.backend.dto.RegisterRequest;
import org.docmind.backend.entity.User;
import org.docmind.backend.enums.Role;
import org.docmind.backend.exception.InvalidCredentialsException;
import org.docmind.backend.exception.UserAlreadyExistsException;
import org.docmind.backend.exception.UserNotFoundException;
import org.docmind.backend.repository.UserRepository;
import org.docmind.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Email already exists"
            );
        }
        passwordEncoder.encode(request.getPassword());
        User user = User.builder().name(request.getName()).email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(Role.EMPLOYEE)
                .build();
        userRepository.save(user);
        return "User Registered Successfully....";
    }
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(
                                () -> new UserNotFoundException("User Not Found")
                        );
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()
        )) throw new InvalidCredentialsException("Invalid Credentials");

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
