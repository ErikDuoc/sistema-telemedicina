package cl.duoc.fullstack.clinicalrecordservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long appointmentId;
    private Long patientId;
    private Long doctorId;

    @Column(length = 5000)
    private String diagnosis;

    @Column(length = 5000)
    private String treatment;

    private String createdAt;
}