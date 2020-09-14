package ru.test.model.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.test.model.Visit;
import ru.test.model.dto.VisitDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VisitMapper {

    @Mapping(source = "name", target = "patient.name")
    @Mapping(source = "surname", target = "patient.surname")
    @Mapping(source = "secondName", target = "patient.secondName")
    @Mapping(source = "birthday", target = "patient.birthday")
    Visit visitDtoToVisit(VisitDto dto);

    @InheritInverseConfiguration
    VisitDto visitToVisitDto(Visit visit);

    List<VisitDto> visitsToDtos(List<Visit> visits);
}
