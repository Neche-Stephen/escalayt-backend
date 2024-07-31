package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.model.Department;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.request.DepartmentRequestDto;

import java.util.List;

public interface DepartmentService {

    //create Department
    String createDepartment(DepartmentRequestDto requestDto, String username);

    // get all departments
    List<Department> getAllDepartment( String username);

   // List<User> getAllUserUnderDepartment(Long departmentId);

}
