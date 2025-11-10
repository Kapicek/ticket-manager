package cz.upce.ticketmanager.tickethistory;

import cz.upce.ticketmanager.ticket.*;
import cz.upce.ticketmanager.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "ticket_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @Enumerated(EnumType.STRING) private TicketState fromState;
    @Enumerated(EnumType.STRING) private TicketState toState;

    @Enumerated(EnumType.STRING) private TicketPriority fromPriority;
    @Enumerated(EnumType.STRING) private TicketPriority toPriority;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "from_assignee_id")
    private User fromAssignee;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "to_assignee_id")
    private User toAssignee;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant changedAt;
}
