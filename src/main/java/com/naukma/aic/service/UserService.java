package com.naukma.aic.service;

import com.naukma.aic.entity.User;
import com.naukma.aic.entity.UserDto;
import com.naukma.aic.entity.order.Receipt;
import com.naukma.aic.exceptionHandlers.exceptions.EntityDuplicateException;
import com.naukma.aic.exceptionHandlers.exceptions.UserNotFoundException;
import com.naukma.aic.repository.ReceiptRepository;
import com.naukma.aic.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class UserService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    public User findUserByIpn(String ipn) {
        if (userRepository.findUserByIpn(ipn).isPresent()) {
            log.info("found by ipn  -> ipn:" + ipn);
            return userRepository.findUserByIpn(ipn).get();
        } else {
            return null;
        }
    }

    public User findUserByEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("found by email  -> email:" + email);
            return userRepository.findByEmail(email).get();
        } else {
            return null;
        }
    }

    public List<User> findByRole(String role) {
        return userRepository.findUsersByStatus(role);
    }

    public void addUser(UserDto userNew) {
        User userNewIpn = findUserByIpn(userNew.getIpn());
        User userNewEmail = findUserByEmail(userNew.getEmail());
        if (userNewIpn != null) {
            throw new EntityDuplicateException("Користувач", "ipn", userNew.getIpn());
        }
        if (userNewEmail != null) {
            throw new EntityDuplicateException("Користувач", "email", userNew.getEmail());
        }
        User user = new User();
        userNew.setPassword(passwordEncoder.encode(userNew.getPassword()));
        user.setIpn(userNew.getIpn());
        user.changeUser(userNew);
        user.setStatus("USER");
        userRepository.save(user);
    }

    public void editUser(String ipn, UserDto userNew) {
        if (!userRepository.findUserByIpn(ipn).isPresent()) {
            throw new UserNotFoundException(ipn);
        }
        User userNewIpn = findUserByIpn(userNew.getIpn());
        User userNewEmail = findUserByEmail(userNew.getEmail());
        if (userNewIpn != null && !userNew.getIpn().equals(userNew.getOldIpn())) {
            throw new EntityDuplicateException("Користувач", "ipn", userNew.getIpn());
        }
        if (userNewEmail != null && !userNew.getEmail().equals(userNew.getOldEmail())) {
            throw new EntityDuplicateException("Користувач", "email", userNew.getEmail());
        }
        User user = userRepository.findUserByIpn(ipn).get();

        if (!userNew.getPassword().isEmpty() && !passwordEncoder.encode(userNew.getPassword()).equals(user.getPassword())) {
            userNew.setPassword(passwordEncoder.encode(userNew.getPassword()));
        }

        if (!userNew.getIpn().equals(userNew.getOldIpn())) {
            User changed = new User();
            changed.changeUser(userNew);
            changed.setStatus(user.getStatus());
            changed.setIpn(userNew.getIpn());
            if (userNew.getPassword().isEmpty())
                changed.setPassword(user.getPassword());
            userRepository.save(changed);

            User newIpnUser = userRepository.findUserByIpn(userNew.getIpn()).get();
            List<Receipt> receipts = receiptRepository.findAllByUserIpn(userNew.getOldIpn());
            for (Receipt receipt : receipts) {
                receipt.setUser(newIpnUser);
            }
            receiptRepository.saveAll(receipts);
            userRepository.delete(user);
        } else {
            user.changeUser(userNew);
            userRepository.save(user);
        }
    }
}
