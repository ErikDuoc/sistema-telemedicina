package cl.duoc.fullstack.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "insurances")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double coveragePercentage;
}
