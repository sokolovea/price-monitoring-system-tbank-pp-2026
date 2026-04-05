package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.pp.api.UserApi;
import ru.tbank.pp.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserApiController implements UserApi {
    private final UserService userService;
    @Override
    public ResponseEntity<Long> userGetUserId() {
        return ResponseEntity.ok(userService.getIdFromCridentials());
    }
}
