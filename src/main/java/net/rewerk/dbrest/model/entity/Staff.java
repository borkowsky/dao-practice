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
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "staff_id_seq_gen"
    )
    @SequenceGenerator(
            name = "staff_id_seq_gen",
            sequenceName = "staff_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String name;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleId")
    private StaffRole role;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
