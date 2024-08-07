package com.sq022groupA.escalayt.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.*;
import com.sq022groupA.escalayt.exception.DoesNotExistException;
import com.sq022groupA.escalayt.exception.TicketNotFoundException;
import com.sq022groupA.escalayt.exception.UnauthorizedException;
import com.sq022groupA.escalayt.exception.UserNotFoundException;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.*;
import com.sq022groupA.escalayt.repository.*;
import com.sq022groupA.escalayt.service.TicketService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TicketService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final AdminRepository adminRepository;
    private final Cloudinary cloudinary;
    private final NotificationService notificationService;



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
    public List<TicketCommentDTO> getTicketComments(Long ticketId) {

        // find all ticket comments excluding replies
        List<TicketComment> comments = ticketCommentRepository.findByTicketIdAndParentCommentIsNull(ticketId);

        return comments.stream().map(comment -> {
            TicketCommentDTO ticketCommentDTO = new TicketCommentDTO();
            ticketCommentDTO.setId(comment.getId());
            ticketCommentDTO.setComment(comment.getComment());
            ticketCommentDTO.setCreatedAt(comment.getCreatedAt());

            if (comment.getCommenter() != null) {
                ticketCommentDTO.setFullName(comment.getCommenter().getFullName());
                ticketCommentDTO.setProfileUrl(comment.getCommenter().getPictureUrl());
            } else if (comment.getAdminCommenter() != null) {
                ticketCommentDTO.setFullName(comment.getAdminCommenter().getFirstName() + " " + comment.getAdminCommenter().getLastName());
                ticketCommentDTO.setProfileUrl(comment.getAdminCommenter().getPictureUrl());
            }

            return ticketCommentDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TicketRepliesDTO> getRepliesForComment(Long commentId) {

        // find all ticket replies excluding actual comments
        List<TicketComment> replies = ticketCommentRepository.findByParentCommentId(commentId);

        return replies.stream().map(reply -> {
            TicketRepliesDTO ticketRepliesDTO = new TicketRepliesDTO();
            ticketRepliesDTO.setId(reply.getId());
            ticketRepliesDTO.setComment(reply.getComment());
            ticketRepliesDTO.setCreatedAt(reply.getCreatedAt());

            if(reply.getCommenter() != null){
                ticketRepliesDTO.setFullName(reply.getCommenter().getFullName());
                ticketRepliesDTO.setProfileUrl(reply.getCommenter().getPictureUrl());
            }else if (reply.getAdminCommenter() != null) {
                ticketRepliesDTO.setFullName(reply.getAdminCommenter().getFirstName() + " " + reply.getAdminCommenter().getLastName());
                ticketRepliesDTO.setProfileUrl(reply.getAdminCommenter().getPictureUrl());
            }

            return ticketRepliesDTO;
        }).collect(Collectors.toList());

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

        String commenterName = (commentingUser != null) ? commentingUser.getFullName() :
                commentingAdmin.getFirstName() + " " + commentingAdmin.getLastName();

        // Return response
        return TicketCommentResponse.builder()
                .responseCode("200")
                .responseMessage("Comment replied successfully")
                .ticketCommentInfo(TicketCommentInfo.builder()
                        .createdAt(replyComment.getCreatedAt())
                        .ticketTitle(replyComment.getTicket().getTitle())
                        .comment(replyDto.getComment())
                        .commenter(commenterName)
                        .build())
                .build();
    }


    public List<TicketCommentResponse> getCommentReplies(Long commentId, String username) {

        // Check if user exists
        User commentingUser = userRepository.findByUsername(username).orElse(null);

        // Get admin
        Admin commentingAdmin = adminRepository.findByUsername(username).orElse(null);
        if (commentingUser == null && commentingAdmin == null) {
            throw new UserNotFoundException("User Not found");
        }

        // Fetch the replies
        List<TicketComment> replies = ticketCommentRepository.findByParentCommentId(commentId);

        // Convert the replies to response DTOs
        return replies.stream().map(reply -> TicketCommentResponse.builder()
                .responseCode("200")
                .responseMessage("Reply fetched")
                .ticketCommentInfo(TicketCommentInfo.builder()
                        .createdAt(reply.getCreatedAt())
                        .ticketTitle(reply.getTicket().getTitle())
                        .comment(reply.getComment())
                        .build())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<AssigneeDTO> fetchAssignees(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if (user == null && admin == null) {
            throw new UserNotFoundException("User not found");
        }

        List<User> assignees;
        if (admin != null) {
            assignees = ticketRepository.findAllAssignees();
        } else {
            assignees = ticketRepository.findAssigneesByUserId(user.getId());
        }

        return assignees.stream()
                .map(assignee -> new AssigneeDTO(assignee.getFullName(), assignee.getJobTitle()))
                .collect(Collectors.toList());

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
        Long openTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.OPEN);
        Long inProgressTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.IN_PROGRESS);
        Long resolvedTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.RESOLVED);


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
        Long resolvedTickets = ticketRepository.countTicketsByUserIdAndStatus(userId, Status.RESOLVED);

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

//    public List<String> getCategoryName(String username){
//
//        Admin admin = adminRepository.findByUsername(username).orElse(null);
//        User user = userRepository.findByUsername(username).orElse(null);
//        List<TicketCategory> categories;
//        if(admin != null){
//            categories = ticketCategoryRepository.findByCreatedUnder(admin.getId());
//        }else {
//            assert user != null;
//            categories = ticketCategoryRepository.findByCreatedUnder(user.getCreatedUnder());
//        }
//
//        return categories.stream().map(TicketCategory::getName).collect(Collectors.toList());
//    }
    @Override
    public List<CategoryDto> getCategoryName(String username) {
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);
        List<TicketCategory> categories;

        if (admin != null) {
            categories = ticketCategoryRepository.findByCreatedUnder(admin.getId());
        } else {
            assert user != null;
            categories = ticketCategoryRepository.findByCreatedUnder(user.getCreatedUnder());
        }

        return categories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());
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

        String fileUrl = null;
        MultipartFile file = ticketRequest.getFile();
        if (file != null && !file.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                fileUrl = uploadResult.get("url").toString();
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file to Cloudinary", e);
            }
        }

        String fileTitle = ticketRequest.getFileTitle();
        if (fileTitle == null) {
            fileTitle = ""; // or set a default title
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
                .fileUrl(fileUrl)
                .fileTitle(fileTitle)
                .build());

        // Send notification after ticket creation to Admin if ticket is created by user.
        try {
            if ( userCreator != null){
                // Creator is User, get Admin Id and send notification
                List<Admin> admins = adminRepository.findAll(); // Fetch all admins
                System.out.println("ADMINS " + admins);
                if (admins.isEmpty()) {
                    throw new RuntimeException("No Admins found to notify");
                }
                Long adminId = admins.get(0).getId(); // Get the ID of the first Admin (We only have one admin)
                System.out.println("ADMIN ID " + adminId);
                NotificationRequest notificationRequest = new NotificationRequest();

                notificationRequest.setTitle("New Ticket Created");
                notificationRequest.setBody("A new ticket has been created with title: " + ticket.getTitle());
                notificationRequest.setTopic("Ticket Notifications");

                notificationService.sendNotificationToUser(adminId, notificationRequest);
            }

            // Long userId = userCreator != null ? userCreator.getId() : adminCreator.getId();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }




        return TicketResponseDto.builder()
                .responseCode("111")
                .responseMessage("Ticket created")
                .ticketInfo(TicketInfo.builder()
                        .title(ticket.getTitle())
                        .createdAt(ticket.getCreatedAt())
                        .createdUnder(ticket.getCreatedUnder())
                        .fileUrl(fileUrl)
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

        List<Ticket> openTickets = ticketRepository.findTop3ByStatusAndCreatedUnderOrderByCreatedAtDesc(Status.RESOLVED, admin.getId());

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
                .assignedByAdmin(ticket.getAssignedByAdmin() != null ? ticket.getAssignedByAdmin().getFirstName() + " " + ticket.getAssignedByAdmin().getLastName() : null)
                .createdUnder(ticket.getCreatedUnder())
                .status(ticket.getStatus())
                .rating(ticket.getRating())
                .review(ticket.getReview())
//                .ticketComments(ticket.getTicketComments())
                .assignee(ticket.getAssignee() != null ? ticket.getAssignee().getFullName() : null)
                .build();
    }


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
                .fileUrl(ticket.getFileUrl())
                .fileTitle(ticket.getFileTitle())
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

        ticket.setStatus(Status.RESOLVED);
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

        List<TicketActivitiesResponseDto> sortedDtos = ticketsPage.stream()
                .map(ticket -> {
                    long minuteDifference = Duration.between(ticket.getCreatedAt(), LocalDateTime.now()).toMinutes();

                    if (ticket.getUpdatedAt() != null) {
                        minuteDifference = Duration.between(ticket.getUpdatedAt(), LocalDateTime.now()).toMinutes();
                    }

                    return new TicketActivitiesResponseDto(
                            ticket.getId(),
                            ticket.getTitle(),
                            ticket.getPriority().toString(),
                            ticket.getAssignee() != null ? ticket.getAssignee().getFullName() : null,
                            ticket.getStatus().toString(),
                            ticket.getTicketCategory().getName(),
                            ticket.getCreatedAt(),
                            ticket.getLocation(),
                            minuteDifference // Assuming you want to include the minuteDifference in the DTO
                    );
                })
                .sorted(Comparator.comparingLong(TicketActivitiesResponseDto::getMinuteDifference)) // Sorting by minuteDifference
                .collect(Collectors.toList()); // Collect to a List

        // Return a new Page with the sorted list
        return new PageImpl<>(sortedDtos, ticketsPage.getPageable(), ticketsPage.getTotalElements());
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
    public String assignTicket(Long ticketId, Long assignId, String username) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);

        User userAssigned = userRepository.findById(assignId).orElse(null);


        if(admin == null){
            throw new UserNotFoundException("You do not have proper authorization to make this action");
        }


        if(userAssigned == null){
            throw new UserNotFoundException("user does not exist to assign ticket to");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        ticket.setAssignee(userAssigned);
        ticket.setAssignedByAdmin(admin);
        ticket.setResolvedByAdmin(null);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(Status.IN_PROGRESS);

        ticketRepository.save(ticket);

        // Send Notification to assignee
        try {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setTitle("Ticket Assigned");
            notificationRequest.setBody("A ticket has been assigned to you with title: " + ticket.getTitle());
            notificationRequest.setTopic("Ticket Notifications");

            notificationService.sendNotificationToUser(userAssigned.getId(), notificationRequest);
            System.out.println("Notification sent to assignee with ID: " + userAssigned.getId());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

        return "Ticket Assign successful";

    }


    // get by created by
    @Override
    public List<GeneralTicketDto> getTicketByCreatedBy(String username) {

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("You cannot access this Tickets");
        }

        List <GeneralTicketDto> generalTicketDto = user.getCreatedTickets().stream().map(ticket -> {

                    long minutesDifference = Duration.between(ticket.getCreatedAt(), LocalDateTime.now()).toMinutes();

                    if(ticket.getUpdatedAt() != null){
                        minutesDifference = Duration.between(ticket.getUpdatedAt(), LocalDateTime.now()).toMinutes();
                    }

            return new GeneralTicketDto(
                    ticket.getId(),  ticket.getTitle(),
                    ticket.getStatus(),
                    minutesDifference,
                    ticket.getCreatedByUser() != null ? (AdminUserDetailsDto.builder()
                            .id(ticket.getCreatedByUser().getId())
                            .pictureUrl( ticket.getCreatedByUser().getPictureUrl())
                            .username( ticket.getCreatedByUser().getUsername())
                            .fullName(ticket.getCreatedByUser().getFullName())
                            .build()): null,
                    null,
                    ticket.getAssignee() != null ?(AdminUserDetailsDto.builder()
                            .pictureUrl(ticket.getAssignee().getPictureUrl())
                            .fullName(ticket.getAssignee().getFullName())
                            .id(ticket.getAssignee().getId())
                            .username(ticket.getAssignee().getUsername())
                            .build()): null,
                    ticket.getResolvedByAdmin() != null ?(AdminUserDetailsDto.builder()
                            .pictureUrl(ticket.getResolvedByAdmin().getPictureUrl())
                            .fullName(ticket.getResolvedByAdmin().getFirstName()+" "+ ticket.getResolvedByAdmin().getLastName())
                            .id(ticket.getResolvedByAdmin().getId())
                            .username(ticket.getResolvedByAdmin().getUsername())
                            .build()): null,
                    ticket.getResolvedByUser() != null ?(AdminUserDetailsDto.builder()
                            .pictureUrl(ticket.getResolvedByUser().getPictureUrl())
                            .fullName(ticket.getResolvedByUser().getFullName())
                            .id(ticket.getResolvedByUser().getId())
                            .username(ticket.getResolvedByUser().getUsername())
                            .build()): null
            );
                }




        ).sorted(Comparator.comparingLong(GeneralTicketDto::getMinutesDifference)) // Sort by minutesDifference in ascending order
                .limit(7) // Limit to the first 7 elements
                .collect(Collectors.toList());

        return generalTicketDto;
    }



    // get by created under
    @Override
    public List<GeneralTicketDto> getTicketByCreatedUnder(String username, Long createdUnderId) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if (admin == null || createdUnderId != admin.getId()) {
            throw new UserNotFoundException("You cannot access this tickets");
        }

        // reduces the notification to just seven.
        Pageable topSeven = PageRequest.of(0, 7, Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt"));
        List<Ticket> tickets = ticketRepository.findAllByCreatedUnder(createdUnderId, topSeven);


        List<GeneralTicketDto> generalTicketDto = tickets.stream()
                .map(ticket -> {


                    long minutesDifference = Duration.between(ticket.getCreatedAt(), LocalDateTime.now()).toMinutes();

                    if(ticket.getUpdatedAt() != null){
                        minutesDifference = Duration.between(ticket.getUpdatedAt(), LocalDateTime.now()).toMinutes();
                    }


                           return new GeneralTicketDto(
                                    ticket.getId(), ticket.getTitle(),
                                    ticket.getStatus(),
                                    minutesDifference,
                                   ticket.getCreatedByUser() != null ? (AdminUserDetailsDto.builder()
                                           .id(ticket.getCreatedByUser().getId())
                                           .pictureUrl( ticket.getCreatedByUser().getPictureUrl())
                                           .username( ticket.getCreatedByUser().getUsername())
                                           .fullName(ticket.getCreatedByUser().getFullName())
                                           .build()): null,
                                   ticket.getCreatedByAdmin() != null ? (AdminUserDetailsDto.builder()
                                           .id(ticket.getCreatedByAdmin().getId())
                                           .pictureUrl( ticket.getCreatedByAdmin().getPictureUrl())
                                           .username( ticket.getCreatedByAdmin().getUsername())
                                           .fullName(ticket.getCreatedByAdmin().getFirstName() +" "+ ticket.getCreatedByAdmin().getLastName())
                                           .build()): null,
                                   ticket.getAssignee() != null ?(AdminUserDetailsDto.builder()
                                           .pictureUrl(ticket.getAssignee().getPictureUrl())
                                           .fullName(ticket.getAssignee().getFullName())
                                           .id(ticket.getAssignee().getId())
                                           .username(ticket.getAssignee().getUsername())
                                           .build()): null,
                                   ticket.getResolvedByAdmin() != null ?(AdminUserDetailsDto.builder()
                                           .pictureUrl(ticket.getResolvedByAdmin().getPictureUrl())
                                           .fullName(ticket.getResolvedByAdmin().getFirstName()+" "+ ticket.getResolvedByAdmin().getLastName())
                                           .id(ticket.getResolvedByAdmin().getId())
                                           .username(ticket.getResolvedByAdmin().getUsername())
                                           .build()): null,
                                   ticket.getResolvedByUser() != null ?(AdminUserDetailsDto.builder()
                                           .pictureUrl(ticket.getResolvedByUser().getPictureUrl())
                                           .fullName(ticket.getResolvedByUser().getFullName())
                                           .id(ticket.getResolvedByUser().getId())
                                           .username(ticket.getResolvedByUser().getUsername())
                                           .build()): null

                );}).sorted(Comparator.comparingLong(GeneralTicketDto::getMinutesDifference)) // Sort by minutesDifference in ascending order
                .limit(7) // Limit to the first 7 elements
                .collect(Collectors.toList());

        return generalTicketDto;
    }

    // DELETE MULTIPLE TICKETS
    @Transactional
    @Override
    public void deleteTickets(List<Long> ticketIds, String username) {
        // Determine if the user is an admin or a regular user
        User userCreator = userRepository.findByUsername(username).orElse(null);
        Admin adminCreator = adminRepository.findByUsername(username).orElse(null);

        if (userCreator == null && adminCreator == null) {
            throw new UserNotFoundException("User not found");
        }

        for (Long ticketId : ticketIds) {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new DoesNotExistException("Ticket does not exist"));

            // If the user is not an admin, perform additional checks
            if (adminCreator == null) {
                // Check if the current user created the ticket
                if (ticket.getCreatedByUser() == null || !ticket.getCreatedByUser().getId().equals(userCreator.getId())) {
                    throw new UnauthorizedException("You are not authorized to delete this ticket");
                }
                // Check if the ticket has been resolved
                if (!Status.RESOLVED.equals(ticket.getStatus())) {
                    throw new UnauthorizedException("You cannot delete a ticket that has not been resolved");
                }
            }

            ticketRepository.delete(ticket);
        }
    }

    // RESOLVE MULTIPLE TICKETS
    @Transactional
    @Override
    public void resolveTickets(List<Long> ticketIds, String username) {
        // Get the user or admin based on the username
        User user = userRepository.findByUsername(username).orElse(null);
        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if (admin == null && user == null) {
            throw new UserNotFoundException("User not found");
        }

        for (Long ticketId : ticketIds) {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

            if (admin != null) {
                ticket.setResolvedByAdmin(admin);
            } else if (user != null) {
                ticket.setResolvedByUser(user);
            }

            ticket.setStatus(Status.RESOLVED);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
        }
    }


}