package com.naukma.aic.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="product")
public class Product implements Serializable {
    @Id
    @Column(nullable = false)
    @NotNull
    private String article;

    @Column(unique = true, nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @NotNull
    private String producer;

    @Column(nullable = false)
    @NotNull
    private Double price;

    @Column(nullable = false)
    @NotNull
    private Double volume;

    @Column(nullable = false)
    @NotNull
    private Integer minAge;

    private String notes;

    @ManyToOne
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return article.equals(product.article) && name.equals(product.name) && producer.equals(product.producer) && price.equals(product.price) && volume.equals(product.volume) && minAge.equals(product.minAge) && Objects.equals(notes, product.notes) && category.equals(product.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(article, name, producer, price, volume, minAge, notes, category);
    }
}
