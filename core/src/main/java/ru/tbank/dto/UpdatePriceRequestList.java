package ru.tbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdatePriceRequestList {
    private List<UpdatePriceRequest> items;
}
