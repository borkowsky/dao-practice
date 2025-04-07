package net.rewerk.dbrest.model.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rewerk.dbrest.enumerator.UserGender;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_id_seq_gen"
    )
    @SequenceGenerator(
            name = "users_id_seq_gen",
            sequenceName = "users_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String username;
    private String password;
    private String name;
    private Integer age;
    private String email;
    private String passport;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private UserGender gender;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
