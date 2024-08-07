package com.sq022groupA.escalayt.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDetailsDto {

   private Long id;

   private String username;

   private String fullName; // combination of firstName and lastName for admin

   private String email;

   private String pictureUrl;

   private String jobTitle;

   private String department;

   private String phoneNumber;

}
