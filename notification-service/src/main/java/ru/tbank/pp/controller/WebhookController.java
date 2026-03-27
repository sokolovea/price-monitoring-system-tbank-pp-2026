package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.pp.dto.RequestDto;
import ru.tbank.pp.service.TgBotService;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final TgBotService botService;

    /**
     * Этот контроллер использует сам телеграм, если отправлять запросы извне работать не будет
     */
    @PostMapping("/callback")
    public BotApiMethod<?> handleUpdate(@RequestBody Update update) {
        return botService.onWebhookUpdateReceived(update);
    }

    /**
     * Контроллер для наших вебхуков, должен вызываться при уменьшении цены до порога, вызывается из основного сервиса
     */
    @PostMapping("/webhook/send")
    public void sendMessage(@RequestBody RequestDto request) throws TelegramApiException {
        botService.executeNotification(request);
    }
}
