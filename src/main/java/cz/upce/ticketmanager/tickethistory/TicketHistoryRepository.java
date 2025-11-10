package cz.upce.ticketmanager.tickethistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> { }
