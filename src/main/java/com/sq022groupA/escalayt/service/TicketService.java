package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.payload.response.TicketCountResponse;

public interface TicketService {
    TicketCountResponse getAdminTicketCount(Long adminId);
    TicketCountResponse getUserTicketCount(Long userId);
}
