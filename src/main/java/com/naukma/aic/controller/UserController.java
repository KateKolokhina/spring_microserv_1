package com.naukma.aic.controller;

import com.naukma.aic.entity.User;
import com.naukma.aic.entity.UserDto;
import com.naukma.aic.exceptionHandlers.exceptions.EntityDuplicateException;
import com.naukma.aic.exceptionHandlers.exceptions.InvalidData;
import com.naukma.aic.exceptionHandlers.exceptions.UserNotFoundException;
import com.naukma.aic.repository.UserRepository;
import com.naukma.aic.service.ReceiptService;
import com.naukma.aic.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@Log4j2
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReceiptService receiptService;

    @ModelAttribute("currentUser")
    public UserDetails getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : (UserDetails) authentication.getPrincipal();
    }

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

    @GetMapping("/user/all")
    public ModelAndView getAllUsers() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user_all");
        List<String> statuses = new ArrayList<>();
        statuses.add("USER");
        statuses.add("UNACTIVE");
        mav.addObject("users", userRepository.findAllByStatusIn(statuses));
        return mav;
    }

    @DeleteMapping("/user/active/{ipn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView activeUser(@PathVariable String ipn) {
        if(!userRepository.findUserByIpn(ipn).isPresent()){
            throw new UserNotFoundException(ipn);
        }
        User user = userRepository.findUserByIpn(ipn).get();
        user.setStatus("USER");
        userRepository.save(user);
        return new RedirectView("/user/all");
    }

    @DeleteMapping("/user/deactive/{ipn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView deactiveUser(@PathVariable String ipn) {
        if(!userRepository.findUserByIpn(ipn).isPresent()){
            throw new UserNotFoundException(ipn);
        }
        User user = userRepository.findUserByIpn(ipn).get();
        user.setStatus("UNACTIVE");
        userRepository.save(user);
        return new RedirectView("/user/all");
    }

    @GetMapping("/user/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView createUserPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user_create");
        UserDto user = new UserDto();
        mav.addObject("user", user);
        return mav;
    }

    @PostMapping("/user/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RedirectView createUser(@ModelAttribute("user") UserDto user) {
        if (userRepository.findUserByIpn(user.getIpn()).isPresent()) {
            throw new EntityDuplicateException("Користувач", "ipn", user.getIpn());
        }
        userService.addUser(user);
        return new RedirectView("/user/all");
    }


    @GetMapping("/user/{ipn}")
    public ModelAndView showUser(@PathVariable String ipn) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user");

        if(!userRepository.findUserByIpn(ipn).isPresent()){
            throw new UserNotFoundException(ipn);
        }
        mav.addObject("receipts", receiptService.getAllReceipts(null, null, ipn));
        mav.addObject("user", userRepository.findUserDtoByIpn(ipn));
        return mav;
    }

    @GetMapping("/user/edit/{ipn}")
    public ModelAndView showEditUser(@PathVariable String ipn) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user_edit");
        UserDto user = new UserDto(userRepository.findUserDtoByIpn(ipn));
        if(!userRepository.findUserByIpn(ipn).isPresent()){
            throw new UserNotFoundException(ipn);
        }
        mav.addObject("user", user);
        return mav;
    }
    @PutMapping("/user/edit/{ipn}")
    public RedirectView editUser(@PathVariable String ipn, @ModelAttribute("userNew") UserDto user) {
        log.info(user);
        if(!userRepository.findUserByIpn(ipn).isPresent()){
            throw new UserNotFoundException(ipn);
        }
        if(!user.getOldIpn().equals(ipn)){
            throw new InvalidData(Collections.singletonMap("ipn", ipn.toString()));
        }
        userService.editUser(user.getOldIpn(), user);
        if(!user.getOldIpn().equals(ipn)){
            return new RedirectView("/user/"+user.getIpn());
        }else{
            return new RedirectView("/user/"+ipn);
        }

    }
}
