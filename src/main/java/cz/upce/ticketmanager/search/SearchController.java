package cz.upce.ticketmanager.search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final RestTemplate rest = new RestTemplate();

    // GET /api/search/tickets?q=...&project=...&state=open&priority=high&from=0&size=20
    @GetMapping("/tickets")
    public Map<String, Object> search(
            @RequestParam(defaultValue = "*") String q,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> bool = new HashMap<>();
        List<Map<String, Object>> must = new ArrayList<>();
        List<Map<String, Object>> filters = new ArrayList<>();

        must.add(Map.of("query_string",
                Map.of("query", q, "fields", List.of("title^3","project_name","assignee_username"))));
        if (project != null) filters.add(Map.of("term", Map.of("project_name.keyword", project)));
        if (state != null) filters.add(Map.of("term", Map.of("state", state.toLowerCase())));
        if (priority != null) filters.add(Map.of("term", Map.of("priority", priority.toLowerCase())));

        bool.put("must", must);
        if (!filters.isEmpty()) bool.put("filter", filters);

        query.put("query", Map.of("bool", bool));
        query.put("from", from);
        query.put("size", size);
        query.put("_source", List.of("id","title","project_id","project_name","state","priority",
                "assignee_username","updated_at"));

        // v Docker síti mluv na "elasticsearch:9200"; mimo Docker by to bylo http://localhost:9200
        String url = "http://elasticsearch:9200/tickets/_search";
        ResponseEntity<Map> resp = rest.postForEntity(url, query, Map.class);
        //noinspection unchecked
        return (Map<String, Object>) resp.getBody();
    }
}
