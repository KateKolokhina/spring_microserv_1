package com.naukma.aic.repository;

import com.naukma.aic.entity.User;
import com.naukma.aic.entity.UserInfoDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findUserByIpn(String ipn);

    List<User> findUsersByStatus(String status);

    void deleteByIpn(String ipn);

    @Query(nativeQuery = true,
            value = "SELECT * FROM user " +
                    "WHERE user.status IN :statuses")
    List<UserInfoDTO> findAllByStatusIn(@Param("statuses") List<String> statuses);

    @Query(nativeQuery = true,
            value = "SELECT ipn, email, password, status, surname," +
                    "name, middle_name AS middleName, country," +
                    "district, city, street, house, note, telephone, " +
                    "COUNT(rp.id) AS totalOrderCount " +
                    "FROM user u" +
                    "LEFT JOIN receipt rp ON rp.user_ipn = ipn " +
                    "WHERE ipn = :ipn " +
                    "GROUP BY ipn")
    UserInfoDTO findUserDtoByIpn(@Param("ipn") String ipn);

    String helpRequest = " ( SELECT user_ipn, product_article " +
            "FROM receipt " +
            "LEFT JOIN receipt_product ON receipt.id = receipt_product.receipt_id ) ";

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT ipn, email, password, status, surname," +
                    "name, middle_name AS middleName, country," +
                    "district, city, street, house, note, telephone " +
                    "FROM user u " +
                    "LEFT JOIN receipt r ON r.user_ipn = u.ipn " +
                    "LEFT JOIN receipt_product rp ON r.id = rp.receipt_id " +
                    "WHERE NOT EXISTS (" +
                    "SELECT product_article " +
                    "FROM " + helpRequest + "AS S " +
                    "WHERE user_ipn = :ipn AND " +
                    "product_article NOT IN (" +
                    "SELECT product_article " +
                    "FROM " + helpRequest + "AS T " +
                    "WHERE user_ipn = r.user_ipn " +
                    ")" +
                    ") AND r.user_ipn <> :ipn ")
    List<UserInfoDTO> findAllForZvit(@Param("ipn") String ipn);

}