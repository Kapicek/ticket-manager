package cz.upce.ticketmanager.project;

import cz.upce.ticketmanager.project.dto.ProjectPatchRequest;
import cz.upce.ticketmanager.project.dto.ProjectRequest;
import cz.upce.ticketmanager.project.dto.ProjectResponse;
import cz.upce.ticketmanager.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository repo;
    public ProjectService(ProjectRepository repo) { this.repo = repo; }

    public List<ProjectResponse> list(User owner) {
        return repo.findAllByOwner_Id(owner.getId()).stream().map(this::toDto).toList();
    }

    public ProjectResponse create(ProjectRequest req, User owner) {
        var p = Project.builder()
                .name(req.name())
                .description(req.description())
                .status(req.status() == null ? ProjectStatus.ACTIVE : req.status())
                .owner(owner)
                .build();
        return toDto(repo.save(p));
    }

    public ProjectResponse get(Long id, User owner) {
        var p = owned(id, owner);
        return toDto(p);
    }

    public ProjectResponse update(Long id, ProjectRequest req, User owner) {
        var p = owned(id, owner);
        p.setName(req.name());
        p.setDescription(req.description());
        p.setStatus(req.status() == null ? ProjectStatus.ACTIVE : req.status());
        return toDto(p);
    }

    public void delete(Long id, User owner) {
        var p = owned(id, owner);
        repo.delete(p);
    }

    public ProjectResponse patch(Long id, ProjectPatchRequest req, User owner) {
        var p = owned(id, owner);
        if (req.name() != null) p.setName(req.name());
        if (req.description() != null) p.setDescription(req.description());
        if (req.status() != null) p.setStatus(req.status());
        return toDto(p);
    }

    private Project owned(Long id, User owner) {
        var p = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!p.getOwner().getId().equals(owner.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return p;
    }

    private ProjectResponse toDto(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getStatus());
    }
}
