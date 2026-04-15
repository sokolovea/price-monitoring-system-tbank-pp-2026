package ru.tbank.pp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionDto {
    public String message;
    public Map<String, Object> info;
}