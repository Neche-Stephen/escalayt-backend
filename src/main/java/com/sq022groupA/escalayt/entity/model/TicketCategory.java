package com.sq022groupA.escalayt.entity.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketCategory extends BaseClass{

/*
Unlike the TicketComment, the TicketCategory can extend the base class.
It has a one-to-many relationship with Ticket, meaning multiple tickets can be assigned to one category.
We can retrieve the list of tickets under a category using this one-to-many mapping.

The createdBy field still cannot be used as intended.
The createdUnder field needs to be an actual User entity rather than just an ID.
For now, I will ignore the createdUnder field (leaving its value as null) and use an actual User entity.
Using createdUnder is redundant after mapping a User, even if I assign the ID to createdUnder,
the User ID will still exist in that table.
Unless there is a specific perspective where this might be useful that I am missing.

-- Based on the PRD:
Create Ticket Category
    · As an admin, I want to be able to create a new ticket category for tickets
      to fall under, e.g., (facility, procurement, etc.)

    - The controller for creating categories will be included in the security config.
    - Only users with the Admin role can access that POST method.
    - This protocol will be added to the security context.
 */

// Steps to creating the category
// From the PRD: Category has a name and a brief description

    /*
    to iterate: the reason why the admin is not mapped to the ticket category is because
    it would be restricted from the security context non Admin will not be able to access the
    end point to create ticket category
     */

    private String name;

    private String description;

    private Long createdUnder;

    @OneToMany(mappedBy = "ticketCategory")
    @JsonManagedReference
    @JsonIgnoreProperties("ticketCategory")
    private List<Ticket> tickets;

    // ticket category
    @ManyToOne
    @JoinColumn(name = "createdBy_id")
    @JsonBackReference
    private Admin createdBy;

}
