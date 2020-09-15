package ru.test.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.model.Visit;
import ru.test.model.dto.VisitDto;
import ru.test.service.VisitService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visit")
public class VisitController {

    final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping
    public List<VisitDto> getAllByFields(@RequestParam Map<String, String> filteringParams) {
        if (filteringParams == null){
            return visitService.getAll();
        }
        return visitService.allByFields(filteringParams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visit> getById(@PathVariable Long id) {
        return visitService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Long addVisit(@RequestBody @Valid VisitDto visit) {
        return visitService.add(visit);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @RequestBody @Valid VisitDto visitDto) {
        visitService.update(id, visitDto);
    }
}
