package com.project.service.user;

import com.project.configuration.jwt.JwtUtil;
import com.project.configuration.security.UserDetailsServiceImpl;
import com.project.helper.exception.ResourceNotFoundException;
import com.project.helper.payload.email.Email;
import com.project.helper.payload.user.AuthenticationResponse;
import com.project.helper.payload.user.LoginRequest;
import com.project.helper.payload.user.RegisterRequest;
import com.project.model.user.User;
import com.project.model.user.VerificationToken;
import com.project.repository.user.UserRepository;
import com.project.repository.user.VerificationTokenRepository;
import com.project.service.mail.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    public static String activationEmail;
    private final UserRepository userRepo;
    private final VerificationTokenRepository tokenRepo;
    private final ModelMapper mapper;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    //create a user and send the verification email
    @Transactional
    public void save(RegisterRequest registerRequest) {
        User user = mapper.map(registerRequest, User.class);
        user.setCreatedAt(Instant.now());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User savedUser = userRepo.save(user);
        String token = generateVerificationToken(user);
        mailService.sendEmail(new Email(
                "Please Activate Your Account",
                user.getEmail(),
                "Thank you for signing up to spring reddit app!" +
                "please click the url below to activate ur account: " + "http://localhost:8080/api/v1/auth/user/accountVerification/" + token));
        activationEmail = "http://localhost:8080/api/v1/auth/user/accountVerification/" + token;
        log.info(activationEmail);
        mapper.map(savedUser, RegisterRequest.class);
    }

    private String generateVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(user);
        tokenRepo.save(verificationToken);
        return verificationToken.getToken();
    }

    //verify sent email i.e. set the user as enabled
    @Transactional
    public void verifyAccount(String token) {
        VerificationToken verificationToken = tokenRepo.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Token", "token", token));
        String username = verificationToken.getUser().getUsername();
        User user = userRepo.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "Username", username));
        user.setEnabled(true);
        userRepo.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        String token = null;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            token = jwtUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        return new AuthenticationResponse(loginRequest.getUsername(), token);
    }

    //get the current logged-in user
    @Transactional
    public User currentUser() {
        Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findUserByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
    }
}