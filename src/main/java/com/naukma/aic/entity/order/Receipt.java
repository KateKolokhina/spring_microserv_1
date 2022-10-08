package com.naukma.aic.entity.order;

import com.naukma.aic.entity.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "receipt")
public class Receipt implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double totalPrice = 0.0;

    private String notes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "check_id")
    private List<ReceiptProduct> receiptProductList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ipn", nullable = false)
    private User user;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Order( ");
        stringBuilder
                .append("id = ").append(id).append(",")
                .append("user = ").append(user.getEmail()).append(",")
                .append(" products = [");
        receiptProductList.forEach(i -> stringBuilder
                .append("[ name= ").append(i.getProduct().getName()).append(", ")
                .append("amount = ").append(i.getAmount()).append(" , ")
                .append("price = ").append(i.getPriceForOne()).append(" ]"));
        stringBuilder
                .append("], ")
                .append("totalPrice = ").append(totalPrice).append(" )");
        return stringBuilder.toString();
    }
}
