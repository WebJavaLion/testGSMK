package ru.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.model.Patient;

public interface UserRepository extends JpaRepository<Patient, Long> {
}
