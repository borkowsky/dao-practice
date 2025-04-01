package net.rewerk.dbrest.util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.rewerk.dbrest.model.dto.response.JSONResponseDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

public abstract class ServletUtil {
    public static void sendPlainResponse(HttpServletResponse res,
                                         List<String> errors,
                                         int[] statusCodes) throws IOException {
        Gson gson = new Gson();
        if (statusCodes.length == 2) {
            res.setStatus(errors.isEmpty() ? statusCodes[0] :
                    statusCodes[1]);
        } else {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        JSONResponseDto<?> jsonResponse = JSONResponseDto.builder()
                .success(errors.isEmpty())
                .message(!errors.isEmpty() ? String.join(", ", errors) : null)
                .build();
        PrintWriter out = res.getWriter();
        out.println(gson.toJson(jsonResponse));
        out.close();
    }

    public static Long getIdFromRequest(HttpServletRequest req, List<String> errors) {
        Long id = null;
        if (req.getParameter("id") == null || Objects.equals(req.getParameter("id"), "")) {
            errors.add("ID is required");
        } else {
            id = Long.parseLong(req.getParameter("id"));
        }
        return id;
    }

    public static <T> void sendPayloadResponse(HttpServletResponse res,
                                               List<String> errors,
                                               List<T> entities,
                                               int[] statuses) throws IOException {
        if (statuses.length == 2) {
            res.setStatus(errors.isEmpty() ? statuses[0] :
                    statuses[1]);
        } else {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        Gson gson = new Gson();
        JSONResponseDto<T> jsonResponse = JSONResponseDto.<T>builder()
                .success(errors.isEmpty())
                .message(!errors.isEmpty() ? String.join(", ", errors) : null)
                .total(entities.size())
                .payload(entities)
                .build();
        PrintWriter out = res.getWriter();
        out.println(gson.toJson(jsonResponse));
        out.close();
    }
}
