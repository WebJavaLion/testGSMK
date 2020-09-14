package ru.test.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "visit")
@AllArgsConstructor
@NoArgsConstructor
public class Visit {

    @Id
    @SequenceGenerator(
            name = "default_seq",
            sequenceName = "id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "patient_id")
    Patient patient;

    LocalDate dateOut;

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", dateOut=" + dateOut +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDateOut() {
        return dateOut;
    }

    public void setDateOut(LocalDate dateOut) {
        this.dateOut = dateOut;
    }
}
