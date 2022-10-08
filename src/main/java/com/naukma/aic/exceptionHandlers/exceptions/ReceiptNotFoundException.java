package com.naukma.aic.exceptionHandlers.exceptions;

public class ReceiptNotFoundException extends RuntimeException {

    public ReceiptNotFoundException(Long id) {
        super("Не можемо знайти чек з id: " + id);
    }

}
