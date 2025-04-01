package net.rewerk.dbrest.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Airplane {
    private Long id;
    private String name;
    private String number;
    private List<Staff> staff;
    private String createdAt;
    private String updatedAt;
}
