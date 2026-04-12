package ru.tbank.pp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.pp.api.GptApi;
import ru.tbank.pp.model.GptGptResponse;
import ru.tbank.pp.model.GptIdList;
import ru.tbank.pp.service.GptHelperService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class GptApiController implements GptApi {
    private final GptHelperService gptHelperService;
    @Override
    public ResponseEntity<GptGptResponse> gptGetGptHelp(GptIdList gptIdList) {
        return ResponseEntity.of(Optional.of(gptHelperService.getGptResponse(gptIdList)));
    }
}
