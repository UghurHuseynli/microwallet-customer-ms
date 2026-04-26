package az.abb.customer.service;

import az.abb.customer.dto.request.AccountRequest;
import az.abb.customer.dto.request.LoginRequest;
import az.abb.customer.dto.request.RegisterRequest;
import az.abb.customer.dto.response.AccountResponse;
import az.abb.customer.dto.response.AuthResponse;
import az.abb.customer.entity.User;
import az.abb.customer.enums.Role;
import az.abb.customer.exception.UserAlreadyExistsException;
import az.abb.customer.feign.UserFeignClient;
import az.abb.customer.repository.UserRepository;
import az.abb.customer.security.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    UserFeignClient userFeignClient;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists" + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getUsername());
        AccountResponse feignAccount = userFeignClient.createAccount(savedUser.getId(), AccountRequest.defaultAzn());
        log.info("Account created: {}", feignAccount.getStatus());

        return generateAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user =  userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User credentials are wrong"));

        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn((int) (jwtService.getJwtExpiration() / 1000))
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getUsername())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

}
