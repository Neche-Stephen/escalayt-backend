package com.sq022groupA.escalayt.entity.model;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket extends BaseClass{


    private String title;

    private String location;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String description;

    private Long createdUnder;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(optional = true)
    @JoinColumn(name = "created_by_user_id", foreignKey = @ForeignKey(name = "FK_ticket_created_by_user"))
    @JsonBackReference("createdByUser")
    private User createdByUser;

    @ManyToOne(optional = true)
    @JoinColumn(name = "resolved_by_user_id", foreignKey = @ForeignKey(name = "FK_ticket_resolved_by_user"))
    @JsonBackReference("resolvedByUser")
    private User resolvedByUser;

    @ManyToOne(optional = true)
    @JoinColumn(name = "created_by_admin_id", foreignKey = @ForeignKey(name = "FK_ticket_created_by_admin"))
    @JsonBackReference("createdByAdmin")
    private Admin createdByAdmin;

    @ManyToOne(optional = true)
    @JoinColumn(name = "resolved_by_admin_id", foreignKey = @ForeignKey(name = "FK_ticket_resolved_by_admin"))
    @JsonBackReference("resolvedByAdmin")
    private Admin resolvedByAdmin;

    @ManyToOne(optional = true)
    @JoinColumn(name = "assigned_by_admin_id", foreignKey = @ForeignKey(name = "FK_ticket_assigned_by_admin"))
    @JsonBackReference("assignedByAdmin")
    private Admin assignedByAdmin;


    private int rating;

    // review for the ticket rating
    private String review;

    // New field for file URL
    private String fileUrl;

    private String fileTitle;

    // map category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_category_id")
    @JsonBackReference("ticketCategory")
    private TicketCategory ticketCategory;

    // mapped ticket comment here
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("ticketComments")
    private List<TicketComment> ticketComments;

    @ManyToOne(optional = true)
    @JoinColumn(name = "assignee_user_id", foreignKey = @ForeignKey(name = "FK_ticket_assignee_user"))
    @JsonManagedReference("assignee")
    private User assignee;


}
