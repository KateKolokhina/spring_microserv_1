package com.naukma.aic.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public interface UserInfoDTO {

    public String getIpn();

    public String getEmail();

    public String getPassword();

    public String getStatus();

    public String getSurname();

    public String getName();

    public String getMiddleName();

    public String getCountry();

    public String getDistrict();

    public String getCity();

    public String getStreet();

    public String getHouse();

    public String getNote();

    public String getTelephone();

    public Integer getTotalOrderCount();

    default String getNotes() {
        if(getNote() == null)
            return "";
        else
            return getNote();
    }

    default String getPib() {
        String res = getSurname() + " " + getName();
        if(getMiddleName() == null)
            return res;
        else
            return  res + " " + getMiddleName();
    }

    default String getAddress() {
        return getCountry() + ", " + getDistrict() + " обл, м. " + getCity() + ",вул. " + getStreet() + ", " + getHouse();
    }
}
