package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.model.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {

    List<TicketCategory> findByCreatedUnder(Long createdUnder);

}
