package cl.duoc.fullstack.doctorservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String nationalRegistry;

    private String email;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
}
