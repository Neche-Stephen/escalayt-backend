package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.exception.DoesNotExistException;
import com.sq022groupA.escalayt.exception.UserNotFoundException;
import com.sq022groupA.escalayt.payload.request.TicketCommentRequestDto;
import com.sq022groupA.escalayt.payload.response.TicketCommentInfo;
import com.sq022groupA.escalayt.payload.response.TicketCommentResponse;
import com.sq022groupA.escalayt.repository.TicketCommentRepository;
import com.sq022groupA.escalayt.repository.TicketRepository;
import com.sq022groupA.escalayt.repository.UserRepository;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TicketService {

    private final UserRepository userRepository;

    private final TicketRepository ticketRepository;

    private final TicketCommentRepository ticketCommentRepository;



    @Override
    public TicketCommentResponse createTicketComment(TicketCommentRequestDto commentRequestDto, Long ticketId, String commenter) {
        // check if user exist
        User commentingUser = userRepository.findByUsername(commenter).orElse(null);
        if(commentingUser == null){

            throw new UserNotFoundException("User Not found");
        }


        // check if the ticket to be commented exist
        Ticket commentingTicket = ticketRepository.findById(ticketId).orElse(null);
        if(commentingTicket == null){

            throw new DoesNotExistException("Ticket does not exist");
        }

        TicketComment ticketComment = ticketCommentRepository.save(TicketComment.builder()
                .ticket(commentingTicket)
                .comment(commentRequestDto.getComment())
                .commenter(commentingUser)
                .build());

        return TicketCommentResponse.builder()
                .responseCode("200")
                .responseMessage("ticket commented")
                .ticketCommentInfo(TicketCommentInfo.builder()
                        .createdAt(ticketComment.getCreatedAt())
                        .commenter(ticketComment.getCommenter().getUsername())
                        .ticketTitle(ticketComment.getTicket().getTitle())
                        .build())
                .build();
    }

    @Override
    public List<TicketComment> getTicketComments(Long ticketId) {

        // check if the ticket to be commented exist
        Ticket commentingTicket = ticketRepository.findById(ticketId).orElse(null);
        if(commentingTicket == null){

            throw new DoesNotExistException("Ticket does not exist");
        }

        return ticketRepository.findById(ticketId).get().getTicketComments();
    }
}
