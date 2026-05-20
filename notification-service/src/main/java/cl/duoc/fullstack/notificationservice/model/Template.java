package cl.duoc.fullstack.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "templates")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(length = 2000)
    private String content;
}
