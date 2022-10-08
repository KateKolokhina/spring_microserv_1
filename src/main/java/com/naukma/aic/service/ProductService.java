package com.naukma.aic.service;

import com.naukma.aic.entity.Category;
import com.naukma.aic.entity.CategoryDTO;
import com.naukma.aic.entity.Product;
import com.naukma.aic.entity.ProductAddDTO;
import com.naukma.aic.exceptionHandlers.exceptions.CategoryNotFoundException;
import com.naukma.aic.exceptionHandlers.exceptions.EntityDuplicateException;
import com.naukma.aic.exceptionHandlers.exceptions.InvalidData;
import com.naukma.aic.exceptionHandlers.exceptions.ProductNotFoundException;
import com.naukma.aic.repository.CategoryRepository;
import com.naukma.aic.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Log4j2
public class ProductService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void deleteRepository(String article) {
        Optional<Product> product = productRepository.findProductByArticle(article);
        if (!product.isPresent()) {
            throw new ProductNotFoundException(article);
        } else {
            productRepository.deleteProductByArticle(article);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createProduct(ProductAddDTO product) {

        if (!categoryRepository.findById(product.getCategoryId()).isPresent()) {
            throw new CategoryNotFoundException(product.getCategoryId());
        }
        if (productRepository.findProductByArticle(product.getArticle()).isPresent()) {
            throw new EntityDuplicateException("Товар", "article", product.getArticle());
        }
//        if (productRepository.findProductByName(product.getName()).isPresent()) {
//            throw new EntityDuplicateException("Товар", "name", product.getArticle());
//        }
        Category category = categoryRepository.findById(product.getCategoryId()).get();
        Product pr = fromDTO(product);
        pr.setCategory(category);
        productRepository.save(pr);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void editProduct(String article, ProductAddDTO product) {

        if (!categoryRepository.findById(product.getCategoryId()).isPresent()) {
            throw new CategoryNotFoundException(product.getCategoryId());
        }
        if (!productRepository.findProductByArticle(article).isPresent()) {
            throw new ProductNotFoundException(article);
        }
        if (productRepository.findProductByArticle(product.getArticle()).isPresent() && !product.getArticle().equals(article)) {
            throw new EntityDuplicateException("Товар", "article", product.getArticle());
        }
        Category category = categoryRepository.findById(product.getCategoryId()).get();
        Product pr = fromDTO(product);
        pr.setCategory(category);

        Product old = productRepository.findProductByArticle(article).get();

        if (!old.equals(pr))
            productRepository.save(pr);
    }
//
//    public boolean editCategory(Long id, Category newCategory) {
//        Category old;
//        if (categoryRepository.findById(id).isPresent()) {
//            old = categoryRepository.findById(id).get();
//        } else {
//            throw new CategoryNotFoundException(id);
//        }
//        if (!old.getId().equals(newCategory.getId())) {
//            throw new InvalidData(Collections.singletonMap("category_id", id.toString()));
//        }
//        if (!old.equals(newCategory)) {
//            if (categoryRepository.findByName(newCategory.getName()).isPresent()) {
//                Category finded = categoryRepository.findByName(newCategory.getName()).get();
//                if (finded.getName().equals(newCategory.getName()) && !old.getName().equals(newCategory.getName()))
//                    throw new EntityDuplicateException("Категорія", "name", newCategory.getName());
//            }
//            newCategory.setProducts(old.getProducts());
//            categoryRepository.save(newCategory);
//            return true;
//        }
//        return false;
//    }

    public Product fromDTO(ProductAddDTO dto) {
        Product res = new Product();
        res.setArticle(dto.getArticle());
        res.setName(dto.getName());
        res.setProducer(dto.getProducer());
        res.setPrice(dto.getPrice());
        res.setVolume(dto.getVolume());
        if (dto.getMinAge() == null) {
            res.setMinAge(0);
        } else
            res.setMinAge(dto.getMinAge());
        res.setNotes(dto.getNotes());
        return res;
    }
}
