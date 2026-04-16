package ru.tbank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UpdateProductPriceRequestDtoList {
    private List<UpdateProductPriceRequestDto> items;
}
