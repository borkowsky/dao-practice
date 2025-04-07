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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "routes_id_seq_gen"
    )
    @SequenceGenerator(
            name = "routes_id_seq_gen",
            sequenceName = "routes_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String departureTime;
    private String arrivalTime;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airplaneId")
    private Airplane airplane;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departureLocationId")
    private Location departureLocation;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "arrivalLocationId")
    private Location arrivalLocation;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
