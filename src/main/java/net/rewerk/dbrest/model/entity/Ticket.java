package net.rewerk.dbrest.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.dbrest.enumerator.TicketClass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tickets")
@org.hibernate.annotations.NamedNativeQueries(
        @org.hibernate.annotations.NamedNativeQuery(
                name = "Ticket.findByUserId",
                query = "select * from tickets where userId = :userId",
                resultClass = Ticket.class
        )
)
public class Ticket {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tickets_id_seq_gen"
    )
    @SequenceGenerator(
            name = "tickets_id_seq_gen",
            sequenceName = "tickets_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String passport;
    @Enumerated(EnumType.STRING)
    @Column(name = "class")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TicketClass ticketClass;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "routeId")
    private Route route;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staffId")
    private Staff staff;
    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "luggageId")
    private Luggage luggage;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String updatedAt;
}
