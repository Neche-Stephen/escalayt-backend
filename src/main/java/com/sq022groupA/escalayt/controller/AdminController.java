package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.Department;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.exception.ErrorResponse;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.AdminUserDetailsDto;
import com.sq022groupA.escalayt.payload.response.UserRegistrationResponse;
import com.sq022groupA.escalayt.service.AdminService;
import com.sq022groupA.escalayt.service.DepartmentService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final DepartmentService departmentService;

    // Endpoint to handle PUT requests for editing user details.
    @PutMapping("/edit")
    public ResponseEntity<String> editUserDetails(@RequestBody UserDetailsDto userDetailsDto){

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Delegate user details editing to UserService and get response.
        String response = adminService.editUserDetails(currentUsername, userDetailsDto);

        // Return HTTP 200 OK response with the edited user details response.
        return ResponseEntity.ok(response);
    }

    @PostMapping("/new-admin-password")
    public ResponseEntity<?> resetPassword(@RequestParam String newPassword) {

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        adminService.updatePassword(currentUsername, newPassword);

        return ResponseEntity.ok("Password reset successfully.");
    }



    /////------ USER/EMPLOYEE RELATED ADMIN ENDPOINTS -----\\\\\

    @PostMapping("/register-user")
    public ResponseEntity<UserRegistrationResponse> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        UserRegistrationResponse response = adminService.registerUser(currentUsername, userRegistrationDto);
        return ResponseEntity.ok(response);
    }



    // create department by admin.
    @PostMapping("/create-department")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentRequestDto requestDto){


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        String response = departmentService.createDepartment(requestDto, username);

        return ResponseEntity.ok(response);
    }

    // get all department
    @GetMapping("/get-all-department")
    public ResponseEntity<?> getAllDepartment(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Department> listOfDepartment = departmentService.getAllDepartment(username);

        return ResponseEntity.ok(listOfDepartment);
    }

//    @GetMapping("/department/{id}/employee")
//    public ResponseEntity<?> getAllDepartment(@PathVariable Long id){
//
//        List<User> listUser = departmentService.getAllUserUnderDepartment(id);
//
//        return ResponseEntity.ok(listUser);
//    }

    // get the user from the database.

    // get admin info

    @GetMapping("/get-admin-details")
    public ResponseEntity<AdminUserDetailsDto> getAdminDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(adminService.getAdminDetails(username));
    }


    // get list of users
    @GetMapping("/get-employeeList-details")
    public ResponseEntity<List<AdminUserDetailsDto>> getEmployeeDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(adminService.getAllEmployee(username));
    }

    // edit user endpoint by admin on admin dashboard
    @PutMapping("/update-employee-details/{id}")
    public ResponseEntity<?> updateEmployeeDetailByAdmin(@PathVariable Long id, @RequestBody AdminEditUserRequestDto requestDto){

        return ResponseEntity.ok(adminService.editUserDetailsByAdmin(id, requestDto));
    }

    @PutMapping("/update-admin-details")
    public ResponseEntity<?> updateAdminDetail( @RequestBody AdminDetailsRequestDto requestDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(adminService.editAdminDetails(username, requestDto));
    }
}