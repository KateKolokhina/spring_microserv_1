package com.naukma.aic.repository;

import com.naukma.aic.entity.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    void deleteProductByArticle(String article);
    Optional<Product> findProductByArticle(String aLong);
    Optional<Product> findProductByName(String aLong);

    @Query(nativeQuery = true,
            value = "SELECT article, name, producer , price " +
                    "FROM product " +
                    "WHERE category_id = :catId ")
    List<ProductDTO> getProductsInCategory(@Param("catId") Long id);

    @Query(nativeQuery = true,

            value = "SELECT p.article, p.name, p.producer , rp.price_for_line " +
                    "FROM receipt r " +
                    "LEFT JOIN receipt_product rp ON rp.receipt_id = r.id " +
                    "LEFT JOIN product p ON rp.product_article = p.article " +
                    "WHERE r.id = :id")
    List<ProductDTO> getProductsInOrder(@Param("id") Long id);

    @Query(nativeQuery = true,
            value = "SELECT article, name, producer , price " +
                    "FROM product ")
    List<ProductDTO> getAllProducts();

    @Query(nativeQuery = true,
            value = "SELECT p.article,p.name,p.producer,p.price,p.volume," +
                    "p.min_age as minAge,COUNT(rp.receipt_id) as receiptCount ,coalesce(p.notes,'')" +
                    "FROM product p " +
                    "LEFT JOIN receipt_product rp ON p.article = rp.product_article " +
                    "WHERE p.category_id = :catId  " +
                    "AND EXISTS ( " +
                    "SELECT rp.product_article " +
                    "FROM receipt_product rp " +
                    "WHERE rp.product_article = p.article) " +
                    "GROUP BY p.article")
    List<ProductZvitByCategoryDTO> getProductsInCategoryForZvit1(@Param("catId") Long id);
}