package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByAdmin.id = :adminId AND t.status = :status")
    Long countTicketsByAdminIdAndStatus(@Param("adminId") Long adminId, @Param("status") Status status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByUser.id = :userId AND t.status = :status")
    Long countTicketsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Status status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByAdmin.id = :adminId")
    Long countTotalTicketsByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByUser.id = :userId")
    Long countTotalTicketsByUserId(@Param("userId") Long userId);
}
