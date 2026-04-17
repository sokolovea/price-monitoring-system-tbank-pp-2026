package ru.tbank.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePriceResponseList {
    private List<UpdatePriceResponse> items;
}
