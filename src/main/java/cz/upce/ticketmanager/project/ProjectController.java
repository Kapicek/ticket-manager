package cz.upce.ticketmanager.project;

import cz.upce.ticketmanager.common.CurrentUser;
import cz.upce.ticketmanager.project.dto.ProjectPatchRequest;
import cz.upce.ticketmanager.project.dto.ProjectRequest;
import cz.upce.ticketmanager.project.dto.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;
    private final CurrentUser current;

    public ProjectController(ProjectService service, CurrentUser current) {
        this.service = service;
        this.current = current;
    }

    @GetMapping
    public List<ProjectResponse> list(Authentication auth) {
        return service.list(current.get(auth));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest req, Authentication auth) {
        var created = service.create(req, current.get(auth));
        return ResponseEntity.created(URI.create("/projects/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id, Authentication auth) {
        return service.get(id, current.get(auth));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest req, Authentication auth) {
        return service.update(id, req, current.get(auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, current.get(auth));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{projectId}")
    public ProjectResponse patch(@PathVariable Long projectId,
                                 @RequestBody ProjectPatchRequest req,
                                 Authentication auth) {
        return service.patch(projectId, req, current.get(auth));
    }

}
