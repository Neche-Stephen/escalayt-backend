package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.payload.response.TicketCountResponse;
import com.sq022groupA.escalayt.repository.TicketRepository;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

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
    }
}
