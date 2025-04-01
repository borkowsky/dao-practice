package net.rewerk.dbrest.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.dbrest.enumerator.TicketClass;
import net.rewerk.dbrest.model.dto.UserDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long id;
    private String passport;
    private TicketClass ticketClass;
    private Route route;
    private UserDto user;
    private Staff staff;
    private Luggage luggage;
    private String createdAt;
    private String updatedAt;
}
