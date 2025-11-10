package cz.upce.ticketmanager.ticket;

import cz.upce.ticketmanager.project.Project;
import cz.upce.ticketmanager.project.ProjectRepository;
import cz.upce.ticketmanager.user.User;
import cz.upce.ticketmanager.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketGeneratorController {

    private final TicketRepository tickets;
    private final ProjectRepository projects;
    private final UserRepository users;

    public TicketGeneratorController(TicketRepository tickets, ProjectRepository projects, UserRepository users) {
        this.tickets = tickets;
        this.projects = projects;
        this.users = users;
    }

    @PostMapping("/generate")
    @Transactional
    public Map<String,Object> generate(@RequestParam Long projectId,
                                       @RequestParam(defaultValue="100") int count) {
        Project p = projects.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<User> allUsers = users.findAll();
        Random rnd = new Random();

        for (int i=0; i<count; i++) {
            Ticket t = Ticket.builder()
                    .title("Demo #" + (System.currentTimeMillis()%100000) + "-" + i)
                    .type(TicketType.values()[rnd.nextInt(TicketType.values().length)])
                    .priority(TicketPriority.values()[rnd.nextInt(TicketPriority.values().length)])
                    .state(TicketState.values()[rnd.nextInt(TicketState.values().length)])
                    .project(p)
                    .assignee(allUsers.isEmpty()? null : allUsers.get(rnd.nextInt(allUsers.size())))
                    .build();
            tickets.save(t);
        }
        return Map.of("inserted", count, "projectId", projectId);
    }
}
