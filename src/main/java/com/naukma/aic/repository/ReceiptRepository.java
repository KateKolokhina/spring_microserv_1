package com.naukma.aic.repository;

import com.naukma.aic.entity.*;
import com.naukma.aic.entity.order.Receipt;
import com.naukma.aic.entity.order.ReceiptProduct;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends CrudRepository<Receipt, Long> {

    @Override
    List<Receipt> findAll();

    List<Receipt> findAllByUserIpn(String ipn);

    @Override
    Optional<Receipt> findById(Long aLong);

    @Query(nativeQuery = true,
            value = "SELECT id, u.email AS user, date, total_price AS totalPrice, " +
                    "coalesce(COUNT(rp.product_article),0) AS productAmount, " +
                    "coalesce(r.notes,'') AS note " +
                    "FROM receipt r " +
                    "LEFT JOIN user u ON u.ipn = r.user_ipn " +
                    "LEFT JOIN receipt_product rp ON rp.receipt_id = r.id " +
                    "GROUP BY r.id")
    List<ReceiptDTO> getAllReceiptsInfo();

    @Query(nativeQuery = true,
            value = "SELECT id, u.email AS user, date, total_price AS totalPrice, " +
                    "coalesce(COUNT(rp.product_article),0) AS productAmount, " +
                    "coalesce(r.notes,'') AS note " +
                    "FROM receipt r " +
                    "LEFT JOIN user u ON u.ipn = r.user_ipn " +
                    "LEFT JOIN receipt_product rp ON rp.receipt_id = r.id " +
                    "WHERE u.ipn = :userIpn " +
                    "GROUP BY r.id")
    List<ReceiptDTO> getAllReceiptsInfo(@Param("userIpn") String userIpn);

    @Query(nativeQuery = true,
            value = "SELECT rp.product_article AS article, p.name AS name, p.producer AS producer, " +
                    "coalesce(rp.amount,'0') as amount, " +
                    "rp.price_for_one AS priceForOne, rp.price_for_line AS priceForLine " +
                    "FROM receipt r " +
                    "LEFT JOIN receipt_product rp ON rp.receipt_id = r.id " +
                    "LEFT JOIN product p ON rp.product_article = p.article " +
                    "WHERE id = :id")
    List<ReceiptProductDTO> getProductsInReceipt(@Param("id") Long id);

    @Query(nativeQuery = true,
            value = "SELECT id, u.email AS user, date, total_price AS totalPrice," +
                    "coalesce(COUNT(rp.product_article),0) AS productAmount, " +
                    "coalesce(r.notes,'') AS note " +
                    "FROM receipt r " +
                    "LEFT JOIN user u ON u.ipn = r.user_ipn " +
                    "LEFT JOIN receipt_product rp ON rp.receipt_id = r.id " +
                    "WHERE id = :id " +
                    "GROUP BY r.id")
    ReceiptDTO getReceiptInfo(@Param("id") Long id);

    @Query(nativeQuery = true,
            value = "SELECT id, u.email AS user, date, total_price AS totalPrice," +
                    "coalesce(COUNT(rp.product_article),0) AS productAmount, " +
                    "coalesce(r.notes,'') AS note " +
                    "FROM receipt r " +
                    "LEFT JOIN user u ON u.ipn = r.user_ipn " +
                    "LEFT JOIN receipt_product rp ON rp.receipt_id = r.id " +
                    "WHERE u.status IN ('ADMIN','USER') " +
                    "GROUP BY r.id")
    List<ReceiptDTO> getAllReceiptsInfoSorted(Pageable pageable);


    @Query(nativeQuery = true,
            value = "SELECT id, u.email AS user, date, total_price AS totalPrice, " +
                    "coalesce(COUNT(rp.product_article),0) AS productAmount, " +
                    "coalesce(r.notes,'') AS note " +
                    "FROM receipt r " +
                    "LEFT JOIN user u ON u.ipn = r.user_ipn " +
                    "LEFT JOIN receipt_product rp ON rp.receipt_id = r.id " +
                    "WHERE u.ipn = :userIpn " +
                    "GROUP BY r.id ")
    List<ReceiptDTO> getAllReceiptsInfoSorted(Pageable pageable, @Param("userIpn") String userIpn);


    String helpRequest =    " ( SELECT user_ipn, product_article " +
            "FROM receipt " +
            "LEFT JOIN receipt_product ON receipt.id = receipt_product.receipt_id ) ";

    @Query(nativeQuery = true,
            value = "SELECT r.user_ipn " +
                    "FROM receipt r " +
                    "LEFT JOIN receipt_product rp ON r.id = rp.receipt_id " +
                    "WHERE NOT EXISTS (" +
                    "SELECT product_article " +
                    "FROM "+helpRequest +
                    "WHERE user_ipn = :ipn AND " +
                    "NOT EXISTS (" +
                    "SELECT product_article " +
                    "FROM "+helpRequest +
                    "WHERE user_ipn = r.user_ipn )"+
                    ")")
    List<String> findAllForZvit(@Param("ipn") String ipn);

}

//    Все покупатели, что купили все товары что и петренко