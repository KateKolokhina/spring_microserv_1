package com.naukma.aic.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="category")
public class Category implements Serializable {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;

    private String notes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "article")
    private List<Product> products = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Category( ");
        stringBuilder.append("id = ").append(id).append(",")
                .append("name = ").append(name).append(",")
                .append(" products = [");
        products.forEach(i -> stringBuilder.append(i.getName()).append(", "));
        stringBuilder.append("] )");
        return stringBuilder.toString();
    }
}
