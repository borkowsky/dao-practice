package net.rewerk.dbrest.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Luggage {
    private Long id;
    private Integer weight;
    private Long userId;
    private String createdAt;
    private String updatedAt;
}
