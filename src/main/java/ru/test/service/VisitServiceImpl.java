package ru.test.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.test.annotation.SingularsFromMetaModel;
import ru.test.model.Patient;
import ru.test.model.Patient_;
import ru.test.model.Visit;
import ru.test.model.Visit_;
import ru.test.model.dto.VisitDto;
import ru.test.model.mapper.VisitMapper;
import ru.test.repository.VisitRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class VisitServiceImpl implements VisitService {

    final VisitRepository repository;

    final VisitMapper mapper;

    @SingularsFromMetaModel(target = Patient_.class)
    public Map<String, SingularAttribute<Patient, ?>> attributesForUser;

    @SingularsFromMetaModel(target = Visit_.class)
    public Map<String, SingularAttribute<Visit, ?>> attributesForVisit;

    public VisitServiceImpl(VisitRepository repository, VisitMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<VisitDto> allByFields(Map<String, String> params) {

        Specification<Visit> spec = Specification
                .where((root, query, builder) -> {
                    final Join<Visit, Patient> join = root.join(Visit_.patient);
                    return builder.and(computePredicatesFormParams(builder, root, join, params));
                });
        return mapper.visitsToDtos(repository.findAll(spec));
    }

    @Override
    public List<VisitDto> getAll() {
        return mapper.visitsToDtos(repository.findAll());
    }

    @Override
    public Optional<Visit> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Long add(VisitDto visit) {
        return repository.save(mapper.visitDtoToVisit(visit)).getId();
    }

    @Override
    public void update(Long id, VisitDto visitDto) {
        final Visit visit = mapper.visitDtoToVisit(visitDto);
        repository.findById(id).ifPresentOrElse(v -> {

            v.setDateOut(visit.getDateOut());
            v.getPatient().setName(visit.getPatient().getName());
            v.getPatient().setSurname(visit.getPatient().getSurname());
            v.getPatient().setBirthday(visit.getPatient().getBirthday());
            v.getPatient().setSecondName(visit.getPatient().getSecondName());

        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no visit with such id was found");
        });
    }

    private Predicate[] computePredicatesFormParams(CriteriaBuilder builder,
                                                    Root<Visit> root,
                                                    Join<Visit, Patient> join,
                                                    Map<String, String> params) {

        return params.entrySet()
                .stream()
                .map(entry ->
                        this.attributesForVisit.containsKey(entry.getKey()) ?
                                getPredicateByField(attributesForVisit.get(entry.getKey()), builder, root, entry
                                        .getValue())
                                :
                                attributesForUser.containsKey(entry.getKey()) ?
                                        getPredicateByField(attributesForUser.get(entry.getKey()), builder, join, entry
                                                .getValue())
                                        : null

                )
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);
    }

    private Predicate getPredicateByField(SingularAttribute<Visit, ?> attribute,
                                          CriteriaBuilder builder,
                                          Root<Visit> root,
                                          String value) {

        if (LocalDate.class.equals(attribute.getJavaType())) {
            return builder.equal(root.get(attribute), LocalDate.parse(value));

        } else if (String.class.equals(attribute.getJavaType())) {
            return builder
                    .like(builder.upper(root.get(attribute.getName())),
                            "%" + Objects.requireNonNull(value).toUpperCase() + "%");
        }
        return null;
    }

    private Predicate getPredicateByField(SingularAttribute<Patient, ?> attribute,
                                          CriteriaBuilder builder,
                                          Join<Visit, Patient> join,
                                          String value) {

        Predicate predicate = null;
        if (attribute.getType().getJavaType().equals(String.class)) {
            predicate = builder
                    .like(builder.upper(join.get(attribute.getName())),
                            "%" + Objects.requireNonNull(value).toUpperCase() + "%");

        } else if (attribute.getType().getJavaType().equals(LocalDate.class)) {
            if (Patient_.BIRTHDAY.equals(attribute.getName())) {
                predicate = builder.equal(join.get(attribute), LocalDate.parse(value));
            }
        }
        return predicate;
    }
}
