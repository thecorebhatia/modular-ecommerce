package org.ecommerce.controller;

import org.ecommerce.data.UserInfoRepository;
import org.ecommerce.dto.AuthRequest;
import org.ecommerce.model.UserInfo;
import org.ecommerce.util.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth") // 🔓 Automatically matched by your .requestMatchers("/api/v1/auth/**").permitAll()
public class AuthController {

    private final UserInfoRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtUtils;

    public AuthController(UserInfoRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // 1. Create User / Sign Up
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        // Hash the password using BCrypt before saving
        String hashedPassword = passwordEncoder.encode(request.password());
        UserInfo newUser = new UserInfo(hashedPassword, request.username());
        userRepository.save(newUser);

        // 🖨️ SYSTEM TERMINAL PRINT OUT: Fetch all and print immediately!
        System.out.println("\n=== 🛠️ CURRENT H2 DATABASE 'USERS' TABLE === ");
        userRepository.findAll().forEach(user -> {
            System.out.println("ID: " + user.getId() + " | Username: " + user.getUsername() + " | Password HASH: " + user.getPassword());
        });
        System.out.println("=============================================\n");

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    // 2. Login User & Generate JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<UserInfo> userOpt = userRepository.findByUsername(request.username());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        String token = jwtUtils.generateToken(request.username());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
