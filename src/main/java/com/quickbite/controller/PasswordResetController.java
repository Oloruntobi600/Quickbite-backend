package com.quickbite.controller;

import com.quickbite.model.PasswordResetToken;
import com.quickbite.model.User;
import com.quickbite.repository.PasswordResetTokenRepository;
import com.quickbite.repository.UserRepository;
import com.quickbite.request.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository; // Repository for password reset tokens
    @Autowired
    private PasswordEncoder passwordEncoder; // For encoding passwords

    @GetMapping("/reset-password")
    public ResponseEntity<String> showResetPasswordPage(@RequestParam("token") String token) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken.isEmpty() || passwordResetToken.get().isExpired()) {
            return new ResponseEntity<>("Invalid or expired token.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Please submit your new password via POST request.", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest request) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken.isEmpty() || passwordResetToken.get().isExpired()) {
            return new ResponseEntity<>("Invalid or expired token.", HttpStatus.BAD_REQUEST);
        }

        // Find the user associated with the token and update their password
        User user = passwordResetToken.get().getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Optionally delete the token after successful password reset
        passwordResetTokenRepository.delete(passwordResetToken.get());

        return new ResponseEntity<>("Password has been reset successfully.", HttpStatus.OK);
    }

}
