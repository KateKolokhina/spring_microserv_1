package com.naukma.aic.exceptionHandlers.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String id) {
        super("Не можемо знайти продукт з article: " + id);
    }

}
