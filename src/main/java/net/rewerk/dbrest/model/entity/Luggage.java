package net.rewerk.dbrest.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "luggage")
public class Luggage {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "luggage_id_seq_gen"
    )
    @SequenceGenerator(
            name = "luggage_id_seq_gen",
            sequenceName = "luggage_id_seq",
            allocationSize = 1
    )
    private Long id;
    private Integer weight;
    private Long userId;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
