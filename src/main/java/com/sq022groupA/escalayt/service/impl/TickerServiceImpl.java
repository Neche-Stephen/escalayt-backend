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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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

        // get admin
        Admin commentingAdmin = adminRepository.findByUsername(commenter).orElse(null);
        if(commentingUser == null && commentingAdmin == null){
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
                .adminCommenter(commentingAdmin)
                .build());

        return TicketCommentResponse.builder()
                .responseCode("200")
                .responseMessage("ticket commented")
                .ticketCommentInfo(TicketCommentInfo.builder()
                        .createdAt(ticketComment.getCreatedAt())
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

    public TicketCommentResponse replyToComment(TicketCommentReply replyDto, Long ticketId,
                                                Long commentId, String commenterUsername) {

        // Check if user exists
        User commentingUser = userRepository.findByUsername(commenterUsername).orElse(null);

        // Get admin
        Admin commentingAdmin = adminRepository.findByUsername(commenterUsername).orElse(null);

        if (commentingUser == null && commentingAdmin == null) {
            throw new UserNotFoundException("User not found");
        }

        // Check if the ticket to be commented on exists
        Ticket commentingTicket = ticketRepository.findById(ticketId).orElse(null);
        if (commentingTicket == null) {
            throw new DoesNotExistException("Ticket does not exist");
        }

        // Check if the parent comment exists
        TicketComment parentComment = ticketCommentRepository.findById(commentId).orElse(null);
        if (parentComment == null) {
            throw new DoesNotExistException("Parent comment does not exist");
        }

        // Create and save the reply comment
        TicketComment replyComment = TicketComment.builder()
                .ticket(commentingTicket)
                .comment(replyDto.getComment())
                .commenter(commentingUser)
                .adminCommenter(commentingAdmin)
                .parentComment(parentComment)
                .build();

        ticketCommentRepository.save(replyComment);

        // Return response
        assert commentingUser != null;
        return TicketCommentResponse.builder()
                .responseCode("200")
                .responseMessage("Comment replied successfully")
                .ticketCommentInfo(TicketCommentInfo.builder()
                        .createdAt(replyComment.getCreatedAt())
                        .ticketTitle(replyComment.getTicket().getTitle())
                        .comment(replyDto.getComment())
                        .commenter(commentingUser.getFullName())
                        .build())
                .build();
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
            return getAdminTicketCount(admin.getId());
        } else {
            return getUserTicketCount(user.getId());
        }
    }

    private TicketCountResponse getAdminTicketCount(Long adminId) {
        Long totalTickets = ticketRepository.countTotalTicketsByAdminId(adminId);
        Long openTickets = ticketRepository.countTicketsByAdminIdAndStatus(adminId, Status.OPEN);
        Long inProgressTickets = ticketRepository.countTicketsByAdminIdAndStatus(adminId, Status.IN_PROGRESS);
        Long resolvedTickets = ticketRepository.countTicketsByAdminIdAndStatus(adminId, Status.RESOLVE);


        return TicketCountResponse.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .build();
    }

    private TicketCountResponse getUserTicketCount(Long userId) {
        Long totalTickets = ticketRepository.countTotalTicketsByUserId(userId);
        Long openTickets = ticketRepository.countTicketsByUserIdAndStatus(userId, Status.OPEN);
        Long inProgressTickets = ticketRepository.countTicketsByUserIdAndStatus(userId, Status.IN_PROGRESS);
        Long resolvedTickets = ticketRepository.countTicketsByUserIdAndStatus(userId, Status.RESOLVE);

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

    public List<String> getCategoryName(String username){

        Admin admin = adminRepository.findByUsername(username).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);
        List<TicketCategory> categories;
        if(admin != null){
            categories = ticketCategoryRepository.findByCreatedUnder(admin.getId());
        }else {
            assert user != null;
            categories = ticketCategoryRepository.findByCreatedUnder(user.getCreatedUnder());
        }

        return categories.stream().map(TicketCategory::getName).collect(Collectors.toList());
    }

    @Override
    public List<TicketResponse> getAllTicket(String username, int page, int size) {
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);

        Pageable pageable = PageRequest.of(page, size);
        List<Ticket> ticketList;

        if(admin != null){
            ticketList = ticketRepository.findByCreatedUnder(admin.getId(), pageable);
        }else {
            assert user != null;
            ticketList = ticketRepository.findByCreatedByUserId(user.getId(), pageable);
        }

        return ticketList.stream().map(ticket -> {

            TicketResponse ticketResponse = new TicketResponse();
            ticketResponse.setId(ticket.getId());
            ticketResponse.setCreatedAt(ticket.getCreatedAt());
            ticketResponse.setUpdatedAt(ticket.getUpdatedAt());
            ticketResponse.setTitle(ticket.getTitle());
            ticketResponse.setLocation(ticket.getLocation());
            ticketResponse.setPriority(ticket.getPriority().toString());
            ticketResponse.setDescription(ticket.getDescription());
            ticketResponse.setCreatedUnder(ticket.getCreatedUnder());
            ticketResponse.setStatus(ticket.getStatus().toString());
            ticketResponse.setRating(ticket.getRating());
            ticketResponse.setReview(ticket.getReview());
            ticketResponse.setTicketCategoryId(ticket.getTicketCategory().getId());
            ticketResponse.setTicketCategoryName(ticket.getTicketCategory().getName());
            if (ticket.getAssignee() != null) {
                ticketResponse.setAssigneeFullName(ticket.getAssignee().getFullName());
            }
            return ticketResponse;
        }).collect(Collectors.toList());
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

    @Override
    public List<TicketDto> getLatestThreeOpenTickets(String userName) {
        Admin admin = adminRepository.findByUsername(userName).orElse(null);

        if (admin == null) {
            throw new UserNotFoundException("You do not have proper authorization to make this action");
        }

        List<Ticket> openTickets = ticketRepository.findTop3ByStatusAndCreatedUnderOrderByCreatedAtDesc(Status.OPEN, admin.getId());

        return openTickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<TicketDto> getLatestThreeResolvedTickets(String userName) {
        Admin admin = adminRepository.findByUsername(userName).orElse(null);

        if (admin == null) {
            throw new UserNotFoundException("You do not have proper authorization to make this action");
        }

        List<Ticket> openTickets = ticketRepository.findTop3ByStatusAndCreatedUnderOrderByCreatedAtDesc(Status.RESOLVE, admin.getId());

        return openTickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<TicketDto> getLatestThreeInprogressTickets(String userName) {
        Admin admin = adminRepository.findByUsername(userName).orElse(null);

        if (admin == null) {
            throw new UserNotFoundException("You do not have proper authorization to make this action");
        }

        List<Ticket> inprogresTickets = ticketRepository.findTop3ByStatusAndCreatedUnderOrderByCreatedAtDesc(Status.IN_PROGRESS, admin.getId());

        return inprogresTickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private TicketDto mapToDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .title(ticket.getTitle())
                .location(ticket.getLocation())
                .priority(ticket.getPriority())
                .description(ticket.getDescription())
                .createdByUser(ticket.getCreatedByUser() != null ? ticket.getCreatedByUser().getFullName() : null)
                .createdByAdmin(ticket.getCreatedByAdmin() != null ? ticket.getCreatedByAdmin().getFirstName() + " " + ticket.getCreatedByAdmin().getLastName() : null)
                .resolvedByUser(ticket.getResolvedByUser() != null ? ticket.getResolvedByUser().getFullName() : null)
                .resolvedByAdmin(ticket.getResolvedByAdmin() != null ? ticket.getResolvedByAdmin().getFirstName() + " " + ticket.getResolvedByAdmin().getLastName() : null)
                .createdUnder(ticket.getCreatedUnder())
                .status(ticket.getStatus())
                .rating(ticket.getRating())
                .review(ticket.getReview())
                .ticketComments(ticket.getTicketComments())
                .assignee(ticket.getAssignee() != null ? ticket.getAssignee().getFullName() : null)
                .build();
    }

    // Method to get the latest or recent open tickets
//    @Override
//    public List<Ticket> getLatestThreeOpenTickets(String userName) {
//
//        Admin admin = adminRepository.findByUsername(userName).orElse(null);
//
//        if(admin == null){
//            throw new UserNotFoundException("You do not have proper authorization to make this action");
//        }
//        return ticketRepository.findTop3ByStatusAndCreatedUnderOrderByCreatedAtDesc(Status.OPEN, admin.getId());
//    }




    public List<Ticket> filterTickets(Priority priority, Status status, Long assigneeId, Long categoryId) {
        return ticketRepository.findTicketsByFilters(priority, status, assigneeId, categoryId);
    }

    public Page<TicketResponse> filterTicketsWithPagination(
            List<Priority> priority,
            List<Status> status,
            List<Long> assigneeIds,
            List<Long> categoryIds,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Ticket> ticketPage = ticketRepository.findTicketsByFilters(priority, status, assigneeIds, categoryIds, pageable);

        return ticketPage.map(ticket -> {
            TicketResponse ticketResponse = new TicketResponse();
            ticketResponse.setId(ticket.getId());
            ticketResponse.setCreatedAt(ticket.getCreatedAt());
            ticketResponse.setUpdatedAt(ticket.getUpdatedAt());
            ticketResponse.setTitle(ticket.getTitle());
            ticketResponse.setLocation(ticket.getLocation());
            ticketResponse.setPriority(ticket.getPriority().toString());
            ticketResponse.setDescription(ticket.getDescription());
            ticketResponse.setCreatedUnder(ticket.getCreatedUnder());
            ticketResponse.setStatus(ticket.getStatus().toString());
            ticketResponse.setRating(ticket.getRating());
            ticketResponse.setReview(ticket.getReview());
            ticketResponse.setTicketCategoryId(ticket.getTicketCategory().getId());
            ticketResponse.setTicketCategoryName(ticket.getTicketCategory().getName());
            if (ticket.getAssignee() != null) {
                ticketResponse.setAssigneeFullName(ticket.getAssignee().getFullName());
            }
            return ticketResponse;
        });
    }


    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));
    }

    public TicketDTOs getTicketByIds(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        AssigneeDTO assigneeDTO = null;

        if (ticket.getAssignee() != null) {
            assigneeDTO = AssigneeDTO.builder()
                    .fullName(ticket.getAssignee().getFullName())
                    .email(ticket.getAssignee().getEmail())
                    .jobTitle(ticket.getAssignee().getJobTitle())
                    .phoneNumber(ticket.getAssignee().getPhoneNumber())
                    .build();
        }

        CreatedByUserDTO createdByUserDTO = null;
        if (ticket.getCreatedByUser() != null) {
            createdByUserDTO = CreatedByUserDTO.builder()
                    .fullName(ticket.getCreatedByUser().getFullName())
                    .email(ticket.getCreatedByUser().getEmail())
                    .jobTitle(ticket.getCreatedByUser().getJobTitle())
                    .department(ticket.getCreatedByUser().getEmployeeDepartment() != null ? ticket.getCreatedByUser().getEmployeeDepartment().getDepartment() : null)
                    .phoneNumber(ticket.getCreatedByUser().getPhoneNumber())
                    .build();
        }

        return TicketDTOs.builder()
                .id(ticket.getId())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .title(ticket.getTitle())
                .location(ticket.getLocation())
                .priority(ticket.getPriority().name())
                .description(ticket.getDescription())
                .createdUnder(ticket.getCreatedUnder())
                .status(ticket.getStatus().name())
                .rating(ticket.getRating())
                .review(ticket.getReview())
                .ticketComments(ticket.getTicketComments().stream().map(TicketComment::getComment).collect(Collectors.toList()))
                .assignee(assigneeDTO)
                .ticketCategoryName(ticket.getTicketCategory().getName())
                .createdByUserId(ticket.getCreatedByUser() != null ? ticket.getCreatedByUser().getId() : null)
                .createdByAdminId(ticket.getCreatedByAdmin() != null ? ticket.getCreatedByAdmin().getId() : null)
                .createdByUser(createdByUserDTO)
                .build();
    }



    public void resolveTicket(Long ticketId, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        // Get the user or admin based on the username
        User user = userRepository.findByUsername(username).orElse(null);
        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if (admin != null) {
            ticket.setResolvedByAdmin(admin);
        } else if (user != null) {
            ticket.setResolvedByUser(user);
        } else {
            throw new UserNotFoundException("User not found");
        }

        ticket.setStatus(Status.RESOLVE);
        ticket.setUpdatedAt(LocalDateTime.now());

        ticketRepository.save(ticket);
    }

    @Override
    public void rateTicket(Long ticketId, TicketRatingRequest ratingRequest) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        ticket.setRating(ratingRequest.getRating());
        ticket.setReview(ratingRequest.getReview());

        ticketRepository.save(ticket);
    }

    @Override
    public Page<TicketActivitiesResponseDto> listAllRecentTicketActivities(Long id, String role, Pageable pageable) {
      Page<Ticket> ticketsPage;

      if("ADMIN".equals(role)){
          ticketsPage = ticketRepository.findAllByCreatedUnderOrderByUpdatedAtDescCreatedAtDesc(id, pageable);
      } else if("USER".equals(role)){
          ticketsPage = ticketRepository.findAllByCreatedByUserIdOrderByUpdatedAtDescCreatedAtDesc(id, pageable);
      } else {
          throw new IllegalArgumentException("Invalid role: " + role);
      }

        return ticketsPage.map(ticket -> new TicketActivitiesResponseDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getPriority().toString(),
                ticket.getAssignee() != null ? ticket.getAssignee().getFullName() : null,
                ticket.getStatus().toString(),
                ticket.getTicketCategory().getName(),
                ticket.getCreatedAt(),
                ticket.getLocation()
        ));
    }

    @Override
    public Admin getAdminId(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User getUserId(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


    // assign ticket to assignee
    @Override
    public String assignTicket(Long ticketId, Long assignId) {
        User userAssigned = userRepository.findById(assignId).orElse(null);


        if(userAssigned == null){
            throw new UserNotFoundException("You do not have proper authorization to make this action");
        }


        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        ticket.setAssignee(userAssigned);

        ticketRepository.save(ticket);

        return "Ticket Assign successful";

    }


    // get by created by
    @Override
    public List<Ticket> getTicketByCreatedBy(String username) {

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("You cannot access this Tickets");
        }

        return user.getCreatedTickets();
    }

    // get by created under
    @Override
    public List<Ticket> getTicketByCreatedUnder(String username, Long adminId) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if (admin == null || adminId != admin.getId()) {
            throw new UserNotFoundException("You cannot access this tickets");
        }

        List<Ticket> tickets = ticketRepository.findAllByCreatedUnder(adminId);

        return tickets;
    }

}
