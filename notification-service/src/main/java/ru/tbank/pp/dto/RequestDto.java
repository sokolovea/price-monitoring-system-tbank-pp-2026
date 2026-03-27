package ru.tbank.pp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RequestDto {
    Long chatId;

    @NotBlank
    String productName;

    @NotBlank
    String productPhotoUrl;

    @NotBlank
    String productUrl;
}
