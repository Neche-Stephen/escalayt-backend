package com.sq022groupA.escalayt.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDTOs {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String title;
    private String location;
    private String priority;
    private String description;
    private Long createdUnder;
    private String status;
    private int rating;
    private String review;
    private List<String> ticketComments;
    private AssigneeDTO assignee;
    private String ticketCategoryName;
    private Long createdByUserId;
    private Long createdByAdminId;
    private CreatedByUserDTO createdByUser;
    private String fileUrl;
    private String fileTitle;

}
