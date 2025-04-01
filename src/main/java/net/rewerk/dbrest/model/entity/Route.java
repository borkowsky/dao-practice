package net.rewerk.dbrest.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    private Long id;
    private String departureTime;
    private String arrivalTime;
    private Airplane airplane;
    private Location departureLocation;
    private Location arrivalLocation;
    private String createdAt;
    private String updatedAt;
}
