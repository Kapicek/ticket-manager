package cz.upce.ticketmanager.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByProject_IdAndProject_Owner_Id(Long projectId, Long ownerId);

    Optional<Ticket> findByIdAndProject_IdAndProject_Owner_Id(Long id, Long projectId, Long ownerId);
}
