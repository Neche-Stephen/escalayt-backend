package com.sq022groupA.escalayt.entity.model;



import com.sq022groupA.escalayt.entity.enums.Category;
import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
public class Ticket extends BaseClass{


    private String title;

    private String location;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String description;


    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(optional = true)
    @JoinColumn(name = "createdBy_id", foreignKey = @ForeignKey(name = "FK_ticket_created_by"))
    private User createdBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "resolvedBy_id", foreignKey = @ForeignKey(name = "FK_ticket_resolved_by"))
    private User resolvedBy;
}
