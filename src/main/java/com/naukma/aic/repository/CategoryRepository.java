package com.naukma.aic.repository;

import com.naukma.aic.entity.Category;
import com.naukma.aic.entity.CategoryDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    @Override
    Iterable<Category> findAll();

    @Override
    Optional<Category> findById(Long aLong);

    Optional<Category> findByName(String name);

    @Query(nativeQuery = true,
            value = "SELECT c.id, c.name,coalesce(c.notes,'') AS notes, COUNT(product.article) AS amount " +
                    "FROM category c " +
                    "LEFT JOIN product ON c.id = product.category_id " +
                    "GROUP BY c.id")
    List<CategoryDTO> getCategoriesInformation();

    @Query(nativeQuery = true,
            value = "SELECT c.id, c.name,coalesce(c.notes,'') AS notes, COUNT(product.article) AS amount " +
                    "FROM category c " +
                    "LEFT JOIN product ON c.id = product.category_id " +
                    "WHERE c.id = :catId " +
                    "GROUP BY c.id")
    CategoryDTO getCategoryInformation(@Param("catId") Long id);
//    List<CategoryDTO> getCategoriesInformation(@Param("catId") Long id);
}