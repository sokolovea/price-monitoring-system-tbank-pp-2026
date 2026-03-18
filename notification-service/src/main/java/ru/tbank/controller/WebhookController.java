package ru.tbank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tbank.service.TgBotService;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final TgBotService botService;

    @PostMapping("/webhook")
    public BotApiMethod<?> handleUpdate(@RequestBody Update update) {
        return botService.onWebhookUpdateReceived(update);
    }
}
