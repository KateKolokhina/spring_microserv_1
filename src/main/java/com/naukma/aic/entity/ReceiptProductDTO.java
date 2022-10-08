package com.naukma.aic.entity;


public interface ReceiptProductDTO {

    String getArticle();

    String getName();

    String getProducer();

    Integer getAmount();

    Double getPriceForOne();

    Double getPriceForLine();
}
