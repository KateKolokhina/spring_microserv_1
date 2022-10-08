package com.naukma.aic.controller;

import com.naukma.aic.entity.ProductZvitByCategoryDTO;
import com.naukma.aic.entity.User;
import com.naukma.aic.entity.UserInfoDTO;
import com.naukma.aic.exceptionHandlers.exceptions.CategoryNotFoundException;
import com.naukma.aic.exceptionHandlers.exceptions.UserNotFoundException;
import com.naukma.aic.pdf.service.ExportPdfServiceImpl;
import com.naukma.aic.repository.CategoryRepository;
import com.naukma.aic.repository.ProductRepository;
import com.naukma.aic.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class ZvitController {
    @Autowired
    private ExportPdfServiceImpl exportPdfService;

    @Autowired
    private CategoryRepository categoryRepository;

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

    @GetMapping("/zvit/1")
    public ModelAndView getAllReceipt(@RequestParam(value = "userIpn", required = false, defaultValue = "0") String userIpn) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("zvit_by_category");
        mav.addObject("userIpn", userIpn);

//        if (userIpn != null && !userRepository.findUserByIpn(userIpn).isPresent()) {
//            throw new UserNotFoundException(userIpn);
//        }
        List<UserInfoDTO> res = new ArrayList<>();
        if (!userIpn.equals(0)) {
            res = userRepository.findAllForZvit(userIpn);
        }
        log.info(res);
        mav.addObject("users", userRepository.findUsersByStatus("USER"));
        mav.addObject("res", res);
        return mav;
    }

    @GetMapping("/zvit/1/pdf/{categorySelected}")
    public void getZvitPdf(@PathVariable(value = "categorySelected") Long catId, HttpServletResponse response) throws IOException {
        Map<String, Object> data = new HashMap<>();

        if (catId != 0 && !categoryRepository.findById(catId).isPresent()) {
            throw new CategoryNotFoundException(catId);
        }
        List<ProductZvitByCategoryDTO> res = new ArrayList<>();
        if (catId != 0) {
            res = productRepository.getProductsInCategoryForZvit1(catId);
        }
        data.put("categorySelected", categoryRepository.findById(catId).get().getName());
        data.put("products", res);

        ByteArrayInputStream exportedData = exportPdfService.exportReceiptPdf("zvit_by_category_pdf", data);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=zvit.pdf");
        IOUtils.copy(exportedData, response.getOutputStream());
    }
}