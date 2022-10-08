package com.naukma.aic.controller;

import com.naukma.aic.entity.Category;
import com.naukma.aic.entity.CategoryDTO;
import com.naukma.aic.entity.ProductDTO;
import com.naukma.aic.exceptionHandlers.exceptions.CategoryNotFoundException;
import com.naukma.aic.exceptionHandlers.exceptions.UserNotFoundException;
import com.naukma.aic.repository.CategoryRepository;
import com.naukma.aic.repository.ProductRepository;
import com.naukma.aic.repository.UserRepository;
import com.naukma.aic.service.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.Access;
import javax.validation.Valid;
import java.util.List;

@Controller
@Log4j2
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

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

    @GetMapping("/category/all")
    public ModelAndView getAllCategories(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("category_all");
        mav.addObject("categories", categoryRepository.getCategoriesInformation());
        return mav;
    }

    @GetMapping("/category/{id}/products")
    public ModelAndView getCategoryProducts(@PathVariable Long id){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("category_products");

        CategoryDTO category = categoryRepository.getCategoryInformation(id);
        if(category == null){
            throw new CategoryNotFoundException(id);
        }
        List<ProductDTO> products = productRepository.getProductsInCategory(id);

        mav.addObject("category", category);
        mav.addObject("products", products);
        return mav;
    }

    @GetMapping("/category/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getCategory(@PathVariable Long id){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("category");
        CategoryDTO category = categoryRepository.getCategoryInformation(id);
        if(category == null){
            throw new CategoryNotFoundException(id);
        }
        mav.addObject("category", category);
        return mav;
    }

    @PutMapping("/category/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView updateCategory(@PathVariable Long id, @Valid Category category){
        log.info(category);
        categoryService.editCategory(id, category);
        return new RedirectView("/category/" + id);
    }

    @GetMapping("/category/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAddCategoryPage(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("category_add");
        Category category = new Category();
        mav.addObject("category", category);
        return mav;
    }

    @PostMapping("/category/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView createCategory(@Valid Category category){
        categoryService.createCategory(category);
        return new RedirectView("/category/all");
    }

    @DeleteMapping("/category/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return new RedirectView("/category/all");
    }
}