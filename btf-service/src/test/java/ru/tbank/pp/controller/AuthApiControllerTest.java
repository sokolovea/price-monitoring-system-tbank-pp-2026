package ru.tbank.pp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tbank.pp.dto.RegisterUserRequest;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.model.AuthenticationAuthRequest;
import ru.tbank.pp.service.JwtService;
import ru.tbank.pp.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthApiControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        AuthApiController controller = new AuthApiController(userService, jwtService, authenticationManager);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");
    }

    @Test
    void register_Success() throws Exception {
        AuthenticationAuthRequest authRequest = new AuthenticationAuthRequest();
        authRequest.setEmail("test@test.com");
        authRequest.setPassword("password123");

        when(userService.register(any(RegisterUserRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_Success() throws Exception {
        AuthenticationAuthRequest authRequest = new AuthenticationAuthRequest();
        authRequest.setEmail("test@test.com");
        authRequest.setPassword("password123");

        Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}
