package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DataBaseConfig {

    private static final Logger LOG = LogManager.getLogger("DataBaseConfig");
    String userName ="root";
    String password ="rootroot";

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        LOG.debug("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/prod?serverTimezone=UTC",userName,password);
    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                LOG.debug("Closing DB connection");
            } catch (SQLException e) {
                LOG.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                LOG.debug("Closing Prepared Statement");
            } catch (SQLException e) {
                LOG.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                LOG.debug("Closing Result Set");
            } catch (SQLException e) {
                LOG.error("Error while closing result set",e);
            }
        }
    }
}
