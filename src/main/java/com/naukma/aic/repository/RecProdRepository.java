package com.naukma.aic.repository;

import com.naukma.aic.entity.User;
import com.naukma.aic.entity.order.ReceiptProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecProdRepository extends CrudRepository<ReceiptProduct, Long> {
    List<ReceiptProduct> findReceiptProductsById_ReceiptId(@Param("id") Long id);

    List<ReceiptProduct> findAllById_ReceiptIdAndId_ProductIdIn(@Param("id") Long id, Set<String> productId);

    Optional<ReceiptProduct> findReceiptProductsById_ReceiptIdAndId_ProductId(Long id,String productId);

    void deleteReceiptProductById_ReceiptIdAndId_ProductId(Long idRecep, Long article);
}