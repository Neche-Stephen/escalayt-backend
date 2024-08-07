package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {

    // create the comment
    TicketCommentResponse createTicketComment(TicketCommentRequestDto commentRequestDto, Long ticketId, String commenter);

    // get all the comment made under a particular ticket
    List<TicketCommentDTO> getTicketComments(Long ticketId);

    // get all the replies
    List<TicketRepliesDTO> getRepliesForComment(Long commentId);


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

    // Method to get the latest or recent open tickets
    List<TicketDto> getLatestThreeOpenTickets(String userName);
    List<TicketDto> getLatestThreeResolvedTickets(String userName);
    List<TicketDto> getLatestThreeInprogressTickets(String userName);

    // filter ticket
    List<Ticket> filterTickets(Priority priority, Status status, Long assigneeId, Long categoryId);
    Page<TicketResponse> filterTicketsWithPagination(
            List<Priority> priority,
            List<Status> status,
            List<Long> assigneeIds,
            List<Long> categoryIds,
            int page,
            int size);

    Ticket getTicketById(Long ticketId);

    TicketDTOs getTicketByIds(Long ticketId);

    void resolveTicket(Long ticketId, String username);

    void rateTicket(Long ticketId, TicketRatingRequest ratingRequest);

    //List all recent ticket activities
    Page<TicketActivitiesResponseDto> listAllRecentTicketActivities(Long id, String role, Pageable pageable);

    Admin getAdminId(String username);


    User getUserId(String username);


    // assign a ticket

    String assignTicket(Long ticketId, Long assignId, String username);



    // get ticket category by name
    List<CategoryDto> getCategoryName(String username);

    // get all tickets
    List<TicketResponse> getAllTicket(String username,int page, int size);


    // get all tickets by a user
    List<GeneralTicketDto> getTicketByCreatedBy(String username);

    // get all tickets by created under
    List<GeneralTicketDto> getTicketByCreatedUnder(String username, Long adminId);

    TicketCommentResponse replyToComment(TicketCommentReply replyDto, Long ticketId, Long commentId, String commenterUsername);

    List<TicketCommentResponse> getCommentReplies(Long commentId, String username);

    List<AssigneeDTO> fetchAssignees(String username);

    // DELETE MULTIPLE TICKETS
    void deleteTickets(List<Long> ticketIds, String username);

    // RESOLVE MULTIPLE TICKETS
    void resolveTickets(List<Long> ticketIds, String username);


}
