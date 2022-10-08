package com.naukma.aic.controller;

import com.naukma.aic.entity.*;
import com.naukma.aic.exceptionHandlers.exceptions.CategoryNotFoundException;
import com.naukma.aic.exceptionHandlers.exceptions.ProductNotFoundException;
import com.naukma.aic.exceptionHandlers.exceptions.UserNotFoundException;
import com.naukma.aic.repository.CategoryRepository;
import com.naukma.aic.repository.ProductRepository;
import com.naukma.aic.repository.UserRepository;
import com.naukma.aic.service.CategoryService;
import com.naukma.aic.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;

@Controller
@Log4j2
public class ProductController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("userIpn")
    public String getCurrentUserIpn(Authentication authentication) {
        UserDetails det =(authentication == null) ? null : (UserDetails) authentication.getPrincipal();
        if(det == null){
            return "";
        }else{
            String email = det.getUsername();
            if(!userRepository.findByEmail(email).isPresent()){
                throw new UserNotFoundException(email);
            }
            else
                return userRepository.findByEmail(email).get().getIpn();
        }
    }

    @ModelAttribute("currentUser")
    public UserDetails getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : (UserDetails) authentication.getPrincipal();
    }

    @GetMapping("/product/all")
    public ModelAndView getAllProducts() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("product_all");
        mav.addObject("products", productRepository.findAll());
        return mav;
    }

    @GetMapping("/product/{article}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getProduct(@PathVariable String article) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("product_edit");

        if (!productRepository.findProductByArticle(article).isPresent()) {
            throw new ProductNotFoundException(article);
        }
        Product product = productRepository.findProductByArticle(article).get();

        mav.addObject("categories", categoryRepository.getCategoriesInformation());
        mav.addObject("product", toDto(product));
        return mav;
    }

    @PutMapping("/product/{article}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView updateProduct(@PathVariable String article, @Valid ProductAddDTO product) {
        productService.editProduct(article, product);
        return new RedirectView("/product/all");
    }

    @GetMapping("/product/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAddProductPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("product_add");
        ProductAddDTO product = new ProductAddDTO();
        mav.addObject("categories", categoryRepository.getCategoriesInformation());
        mav.addObject("product", product);

        return mav;
    }

    @PostMapping("/product/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView createProduct(@Valid ProductAddDTO product) {
        productService.createProduct(product);
        return new RedirectView("/product/all");
    }

    @DeleteMapping("/product/{article}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView deleteProduct(@PathVariable String article) {
        productService.deleteRepository(article);
        return new RedirectView("/product/all");
    }

    public ProductAddDTO toDto(Product product) {
        ProductAddDTO res = new ProductAddDTO();
        res.setArticle(product.getArticle());
        res.setName(product.getName());
        res.setProducer(product.getProducer());
        res.setPrice(product.getPrice());
        res.setVolume(product.getVolume());

        if(product.getMinAge() == null)
            res.setMinAge(0);
        else
            res.setMinAge(product.getMinAge());
        if(product.getNotes() == null)
            res.setNotes("");
        else
            res.setNotes(product.getNotes());

        res.setCategoryId(product.getCategory().getId());
        return res;
    }
}