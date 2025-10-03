package cz.upce.ticketmanager.ticket;

import cz.upce.ticketmanager.common.CurrentUser;
import cz.upce.ticketmanager.ticket.dto.TicketPatchRequest;
import cz.upce.ticketmanager.ticket.dto.TicketRequest;
import cz.upce.ticketmanager.ticket.dto.TicketResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tickets")
public class TicketController {

    private final TicketService service;
    private final CurrentUser current;

    public TicketController(TicketService service, CurrentUser current) {
        this.service = service;
        this.current = current;
    }

    @GetMapping
    public List<TicketResponse> list(@PathVariable Long projectId, Authentication auth) {
        return service.list(projectId, current.get(auth));
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@PathVariable Long projectId,
                                                 @Valid @RequestBody TicketRequest req,
                                                 Authentication auth) {
        var created = service.create(projectId, req, current.get(auth));
        return ResponseEntity.created(URI.create(
                "/projects/" + projectId + "/tickets/" + created.id()
        )).body(created);
    }

    @GetMapping("/{ticketId}")
    public TicketResponse get(@PathVariable Long projectId,
                              @PathVariable Long ticketId,
                              Authentication auth) {
        return service.get(projectId, ticketId, current.get(auth));
    }

    @PutMapping("/{ticketId}")
    public TicketResponse update(@PathVariable Long projectId,
                                 @PathVariable Long ticketId,
                                 @Valid @RequestBody TicketRequest req,
                                 Authentication auth) {
        return service.update(projectId, ticketId, req, current.get(auth));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                       @PathVariable Long ticketId,
                                       Authentication auth) {
        service.delete(projectId, ticketId, current.get(auth));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{ticketId}")
    public TicketResponse patch(@PathVariable Long projectId,
                                @PathVariable Long ticketId,
                                @RequestBody TicketPatchRequest req,
                                Authentication auth) {
        return service.patch(projectId, ticketId, req, current.get(auth));
    }

}
