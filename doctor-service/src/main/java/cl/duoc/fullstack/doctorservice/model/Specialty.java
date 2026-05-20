package cl.duoc.fullstack.doctorservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specialties")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
}
