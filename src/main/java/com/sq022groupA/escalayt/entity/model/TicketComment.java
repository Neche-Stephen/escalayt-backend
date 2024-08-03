package com.sq022groupA.escalayt.entity.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class TicketComment extends BaseClass{


    /*
     * This class requires three unique entities:
     *
     * 1. Comment:
     *    - Represents the content of the comment.
     *
     * 2. Commenter:
     *    - Represents the user who made the comment.
     *    - Relationship: Many-to-One (unidirectional)
     *      - A comment has one commenter.
     *      - You can access the commenter from the comment, but not the other way around.
     *
     * 3. Ticket:
     *    - Represents the ticket associated with the comment.
     *    - Relationship: Many-to-One (bidirectional)
     *      - A comment is associated with one ticket.
     *      - You can access the ticket from the comment and the comment from the ticket.
     */

    /*
    Note: From the prd only employee and assignee can make commit no admin:
    so admin cannot make comment on a ticket.
     */

    private String comment;

    private String fileUrl;


    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonBackReference
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "commenter_id")
    @JsonBackReference
    private User commenter;

    @ManyToOne
    @JoinColumn(name = "admin_commenter_id")
    private Admin adminCommenter;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private TicketComment parentComment;

}
