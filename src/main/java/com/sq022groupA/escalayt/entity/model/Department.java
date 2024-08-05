package com.sq022groupA.escalayt.entity.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Department extends BaseClass {

    // input the department as string
    private String department;


    // the relationship with admin is many to one
    // many department to one admin
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private Admin departmentCreatedBy;

    // the relationship to employee is one to many
    // one department can have multiple employee




    // this will be the admin of the user
    private Long createdUnder;

    // map the employee to the department
//    @OneToMany(mappedBy = "employeeDepartment")
//    @JsonManagedReference
//    @JsonIgnoreProperties("employeeDepartment")
//    private List<User> userList;

}
