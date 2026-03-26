package ru.tbank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {
    Long chatId;
    String productName;
    String productPhotoUrl;
    String productUrl;
}
