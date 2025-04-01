package net.rewerk.dbrest.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JSONResponseDto<T> {
    private Boolean success;
    private String message;
    private Integer total;
    private List<T> payload;
}
