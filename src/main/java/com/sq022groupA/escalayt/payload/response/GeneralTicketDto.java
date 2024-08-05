package com.sq022groupA.escalayt.payload.response;


import com.sq022groupA.escalayt.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralTicketDto {

    private Long id;

    private String title;

    private Status status;

    private long minutesDifference;

    private AdminUserDetailsDto createdByUserDto;

    private AdminUserDetailsDto createdByAdmin;

    private  AdminUserDetailsDto assigneeDto;

    private AdminUserDetailsDto resolvedByAdminDto;

    private AdminUserDetailsDto resolvedByUserDto;

}
