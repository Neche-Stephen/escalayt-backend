package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.response.TicketCountResponse;
import com.sq022groupA.escalayt.repository.TicketRepository;
import com.sq022groupA.escalayt.repository.UserRepository;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    private final UserRepository userRepository;

    @Override
    public TicketCountResponse getAdminTicketCount(Long adminId) {
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


//        Long totalTickets = ticketRepository.countAllTicketsUnderAdmin(adminId) +
//                ticketRepository.countAllTicketsByEmployeesUnderAdmin(adminId);
//        Long openTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.OPEN) +
//                ticketRepository.countAllTicketsByEmployeesUnderAdminAndStatus(adminId, Status.OPEN);
//        Long inProgressTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.IN_PROGRESS) +
//                ticketRepository.countAllTicketsByEmployeesUnderAdminAndStatus(adminId, Status.IN_PROGRESS);
//        Long resolvedTickets = ticketRepository.countAllTicketsUnderAdminAndStatus(adminId, Status.RESOLVE) +
//                ticketRepository.countAllTicketsByEmployeesUnderAdminAndStatus(adminId, Status.RESOLVE);
//
//        return TicketCountResponse.builder()
//                .totalTickets(totalTickets)
//                .openTickets(openTickets)
//                .inProgressTickets(inProgressTickets)
//                .resolvedTickets(resolvedTickets)
//                .build();
    }

    @Override
    public TicketCountResponse getUserTicketCount(Long userId) {

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

//        Optional<User> user = userRepository.findById(userId);
//        if (user.isPresent()) {
//
//            Long totalTickets = ticketRepository.countTicketsByUser(userId);
//            Long openTickets = ticketRepository.countTicketsByUserAndStatus(userId, Status.OPEN);
//            Long inProgressTickets = ticketRepository.countTicketsByUserAndStatus(userId, Status.IN_PROGRESS);
//            Long resolvedTickets = ticketRepository.countTicketsByUserAndStatus(userId, Status.RESOLVE);
//
//            return TicketCountResponse.builder()
//                    .totalTickets(totalTickets)
//                    .openTickets(openTickets)
//                    .inProgressTickets(inProgressTickets)
//                    .resolvedTickets(resolvedTickets)
//                    .build();
//        } else {
//            throw new UsernameNotFoundException("User not found");
//        }

    }
}
