package ru.tbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDto {
    Long chatId;

    @NotBlank
    String productName;

    @NotBlank
    String productPhotoUrl;

    @NotBlank
    String productUrl;
}
