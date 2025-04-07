package net.rewerk.dbrest.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "airplanes")
public class Airplane {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "airplanes_id_seq_gen"
    )
    @SequenceGenerator(
            name = "airplanes_id_seq_gen",
            sequenceName = "airplanes_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String name;
    private String number;
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH
            })
    @JoinTable(
            name = "airplane_staff",
            joinColumns = {
                    @JoinColumn(name = "airplaneId")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "staffId")
            }
    )
    private List<Staff> staff;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
