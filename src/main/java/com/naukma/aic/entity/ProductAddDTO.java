package com.naukma.aic.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
public class ProductAddDTO{
    @NotNull
    @NotBlank
    private String article;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String producer;

    @NotNull
    @Positive
    private Long categoryId;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Positive
    private Double volume;

    @PositiveOrZero
    private Integer minAge;

    private String notes;

}
