package com.naukma.aic.entity;

import com.naukma.aic.entity.order.Receipt;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user")
public class User implements Serializable {
    @Id
    @Column(nullable = false)
    private String ipn;

    @Column(nullable = false)
//    @Pattern(regexp = "([a-z]+\\.[a-z]+)@ukma\\.edu\\.ua")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String status;

    //FIO
    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String name;

    private String middleName;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String house;

    private String note;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "id")
    private List<Receipt> receipts = new ArrayList<>();


    public User changeUser(UserDto dto) {

        this.email = dto.getEmail();
        this.surname = dto.getSurname();
        this.name = dto.getName();
        this.middleName = dto.getMiddleName();
        this.telephone = dto.getTelephone();
        this.country = dto.getCountry();
        this.district = dto.getDistrict();
        this.city = dto.getCity();
        this.street = dto.getStreet();
        this.house = dto.getHouse();
        this.note = dto.getNote();

        if (!dto.getPassword().isEmpty()) {
            this.password = dto.getPassword();
        }
        return this;
    }

//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder("User( ");
//        stringBuilder.append("id = ").append(ipn).append(",")
//                .append("PIB = ").append(surname).append(" ").append(name);
//        if (!middleName.isBlank()) {
//            stringBuilder.append(" ").append(middleName).append(",");
//        } else
//            stringBuilder.append(",");
//        stringBuilder.append(" orders = [");
//
//        receipts.forEach(i -> stringBuilder
//                .append("[ id= ").append(i.getId()).append(", ")
//                .append("totalPrice= ").append(i.getTotalPrice()).append(" ]"));
//        stringBuilder.append("])");
//
//        return stringBuilder.toString();
//    }

}
