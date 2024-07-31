package com.sq022groupA.escalayt.payload.response;


import com.sq022groupA.escalayt.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTicketDto {

    private Long id;
    private String title;
    private Status status;
    private long minutesDifference;
    private CreatedByDto createdByDto;

}
