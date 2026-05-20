package cl.duoc.fullstack.agendaservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long doctorId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
