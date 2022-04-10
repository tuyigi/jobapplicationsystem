package com.bkexercise.jobapplication.model;


import com.bkexercise.jobapplication.util.EDegreeType;
import com.bkexercise.jobapplication.util.EGenderType;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


public class JobApplicationDto {
    @NotNull(message = "Fisrt name should not be null")
    private String first_name;
    @NotNull(message = "last name should not be null")
    private String last_name;
    @Size(min=12,max = 12)
    private String phone_number;
    private String email;
    @Enumerated(EnumType.STRING)
    private EDegreeType degree;
    private int experience;
    @NotNull()
    @NotEmpty(message = "birth date sould not be null")
    private Date birth_date;
    @Enumerated(EnumType.STRING)
    private EGenderType gender;
    private String country;

    public String getCountry() {
        return country;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public EDegreeType getDegree() {
        return degree;
    }

    public int getExperience() {
        return experience;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public EGenderType getGender() {
        return gender;
    }
}
