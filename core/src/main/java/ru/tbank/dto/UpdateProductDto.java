package ru.tbank.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class UpdateProductDto {
    private String id;
    private Double price;
    private Date date;
}
