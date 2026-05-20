package cl.duoc.fullstack.prescriptionservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clinicalRecordId;
    private Long patientId;

    @Column(length = 3000)
    private String medication;

    @Column(length = 3000)
    private String indications;

    private String createdAt;
}