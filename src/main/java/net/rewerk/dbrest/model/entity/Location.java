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
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "locations_id_seq_gen"
    )
    @SequenceGenerator(
            name = "locations_id_seq_gen",
            sequenceName = "locations_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String address;
    private String country;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
