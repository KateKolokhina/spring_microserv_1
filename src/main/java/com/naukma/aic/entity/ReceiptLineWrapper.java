package com.naukma.aic.entity;


import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

@Log4j2
public class ReceiptLineWrapper {

    private ArrayList<ReceiptLineDTO> properties;

    public ReceiptLineWrapper() {
    }

    public ArrayList<ReceiptLineDTO> getProperties() {
        if (this.properties == null) {
            this.properties = new ArrayList<ReceiptLineDTO>();
        }
        return properties;
    }

    public void setProperties(ArrayList<ReceiptLineDTO> properties) {
        this.properties = properties;
    }

    public void newProperty(String article, String name, String producer, Integer amount, Double priceForOne) {
        ReceiptLineDTO newPr = new ReceiptLineDTO(article, name, producer, amount, priceForOne);
        this.getProperties().add(newPr);
    }

    public void printAll() {
        for (ReceiptLineDTO p : getProperties()) {
            log.info(p.toString());
        }
    }
}
