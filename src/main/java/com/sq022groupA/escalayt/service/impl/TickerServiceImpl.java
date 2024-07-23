package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.*;
import com.sq022groupA.escalayt.exception.DoesNotExistException;
import com.sq022groupA.escalayt.exception.TicketNotFoundException;
import com.sq022groupA.escalayt.exception.UserNotFoundException;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.*;
import com.sq022groupA.escalayt.repository.*;
import com.sq022groupA.escalayt.service.TicketService;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TicketService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final AdminRepository adminRepository;



    @Override
    public TicketCommentResponse createTicketComment(TicketCommentRequestDto commentRequestDto, Long ticketId, String commenter) {
        // check if user exists
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



    @Override
    public TicketCountResponse getTicketCountByUsername(String username) {
        System.out.println("serviceImpl username is " + username);

        User user = userRepository.findByUsername(username).orElse(null);
        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if (user == null && admin == null) {
            throw new UserNotFoundException("User not found");
        }

        if (admin != null) {
            // If the user is an admin, get the ticket count for admin
            return getAdminTicketCount(admin.getId());
        } else {
            // If the user is a regular user, get the ticket count for the user
            return getUserTicketCount(user.getId());
        }
    }

    private TicketCountResponse getAdminTicketCount(Long adminId) {
        Long totalTickets = ticketRepository.countAllTicketsUnderAdmin(adminId);
        Long openTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.OPEN);
        Long inProgressTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.IN_PROGRESS);
        Long resolvedTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.RESOLVE);

        return TicketCountResponse.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .build();
    }

    private TicketCountResponse getUserTicketCount(Long userId) {
        Long totalTickets = ticketRepository.countTicketsByUser(userId);
        Long openTickets = ticketRepository.countTicketsByUserAndStatus(userId, Status.OPEN);
        Long inProgressTickets = ticketRepository.countTicketsByUserAndStatus(userId, Status.IN_PROGRESS);
        Long resolvedTickets = ticketRepository.countTicketsByUserAndStatus(userId, Status.RESOLVE);

        return TicketCountResponse.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .build();
    }


    @Override
    public TicketCategoryResponseDto createTicketCategory(TicketCategoryRequestDto ticketCategoryRequest, String username) {

        Admin creator = adminRepository.findByUsername(username).orElse(null);

        if(creator == null){
            throw new UserNotFoundException("You do not have proper authorization to make this action");
        }

        TicketCategory newTicketCategory = ticketCategoryRepository.save(TicketCategory.builder()
                .name(ticketCategoryRequest.getName())
                .description(ticketCategoryRequest.getDescription())
                .createdBy(null)
                .createdUnder(creator.getId())
                .build());

        return TicketCategoryResponseDto.builder()
                .responseCode("007")
                .responseMessage("Created a new Category")
                .ticketCategoryInfo(TicketCategoryInfo.builder()
                        .name(newTicketCategory.getName())
                        .createdUnder(newTicketCategory.getCreatedUnder())
                        .createdAt(newTicketCategory.getCreatedAt())
                        .build())
                .build();
    }

    @Override
    public List<Ticket> getTicketByCategory(Long categoryId) {
        TicketCategory ticketCategory= ticketCategoryRepository.findById(categoryId).orElse(null);

        if(ticketCategory == null){
            throw new DoesNotExistException("Ticket category not found");
        }

        return ticketCategory.getTickets() ;
    }

    @Override
    public TicketResponseDto createTicket(Long catId, TicketRequestDto ticketRequest, String username) {

        // get the creator of the ticket
        User userCreator = userRepository.findByUsername(username).orElse(null);

        Admin adminCreator =  adminRepository.findByUsername(username).orElse(null);

        if(userCreator == null && adminCreator == null){
            throw new UserNotFoundException("user not found");
        }



        // get category
        TicketCategory ticketCategory = ticketCategoryRepository.findById(catId).orElse(null);

        if(ticketCategory == null){
            throw new DoesNotExistException("Ticket Category does not exist");
        }



        Ticket ticket= ticketRepository.save(Ticket.builder()
                .createdByAdmin(adminCreator)
                .createdByUser(userCreator)
                .createdUnder(ticketCategory.getCreatedUnder())
                .ticketCategory(ticketCategory)
                .title(ticketRequest.getTitle())
                .description(ticketRequest.getDescription())
                .location(ticketRequest.getLocation())
                .priority(ticketRequest.getPriority())
                .status(Status.OPEN)
                .build());


        return TicketResponseDto.builder()
                .responseCode("111")
                .responseMessage("Ticket created")
                .ticketInfo(TicketInfo.builder()
                        .title(ticket.getTitle())
                        .createdAt(ticket.getCreatedAt())
                        .createdUnder(ticket.getCreatedUnder())
                        .build())
                .build();
    }

    @Override
    public TicketResponseDto deleteTicket(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);

        if(ticket == null){
            throw new DoesNotExistException("Ticket does not exist");
        }

        ticketRepository.delete(ticket);

        return TicketResponseDto.builder()
                .responseCode("888")
                .responseMessage("Ticket Deleted")
                .ticketInfo(null)
                .build();
    }

    public List<Ticket> filterTickets(Priority priority, Status status, Long assigneeId, Long categoryId) {
        return ticketRepository.findTicketsByFilters(priority, status, assigneeId, categoryId);
    }

    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));
    }

    public Ticket resolveTicket(Long ticketId, TicketResolutionRequest resolutionRequest) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        if (resolutionRequest.getResolvedByUser() != null) {
            ticket.setResolvedByUser(resolutionRequest.getResolvedByUser());
        }
        if (resolutionRequest.getResolvedByAdmin() != null) {
            ticket.setResolvedByAdmin(resolutionRequest.getResolvedByAdmin());
        }

        ticket.setStatus(Status.RESOLVE);
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    @Override
    public Page<TicketActivitiesResponseDto> listAllRecentTicketActivities(Pageable pageable) {
        // Retrieve all tickets from the repository, ordered by creation date in descending order
        return ticketRepository.findAllByOrderByUpdatedAtDesc(pageable)
                // Map each Ticket entity to a TicketActivitiesResponseDto object
                .map(ticket -> new TicketActivitiesResponseDto(
                        ticket.getId(),
                        ticket.getTitle(),
                        ticket.getPriority().toString(),
                        ticket.getResolvedByAdmin() != null ? ticket.getResolvedByAdmin().getFirstName() : null,
                        ticket.getStatus().toString(),
                        ticket.getTicketCategory().getName(),
                        ticket.getCreatedAt(),
                        ticket.getLocation()

                ));
    }

}
