package com.naukma.aic.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ReceiptInfoDTO {
    @NotNull
    @NotBlank
    private String userIpn;

    private String notes;

}
