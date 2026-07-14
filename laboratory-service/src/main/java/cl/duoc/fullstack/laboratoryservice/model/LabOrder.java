package cl.duoc.fullstack.laboratoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lab_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    @Column(name = "order_type", nullable = false)
    private String orderType;
    private Long doctorId;
    private String examType;
    private String status;
}
