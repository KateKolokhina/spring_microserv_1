package com.naukma.aic.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


public interface ProductZvitByCategoryDTO {

    String getArticle();

    String getName();

    String getProducer();

    Double getPrice();

    Double getVolume();

    Integer getMinAge();

    int getReceiptCount();

    String getNotes();

}
