package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    private static final Logger LOG = LogManager.getLogger("TicketDAO");
    private static final String ERRORFETCHING = "Error fetching next available slot";
    static final Integer SEUIL = 30;
    private DataBaseConfig dataBaseConfig;


    public TicketDAO (DataBaseConfig dataBaseConfig){
        this.dataBaseConfig = dataBaseConfig;
    }

    public boolean saveTicket(Ticket ticket){
        Connection con = null;
        boolean result = false;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            result = ps.execute();
        }catch (Exception ex){
            LOG.error(ERRORFETCHING,ex);
        }finally {
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            LOG.error(ERRORFETCHING,ex);
        }finally {
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setString(3,ticket.getVehicleRegNumber());
            ps.execute();
            return true;
        }catch (Exception ex){
            LOG.error("Error saving ticket info",ex);
        }finally {
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    /**
     * delete ticket
     * @return list of different days as List<Integer>
     * @param ticket
     * @author Abel
     */
    public boolean deleteTicket(Ticket ticket){
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.DELETE_TICKET);
            ps.setInt(1, ticket.getId());
            ps.executeUpdate();
            return true;

        }catch (Exception ex){
            LOG.error("Error deleting ticket info");
            LOG.error(ex);
        }finally {
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    /**
     * get All tickets corresponding to the vehicleRegNumber and compare the different's days between date and put them in a list
     * @return list of different days as List<Integer>
     * @param vehicleRegNumber
     * @author Abel
     */
    public Integer getReccurentTicket(String vehicleRegNumber) {
        Connection con = null;
        Integer nbTicket = 0;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_RECURRENT_VEHICLE);
            ps.setString(1,vehicleRegNumber);
            ps.setInt(2,SEUIL);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                nbTicket  = rs.getInt("nb_ticket");
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);


        }catch (Exception ex){
            LOG.error(ERRORFETCHING,ex);
        }finally {
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return nbTicket;
    }

    public boolean checkIfVehicleAlreadyInTheParking(String vehicleRegNumber){
        Connection con = null;
        boolean result = false;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_TICKET_ALREADY_IN_PARKING_AND_NOT_EXIT);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int found = rs.getInt(1);
                if(found > 0){
                    result= true;
                }
            }
            }catch (Exception ex){
            LOG.error(ERRORFETCHING,ex);
            }finally {
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }
}
