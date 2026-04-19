package ru.tbank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePriceResponseList {
    private List<UpdatePriceResponse> responses;
}
