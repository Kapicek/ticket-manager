package cz.upce.ticketmanager.attachment;

import cz.upce.ticketmanager.project.Project;
import cz.upce.ticketmanager.ticket.Ticket;
import cz.upce.ticketmanager.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "attachments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attachment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String filename;
    @Column(nullable = false) private String contentType;
    @Column(nullable = false) private Long sizeBytes;

    @Column(nullable = false) private String storagePath;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @CreationTimestamp @Column(nullable = false, updatable = false)
    private Instant uploadedAt;
}
