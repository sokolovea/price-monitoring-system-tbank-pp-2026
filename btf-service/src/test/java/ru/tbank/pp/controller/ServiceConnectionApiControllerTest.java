package ru.tbank.pp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionService;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;
import ru.tbank.pp.service.UserNotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ServiceConnectionApiControllerTest {

    private MockMvc mockMvc;
    private UserNotificationService userNotificationService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userNotificationService = mock(UserNotificationService.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ServiceConnectionApiController controller = new ServiceConnectionApiController(userNotificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void checkConnectionStatus_Success() throws Exception {
        ServiceConnectionStatusCheckRequest request = new ServiceConnectionStatusCheckRequest();
        request.setId(1L);
        request.setService(ServiceConnectionService.TELEGRAM);

        when(userNotificationService.checkConnectionStatus(any(ServiceConnectionStatusCheckRequest.class)))
                .thenReturn(true);

        mockMvc.perform(post("/service/check-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void connectService_Success() throws Exception {
        ServiceConnectionConnectRequest request = new ServiceConnectionConnectRequest();
        request.setId(1L);
        request.setService(ServiceConnectionService.TELEGRAM);
        request.setInternalId(12345L);

        doNothing().when(userNotificationService).connectService(any(ServiceConnectionConnectRequest.class));

        mockMvc.perform(post("/service/connect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userNotificationService).connectService(any(ServiceConnectionConnectRequest.class));
    }
}
