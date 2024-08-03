package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findTop3ByStatusAndCreatedUnderOrderByCreatedAtDesc(Status status, Long userId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByAdmin.id = :adminId")
    Long countTicketsByAdmin(Long adminId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByAdmin.id = :adminId AND t.status = :status")
    Long countTicketsByAdminIdAndStatus(@Param("adminId") Long adminId, @Param("status") Status status);
    @Query("SELECT t from Ticket t WHERE " +
            "(:priority is null or t.priority = :priority) and " +
            "(:status is null or t.status = :status) and " +
            "(:assigneeId is null or t.createdByUser.id = :assigneeId or t.createdByAdmin.id = :assigneeId) and " +
            "(:categoryId is null or t.ticketCategory.id = :categoryId)")
    List<Ticket> findTicketsByFilters(@Param("priority") Priority priority, @Param("status") Status status,
                                      @Param("assigneeId") Long assigneeId, @Param("categoryId") Long categoryId);


    @Query("SELECT t FROM Ticket t WHERE " +
            "(:priority IS NULL OR t.priority IN :priority) AND " +
            "(:status IS NULL OR t.status IN :status) AND " +
            "(:assigneeIds IS NULL OR t.assignee.id IN :assigneeIds) AND " +
            "(:categoryIds IS NULL OR t.ticketCategory.id IN :categoryIds)")
    Page<Ticket> findTicketsByFilters(
            @Param("priority") List<Priority> priority,
            @Param("status") List<Status> status,
            @Param("assigneeIds") List<Long> assigneeIds,
            @Param("categoryIds") List<Long> categoryIds,
            Pageable pageable);


    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByUser.id = :userId")
    Long countTicketsByUser(Long userId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByUser.id = :userId AND t.status = :status")
    Long countTicketsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Status status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE (t.createdByAdmin.id = :adminId OR t.createdByUser.createdUnder = :adminId)")
    Long countTotalTicketsByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdByUser.id = :userId")
    Long countTotalTicketsByUserId(@Param("userId") Long userId);
    @Query("SELECT COUNT(t) FROM Ticket t WHERE (t.createdByAdmin.id = :adminId OR t.createdByUser.createdUnder = :adminId) AND t.status = :status")
    Long countAllTicketsUnderAdminAndStatus(Long adminId, Status status);

    //query tickets based on the admin ID stored in createdUnder field

    Page<Ticket> findAllByCreatedUnderOrderByUpdatedAtDescCreatedAtDesc(Long createdUnder, Pageable pageable);

    //query tickets based on the user ID stored in createdByUser field
    Page<Ticket> findAllByCreatedByUserIdOrderByUpdatedAtDescCreatedAtDesc(Long userid, Pageable pageable);

    List<Ticket> findByCreatedUnder(Long createdUnderId, Pageable pageable);

    List<Ticket> findByCreatedByUserId(Long userId, Pageable pageable);

    // find all by created under
    List<Ticket> findAllByCreatedUnder(Long createdUnderId, Pageable pageable);

    @Query("SELECT DISTINCT t.assignee FROM Ticket t WHERE t.createdByUser.id = :userId")
    List<User> findAssigneesByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT t.assignee FROM Ticket t")
    List<User> findAllAssignees();

}
