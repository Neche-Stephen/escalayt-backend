package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.enums.Category;
import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t from Ticket t WHERE " +
            "(:priority is null or t.priority = :priority) and " +
            "(:status is null or t.status = :status) and " +
            "(:assigneeId is null or t.createdByUser.id = :assigneeId or t.createdByAdmin.id = :assigneeId) and " +
            "(:category is null or t.category = :category)")
    List<Ticket> findByFilters(@Param("priority") Priority priority, @Param("status") Status status,
                               @Param("assigneeId") Long assigneeId, @Param("category") Category category);
}
