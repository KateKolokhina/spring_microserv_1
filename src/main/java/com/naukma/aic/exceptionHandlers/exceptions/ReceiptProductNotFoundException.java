package com.naukma.aic.exceptionHandlers.exceptions;

public class ReceiptProductNotFoundException extends RuntimeException {

    public ReceiptProductNotFoundException(Long id, String article) {
        super("Не можемо знайти продукт з article:" + article + ", у чеку з id: " + id);
    }

}
