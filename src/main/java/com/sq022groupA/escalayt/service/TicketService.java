package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {

    // create the comment
    TicketCommentResponse createTicketComment(TicketCommentRequestDto commentRequestDto, Long ticketId, String commenter);

    // get all the comment made under a particular ticket
    List<TicketComment> getTicketComments(Long ticketId);


//    TicketCountResponse getAdminTicketCount(Long adminId);
//    TicketCountResponse getUserTicketCount(Long adminId);

    TicketCountResponse getTicketCountByUsername(String username);

    // create category

    TicketCategoryResponseDto createTicketCategory(TicketCategoryRequestDto ticketCategoryRequest, String username);

    // this is to get ticket by category id
    List<Ticket> getTicketByCategory(Long id);

    // create new ticket
    TicketResponseDto createTicket(Long catId, TicketRequestDto ticketRequest, String username);

    // delete ticket
    TicketResponseDto deleteTicket(Long ticketId);

    // filter ticket
    List<Ticket> filterTickets(Priority priority, Status status, Long assigneeId, Long categoryId);

    Ticket getTicketById(Long ticketId);

    Ticket resolveTicket(Long ticketId, TicketResolutionRequest resolutionRequest);

    //List all recent ticket activities
    Page<TicketActivitiesResponseDto> listAllRecentTicketActivitiesForAdmin(Long admin_id, Pageable pageable);
    Page<TicketActivitiesResponseDto> listAllRecentTicketActivitiesForUser(Long user_id, Pageable pageable);


}
