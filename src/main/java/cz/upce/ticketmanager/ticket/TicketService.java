package cz.upce.ticketmanager.ticket;

import cz.upce.ticketmanager.project.Project;
import cz.upce.ticketmanager.project.ProjectRepository;
import cz.upce.ticketmanager.ticket.dto.TicketAssignedResponse;
import cz.upce.ticketmanager.ticket.dto.TicketPatchRequest;
import cz.upce.ticketmanager.ticket.dto.TicketRequest;
import cz.upce.ticketmanager.ticket.dto.TicketResponse;
import cz.upce.ticketmanager.tickethistory.TicketHistory;
import cz.upce.ticketmanager.tickethistory.TicketHistoryRepository;
import cz.upce.ticketmanager.user.User;
import cz.upce.ticketmanager.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TicketService {

    private final TicketRepository tickets;
    private final ProjectRepository projects;
    private final UserRepository users;
    private final TicketHistoryRepository history;

    public TicketService(TicketRepository tickets,
                         ProjectRepository projects,
                         UserRepository users,
                         TicketHistoryRepository history) {
        this.tickets = tickets;
        this.projects = projects;
        this.users = users;
        this.history = history;
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

        Long assigneeId = tryGetAssigneeId(req);
        if (assigneeId != null) {
            t.setAssignee(users.findById(assigneeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignee not found")));
        }

        p.getTickets().add(t);
        var saved = tickets.save(t);
        return toDto(saved);
    }

    public TicketResponse get(Long projectId, Long ticketId, User user) {
        var t = tickets.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(t.getProject().getId(), projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var project = t.getProject();

        boolean isOwner = Objects.equals(project.getOwner().getId(), user.getId());
        boolean isAssignee = t.getAssignee() != null
                && Objects.equals(t.getAssignee().getId(), user.getId());

        if (!isOwner && !isAssignee) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return toDto(t);
    }

    public List<TicketAssignedResponse> myAssigned(User current) {
        var list = tickets.findByAssignee_Id(current.getId());
        return list.stream()
                .map(t -> new TicketAssignedResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getType(),
                        t.getPriority(),
                        t.getState(),
                        t.getProject().getId(),
                        t.getProject().getName()
                ))
                .toList();
    }

    public TicketResponse update(Long projectId, Long ticketId, TicketRequest req, User actor) {
        var t = ownedTicket(projectId, ticketId, actor);

        // původní hodnoty pro diff
        TicketState oldState = t.getState();
        TicketPriority oldPriority = t.getPriority();
        User oldAssignee = t.getAssignee();

        // aplikace změn
        t.setTitle(req.title());
        t.setType(req.type());
        t.setPriority(req.priority());
        t.setState(req.state() == null ? TicketState.open : req.state());

        Long assigneeId = tryGetAssigneeId(req);
        if (assigneeId != null) {
            var newAssignee = users.findById(assigneeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignee not found"));
            t.setAssignee(newAssignee);
        }

        var saved = tickets.save(t);
        maybeWriteHistory(saved, oldState, oldPriority, oldAssignee, actor);
        return toDto(saved);
    }

    public void delete(Long projectId, Long ticketId, User owner) {
        var t = ownedTicket(projectId, ticketId, owner);
        tickets.delete(t);
    }

    public TicketResponse patch(Long projectId, Long ticketId, TicketPatchRequest req, User actor) {
        var t = ownedTicket(projectId, ticketId, actor);

        TicketState oldState = t.getState();
        TicketPriority oldPriority = t.getPriority();
        User oldAssignee = t.getAssignee();

        if (req.title() != null) t.setTitle(req.title());
        if (req.type() != null) t.setType(req.type());
        if (req.priority() != null) t.setPriority(req.priority());
        if (req.state() != null) t.setState(req.state());

        Long assigneeId = tryGetAssigneeId(req);
        if (assigneeId != null) {
            var newAssignee = users.findById(assigneeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignee not found"));
            t.setAssignee(newAssignee);
        }

        var saved = tickets.save(t);
        maybeWriteHistory(saved, oldState, oldPriority, oldAssignee, actor);
        return toDto(saved);
    }

    // --- helpers ---

    private Project ensureOwnedProject(Long projectId, User owner) {
        var p = projects.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!Objects.equals(p.getOwner().getId(), owner.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return p;
    }

    private Ticket ownedTicket(Long projectId, Long ticketId, User owner) {
        return tickets.findByIdAndProject_IdAndProject_Owner_Id(ticketId, projectId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private TicketResponse toDto(Ticket t) {
        Long assigneeId = t.getAssignee() != null ? t.getAssignee().getId() : null;
        return new TicketResponse(
                t.getId(), t.getTitle(), t.getType(), t.getPriority(), t.getState(), assigneeId
        );
    }

    private void maybeWriteHistory(Ticket saved,
                                   TicketState oldState,
                                   TicketPriority oldPriority,
                                   User oldAssignee,
                                   User actor) {

        boolean stateChanged = oldState != saved.getState();
        boolean priorityChanged = oldPriority != saved.getPriority();
        boolean assigneeChanged = !sameUser(oldAssignee, saved.getAssignee());

        if (!(stateChanged || priorityChanged || assigneeChanged)) return;

        var h = TicketHistory.builder()
                .ticket(saved)
                .changedBy(actor)
                .fromState(oldState).toState(saved.getState())
                .fromPriority(oldPriority).toPriority(saved.getPriority())
                .fromAssignee(oldAssignee)
                .toAssignee(saved.getAssignee())
                .build();

        history.save(h);
    }

    private boolean sameUser(User a, User b) {
        Long aId = (a == null) ? null : a.getId();
        Long bId = (b == null) ? null : b.getId();
        return Objects.equals(aId, bId);
    }

    private Long tryGetAssigneeId(Object dto) {
        try {
            Method m = dto.getClass().getMethod("assigneeId");
            Object v = m.invoke(dto);
            return (v instanceof Long) ? (Long) v : null;
        } catch (NoSuchMethodException nsme) {
            return null;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid assigneeId");
        }
    }
}
