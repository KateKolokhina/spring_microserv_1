package com.naukma.aic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptLineDTO {
    String article;
    String name;
    String producer;
    Integer amount;
    Double priceForOne;
}
