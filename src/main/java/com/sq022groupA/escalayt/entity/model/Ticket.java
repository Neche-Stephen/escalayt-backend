package com.sq022groupA.escalayt.entity.model;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sq022groupA.escalayt.entity.enums.Category;
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

//    @Enumerated(EnumType.STRING)
//    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String description;

    private Long createdUnder;


    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(optional = true)
    @JoinColumn(name = "created_by_user_id", foreignKey = @ForeignKey(name = "FK_ticket_created_by_user"))
    @JsonBackReference
    private User createdByUser;

    @ManyToOne(optional = true)
    @JoinColumn(name = "resolved_by_user_id", foreignKey = @ForeignKey(name = "FK_ticket_resolved_by_user"))
    @JsonBackReference
    private User resolvedByUser;

    @ManyToOne(optional = true)
    @JoinColumn(name = "created_by_admin_id", foreignKey = @ForeignKey(name = "FK_ticket_created_by_admin"))
    @JsonBackReference
    private Admin createdByAdmin;

    @ManyToOne(optional = true)
    @JoinColumn(name = "resolved_by_admin_id", foreignKey = @ForeignKey(name = "FK_ticket_resolved_by_admin"))
    @JsonBackReference
    private Admin resolvedByAdmin;


    private int rating;

    // review for the ticket rating
    private String review;


    // map category
    @ManyToOne
    @JoinColumn(name = "ticket_category_id")
    @JsonBackReference
    private TicketCategory ticketCategory;

    // mapped ticket comment here
    @OneToMany(mappedBy = "ticket")
    @JsonManagedReference
//    @JsonIgnoreProperties("ticket")
    private List<TicketComment> ticketComments;

    @ManyToOne(optional = true)
    @JoinColumn(name = "assignee_user_id", foreignKey = @ForeignKey(name = "FK_ticket_assignee_user"))
    @JsonManagedReference
    private User assignee;


}
