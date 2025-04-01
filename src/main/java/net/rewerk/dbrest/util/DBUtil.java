package net.rewerk.dbrest.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DBUtil {
    public static void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.out.printf("[DBUtils] Unable to close result set: %s\n", e.getMessage());
        }
        closeResources(stmt, conn);
    }

    public static void closeResources(PreparedStatement stmt, Connection conn) {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.out.printf("[DBUtils] Unable to close PreparedStatement: %s\n", e.getMessage());
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.printf("[DBUtils] Unable to close Connection: %s\n", e.getMessage());
        }
    }

    public static Long getIdAfterInsert(PreparedStatement ps, int rowsCount) {
        Long id = null;
        try {
            ResultSet rs;
            if (rowsCount > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getLong(1);
                }
                rs.close();
            }
            return id;
        } catch (SQLException e) {
            return null;
        }
    }
}
