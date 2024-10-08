package com.quickbite.controller;

import com.quickbite.config.JwtProvider;
import com.quickbite.model.Cart;
import com.quickbite.model.PasswordResetToken;
import com.quickbite.model.USER_ROLE;
import com.quickbite.model.User;
import com.quickbite.repository.CartRepository;
import com.quickbite.repository.PasswordResetTokenRepository;
import com.quickbite.repository.UserRepository;
import com.quickbite.request.ForgotPasswordRequest;
import com.quickbite.request.LoginRequest;
import com.quickbite.response.AuthResponse;
import com.quickbite.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender emailSender;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            return new ResponseEntity<>("User with this email does not exist.", HttpStatus.NOT_FOUND);
        }

        String token = UUID.randomUUID().toString();

        // Set token expiry (e.g., valid for 24 hours)
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        // Save token to database
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(passwordResetToken);

        // Create reset link with token
        String resetLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/reset-password")
                .queryParam("token", token)
                .toUriString();

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link: " + resetLink);
        emailSender.send(message);

        return new ResponseEntity<>("Password reset email sent successfully.", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse>createUserHandler(@RequestBody User user) throws Exception {

        User isEmailExist = userRepository.findByEmail(user.getEmail());
        if(isEmailExist!=null){
            throw new Exception("Email is already used with another account");
        }
        User createdUser = new User();
        createdUser.setEmail(user.getEmail());
        createdUser.setFullName(user.getFullName());
        createdUser.setRole(user.getRole());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(createdUser);

        Cart cart = new Cart();
        cart.setCustomer(savedUser);
        cartRepository.save(cart);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Register success");
        authResponse.setRole(savedUser.getRole());

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse>signin(@RequestBody LoginRequest req){

        String username = req.getEmail();
        String password = req.getPassword();

        try {

        Authentication authentication = authenticate(username, password);

        Collection<? extends GrantedAuthority>authorities = authentication.getAuthorities();
        String role = authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login success");
        authResponse.setRole(USER_ROLE.valueOf(role));

        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    } catch (BadCredentialsException e) {
        // Handle invalid credentials
        AuthResponse errorResponse = new AuthResponse();
        errorResponse.setMessage("Invalid username or password");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

    } catch (Exception e) {
        // Handle general exceptions (e.g., user not found)
        AuthResponse errorResponse = new AuthResponse();
        errorResponse.setMessage("User not available or some other error occurred");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if(userDetails==null){
            throw new BadCredentialsException("Invalid username.....");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password....");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
