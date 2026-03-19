package ru.tbank.dto;

import lombok.Data;

@Data
public class RequestDto {
    Long chatId;
    String text;
}
