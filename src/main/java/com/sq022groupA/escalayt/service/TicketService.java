package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.payload.request.TicketCommentRequestDto;
import com.sq022groupA.escalayt.payload.response.TicketCommentResponse;
import com.sq022groupA.escalayt.payload.response.TicketCountResponse;

import java.util.List;

public interface TicketService {

    // create the comment
    TicketCommentResponse createTicketComment(TicketCommentRequestDto commentRequestDto, Long ticketId, String commenter);

    // get all the comment made under a particular ticket
    List<TicketComment> getTicketComments(Long ticketId);


//    TicketCountResponse getAdminTicketCount(Long adminId);
//    TicketCountResponse getUserTicketCount(Long adminId);

    TicketCountResponse getTicketCountByUsername(String username);
}
