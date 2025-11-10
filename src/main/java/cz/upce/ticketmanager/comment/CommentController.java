package cz.upce.ticketmanager.comment;

import cz.upce.ticketmanager.comment.dto.CommentRequest;
import cz.upce.ticketmanager.comment.dto.CommentResponse;
import cz.upce.ticketmanager.common.CurrentUser;
import cz.upce.ticketmanager.project.ProjectRepository;
import cz.upce.ticketmanager.ticket.TicketRepository;
import cz.upce.ticketmanager.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository comments;
    private final TicketRepository tickets;
    private final CurrentUser currentUser;

    @PostMapping
    public CommentResponse create(@Valid @RequestBody CommentRequest req,
                                  Authentication authentication) {
        var author = currentUser.get(authentication);
        var ticket = tickets.findById(req.ticketId())
                .orElseThrow(); // případně 404

        var saved = comments.save(Comment.builder()
                .body(req.body())
                .author(author)
                .ticket(ticket)
                .build());

        return new CommentResponse(
                saved.getId(),
                saved.getBody(),
                author.getId(),
                saved.getTicket().getId(),
                saved.getCreatedAt()
        );
    }

    @GetMapping("/by-ticket/{ticketId}")
    public List<CommentResponse> byTicket(@PathVariable Long ticketId) {
        return comments.findByTicket_IdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getBody(),
                        c.getAuthor().getId(),
                        c.getTicket().getId(),
                        c.getCreatedAt()
                ))
                .toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        comments.deleteById(id);
    }
}

