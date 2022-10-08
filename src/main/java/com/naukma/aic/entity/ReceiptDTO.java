package com.naukma.aic.entity;

import java.time.LocalDate;

public interface ReceiptDTO {

    Long getId();
    String getUser();
    LocalDate getDate();
    Double getTotalPrice();
    int getProductAmount();
    String getNote();

    default double toNormalPrice(){
        if(getTotalPrice() == null){
            return 0.0;
        }else
            return getTotalPrice();
    }
}
