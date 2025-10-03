package cz.upce.ticketmanager.ticket;

import cz.upce.ticketmanager.project.Project;
import cz.upce.ticketmanager.project.ProjectRepository;
import cz.upce.ticketmanager.ticket.dto.TicketPatchRequest;
import cz.upce.ticketmanager.ticket.dto.TicketRequest;
import cz.upce.ticketmanager.ticket.dto.TicketResponse;
import cz.upce.ticketmanager.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TicketService {

    private final TicketRepository tickets;
    private final ProjectRepository projects;

    public TicketService(TicketRepository tickets, ProjectRepository projects) {
        this.tickets = tickets;
        this.projects = projects;
    }

    public List<TicketResponse> list(Long projectId, User owner) {
        ensureOwnedProject(projectId, owner); // 404/403 pokud není jeho
        return tickets.findAllByProject_IdAndProject_Owner_Id(projectId, owner.getId())
                .stream().map(this::toDto).toList();
    }

    public TicketResponse create(Long projectId, TicketRequest req, User owner) {
        Project p = ensureOwnedProject(projectId, owner);
        var t = Ticket.builder()
                .title(req.title())
                .type(req.type())
                .priority(req.priority())
                .state(req.state() == null ? TicketState.open : req.state())
                .project(p)
                .build();
        return toDto(tickets.save(t));
    }

    public TicketResponse get(Long projectId, Long ticketId, User owner) {
        var t = ownedTicket(projectId, ticketId, owner);
        return toDto(t);
    }

    public TicketResponse update(Long projectId, Long ticketId, TicketRequest req, User owner) {
        var t = ownedTicket(projectId, ticketId, owner);
        t.setTitle(req.title());
        t.setType(req.type());
        t.setPriority(req.priority());
        t.setState(req.state() == null ? TicketState.open : req.state());
        return toDto(t);
    }

    public void delete(Long projectId, Long ticketId, User owner) {
        var t = ownedTicket(projectId, ticketId, owner);
        tickets.delete(t);
    }

    public TicketResponse patch(Long projectId, Long ticketId, TicketPatchRequest req, User owner) {
        var t = ownedTicket(projectId, ticketId, owner);
        if (req.title() != null) t.setTitle(req.title());
        if (req.type() != null) t.setType(req.type());
        if (req.priority() != null) t.setPriority(req.priority());
        if (req.state() != null) t.setState(req.state());
        return toDto(t);
    }

    // --- helpers ---

    private Project ensureOwnedProject(Long projectId, User owner) {
        var p = projects.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!p.getOwner().getId().equals(owner.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return p;
    }

    private Ticket ownedTicket(Long projectId, Long ticketId, User owner) {
        return tickets.findByIdAndProject_IdAndProject_Owner_Id(ticketId, projectId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private TicketResponse toDto(Ticket t) {
        return new TicketResponse(t.getId(), t.getTitle(), t.getType(), t.getPriority(), t.getState());
    }
}
