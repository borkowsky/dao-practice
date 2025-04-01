package net.rewerk.dbrest.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    private Long id;
    private String name;
    private StaffRole role;
    private List<Airplane> airplanes;
    private String createdAt;
    private String updatedAt;
}
