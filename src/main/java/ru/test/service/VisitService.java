package ru.test.service;


import ru.test.model.Visit;
import ru.test.model.dto.VisitDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VisitService {

    List<VisitDto> allByFields(Map<String, String> fields);
    List<VisitDto> getAll();
    Optional<Visit> getById(Long id);
    Long add(VisitDto visit);
    void update(Long id, VisitDto visitDto);
}
