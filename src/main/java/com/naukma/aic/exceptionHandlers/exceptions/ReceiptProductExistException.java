package com.naukma.aic.exceptionHandlers.exceptions;

public class ReceiptProductExistException extends RuntimeException {

    public ReceiptProductExistException(Long id, String article) {
        super("Продукт з article:" + article + ", вже є у чеку з id: " + id);
    }

}
