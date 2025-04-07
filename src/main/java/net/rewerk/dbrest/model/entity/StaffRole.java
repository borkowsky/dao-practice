package net.rewerk.dbrest.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff_roles")
public class StaffRole {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "staff_roles_seq_gen"
    )
    @SequenceGenerator(
            name = "staff_roles_seq_gen",
            sequenceName = "staff_roles_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String name;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
