package com.example.commercecartapi.service;

import com.example.commercecartapi.dto.AuthResponse;
import com.example.commercecartapi.dto.LoginRequest;
import com.example.commercecartapi.dto.RegisterRequest;
import com.example.commercecartapi.dto.UserResponse;
import com.example.commercecartapi.entity.User;
import com.example.commercecartapi.exception.ResourceNotFoundException;
import com.example.commercecartapi.exception.UserAlreadyExistsException;
import com.example.commercecartapi.mapper.UserMapper;
import com.example.commercecartapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request){
        boolean isExists = userRepository.existsByEmail(request.getEmail());

        if(!isExists){
            String plainPassword = request.getPassword();
            String hashedPassword = passwordEncoder.encode(plainPassword);
            User newUser = new User(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    hashedPassword
            );

            User savedUser = userRepository.save(newUser);
            return userMapper.toResponse(savedUser);
        } else {
            throw new UserAlreadyExistsException("User with " + request.getEmail() + " is already exists!");
        }
    }

    public UserResponse getUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with " + email + " cannot found!"));

        return userMapper.toResponse(user);
    }

    public AuthResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with " + loginRequest.getEmail() + " cannot found!"));


        String signedToken = jwtService.generateToken(user);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(signedToken);
        return authResponse;
    }
}
