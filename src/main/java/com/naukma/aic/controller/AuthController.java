package com.naukma.aic.controller;

import com.naukma.aic.entity.User;
import com.naukma.aic.entity.UserDto;
import com.naukma.aic.exceptionHandlers.exceptions.EntityDuplicateException;
import com.naukma.aic.exceptionHandlers.exceptions.InvalidData;
import com.naukma.aic.exceptionHandlers.exceptions.UserExistsException;
import com.naukma.aic.exceptionHandlers.exceptions.UserNotFoundException;
import com.naukma.aic.repository.UserRepository;
import com.naukma.aic.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Collections;

@Controller
@Log4j2
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping({"/", "/index"})
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("message", "Test");
        return mav;
    }

    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("registration");
        UserDto user = new UserDto();
        mav.addObject("user", user);
        return mav;
    }

    @PostMapping("/registration")
    public RedirectView registUser(@ModelAttribute("user") UserDto user) {
        log.info(user);
        if (userRepository.findUserByIpn(user.getIpn()).isPresent()) {
            throw new EntityDuplicateException("Користувач", "ipn", user.getIpn());
        }
        userService.addUser(user);
        return new RedirectView("/user/" + user.getIpn());
    }

    @GetMapping("/success_login")
    public RedirectView successLogin(Principal principal){
        User user = userService.findUserByEmail(principal.getName());
        return new RedirectView("/user/" + user.getIpn());
    }

    @GetMapping("/accessDenied")
    public ModelAndView accessDeniedException() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("access_denied");
        return mav;
    }

    @ModelAttribute("userIpn")
    public String getCurrentUserIpn(Authentication authentication) {
        UserDetails det = (authentication == null) ? null : (UserDetails) authentication.getPrincipal();
        if (det == null) {
            return "";
        } else {
            String email = det.getUsername();
            if (!userRepository.findByEmail(email).isPresent()) {
                throw new UserNotFoundException(email);
            } else
                return userRepository.findByEmail(email).get().getIpn();
        }
    }

    @ModelAttribute("currentUser")
    public UserDetails getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : (UserDetails) authentication.getPrincipal();
    }
}