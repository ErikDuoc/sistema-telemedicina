package cl.duoc.fullstack.laboratoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Column(length = 1000)
    private String findings;

    private String documentUrl;
}
