package cz.upce.ticketmanager.ticket;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketExportController {

    private final TicketRepository tickets;

    public TicketExportController(TicketRepository tickets) {
        this.tickets = tickets;
    }

    @GetMapping(value="/export", produces="text/csv")
    public void exportCsv(HttpServletResponse resp) throws IOException {
        resp.setHeader("Content-Disposition","attachment; filename=tickets.csv");
        try (PrintWriter w = resp.getWriter()) {
            w.println("id;title;type;priority;state;projectId;assigneeId;updatedAt");
            for (Ticket t : tickets.findAll()) {
                w.printf("%d;%s;%s;%s;%s;%d;%s;%s%n",
                        t.getId(), escape(t.getTitle()),
                        t.getType(), t.getPriority(), t.getState(),
                        t.getProject().getId(),
                        t.getAssignee()==null? "" : t.getAssignee().getId().toString(),
                        t.getUpdatedAt());
            }
        }
    }

    @GetMapping(value = "/export", produces = "application/json")
    public List<Map<String, Object>> exportJson() {
        return tickets.findAll().stream().map(t -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", t.getId());
            m.put("title", t.getTitle());
            m.put("type", String.valueOf(t.getType()));
            m.put("priority", String.valueOf(t.getPriority()));
            m.put("state", String.valueOf(t.getState()));
            m.put("projectId", t.getProject().getId());
            m.put("assigneeId", t.getAssignee() == null ? null : t.getAssignee().getId());
            m.put("updatedAt", t.getUpdatedAt());
            return m;
        }).toList();
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(";", ","); // jednoduchá sanitizace do CSV
    }
}
