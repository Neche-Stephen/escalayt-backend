package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.Department;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.exception.CustomException;
import com.sq022groupA.escalayt.exception.DoesNotExistException;
import com.sq022groupA.escalayt.exception.UserNotFoundException;
import com.sq022groupA.escalayt.payload.request.DepartmentRequestDto;
import com.sq022groupA.escalayt.repository.AdminRepository;
import com.sq022groupA.escalayt.repository.DepartmentRepository;
import com.sq022groupA.escalayt.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl  implements DepartmentService {

    private final AdminRepository adminRepository;

    private final DepartmentRepository departmentRepository;


    @Override
    public String createDepartment(DepartmentRequestDto requestDto, String username) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);



        // just to confirm that user exist in the database
        if(admin == null){

            throw new UserNotFoundException("Not an admin");
        }

        // save new  department
        try {
            departmentRepository.save(Department.builder()
                            .departmentCreatedBy(admin)
                            .createdUnder(admin.getId())
                            .department(requestDto.getDepartment())
                    .build());
        } catch (Exception e) {
            throw new CustomException("Something went wrong creating the department");
        }


        return "Department created successfully";
    }

    @Override
    public List<Department> getAllDepartment( String username) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);

        // just to confirm that user exists in the database
        if(admin == null){

            throw new UserNotFoundException("Not an admin");
        }

        return admin.getDepartmentList();
    }



//    @Override
//    public List<User> getAllUserUnderDepartment(Long departmentId) {
//
//          Department department = departmentRepository.findById(departmentId).orElse(null);
//
//
//          // check that the category exist
//          if(department == null){
//
//              throw new DoesNotExistException("Department does not exist");
//          }
//
//
//
//        return department.getUserList();
//    }


}
