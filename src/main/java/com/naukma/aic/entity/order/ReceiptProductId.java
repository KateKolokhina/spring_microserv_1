package com.naukma.aic.entity.order;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReceiptProductId implements Serializable {

    @Column(name = "product_article", nullable = false)
    private String productId;

    @Column(name = "receipt_id", nullable = false)
    private Long receiptId;

}
