package ru.tbank.pp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tbank.pp.model.GptGptResponse;
import ru.tbank.pp.model.GptIdList;
import ru.tbank.pp.service.GptHelperService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GptApiControllerTest {

    private MockMvc mockMvc;
    private GptHelperService gptHelperService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        gptHelperService = mock(GptHelperService.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        GptApiController controller = new GptApiController(gptHelperService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void gptGetGptHelp_Success() throws Exception {
        GptIdList idList = new GptIdList();
        idList.setIds(List.of(1L, 2L));

        GptGptResponse response = new GptGptResponse();
        response.setGptOpinion("Это хороший товар!");

        when(gptHelperService.getGptResponse(any(GptIdList.class))).thenReturn(response);

        mockMvc.perform(post("/gpt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gptOpinion").value("Это хороший товар!"));
    }
}
