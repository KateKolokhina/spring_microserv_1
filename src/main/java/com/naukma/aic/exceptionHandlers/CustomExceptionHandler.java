package com.naukma.aic.exceptionHandlers;

import com.naukma.aic.exceptionHandlers.exceptions.*;
import com.naukma.aic.repository.UserRepository;
import com.naukma.aic.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.AccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @Autowired
    private UserRepository userRepository;

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

    private ModelAndView getErrorMav(HttpServletRequest req, Exception exception, Integer status) throws Exception {
        // Rethrow annotated exceptions or they will be processed here instead.
        if (AnnotationUtils.findAnnotation(exception.getClass(),
                ResponseStatus.class) != null)
            throw exception;

        log.error("Request: " + req.getRequestURI() + " raised " + exception);

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("exception", exception);
        mav.addObject("message", exception.getMessage());
        mav.addObject("status", status);

        return mav;
    }

    @ExceptionHandler(AccessException.class)
    public ModelAndView accessError(HttpServletRequest req, Exception exception) throws Exception {
        return getErrorMav(req, exception, 403);
    }

    @ExceptionHandler(EntityDuplicateException.class)
    public ModelAndView handleEntityDuplicateException(HttpServletRequest req, EntityDuplicateException exception)
            throws Exception {
        return getErrorMav(req, exception, 409); //Conflict error
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView HandleUserNotFoundException(HttpServletRequest req, UserNotFoundException exception) throws Exception {
        return getErrorMav(req, exception, 404);
    }

    @ExceptionHandler(ReceiptProductExistException.class)
    public ModelAndView HandleReceiptProductExistException(HttpServletRequest req,
                                                           ReceiptProductExistException exception) throws Exception {
        return getErrorMav(req, exception, 409);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView HandleProductNotFoundException(HttpServletRequest req,
                                                       ProductNotFoundException exception) throws Exception {
        return getErrorMav(req, exception, 404);
    }

    @ExceptionHandler(ReceiptProductNotFoundException.class)
    public ModelAndView HandleReceiptProductNotFoundException(HttpServletRequest req,
                                                              ReceiptProductNotFoundException exception) throws Exception {
        return getErrorMav(req, exception, 404);
    }

    @ExceptionHandler(ReceiptNotFoundException.class)
    public ModelAndView HandleReceiptNotFoundException(HttpServletRequest req, ReceiptNotFoundException exception) throws Exception {
        return getErrorMav(req, exception, 404);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ModelAndView HandleLessonNotFoundException(HttpServletRequest req, CategoryNotFoundException exception) throws Exception {
        return getErrorMav(req, exception, 404);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest req, Exception exception)
            throws Exception {
        return getErrorMav(req, exception, 500);
    }

    @ExceptionHandler(UserExistsException.class)
    public ModelAndView handleUserExistsException(HttpServletRequest req, UserExistsException exception)
            throws Exception {
        return getErrorMav(req, exception, 409); //Conflict error
    }

}
