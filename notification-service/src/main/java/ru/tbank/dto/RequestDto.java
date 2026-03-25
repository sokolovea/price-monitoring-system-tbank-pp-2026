package ru.tbank.dto;

import lombok.Data;

@Data
public class RequestDto {
    Long chatId;
    String productName;
    String productPhoto;
    String productUrl;
}
