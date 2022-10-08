package com.naukma.aic.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public interface CategoryDTO {

    Long getId();
    String getName();
    String getNotes();
    int getAmount();
}
