package com.naukma.aic.entity.order;

import com.naukma.aic.entity.Product;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="receipt_product")
public class ReceiptProduct implements Serializable {

    @EmbeddedId
    private ReceiptProductId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("productId")
    @JoinColumn(name="product_article")
    private Product product;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Double priceForOne;

    @Column(nullable = false)
    private Double priceForLine = 0.0;

    @Override
    public String toString() {
        return "ReceiptProduct{" +
                "id=" + id +
                ", product= [" + "id = "+ product.getArticle() +", name = "+product.getName()+" ]"+
                ", amount=" + amount +
                '}';
    }
}
