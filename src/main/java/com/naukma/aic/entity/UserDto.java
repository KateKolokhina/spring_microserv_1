package com.naukma.aic.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class UserDto{

    public UserDto (UserInfoDTO dto){
        this.ipn = dto.getIpn();
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
        this.status = dto.getStatus();

    }

    @NotNull
    @Length(max = 10, min = 10)
    private String ipn;

//    @NotNull
    @Length(max = 10, min = 10)
    private String oldIpn;

    @NotNull
    private String email;

//    @NotNull
    private String oldEmail;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z]).{8,32}$")
    private String password;

    @NotNull
    @Pattern(regexp = "USER|UNACTIVE")
    private String status;

    //FIO
    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String surname;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String name;

    private String middleName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String telephone;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String country;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String district;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String city;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String street;

    @NotNull
    private String house;

    private String note;

    public String getNotes() {
        if(getNote() == null)
            return "";
        else
            return getNote();
    }
}
