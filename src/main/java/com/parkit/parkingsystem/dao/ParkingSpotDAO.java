package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSpotDAO {
    private static final Logger LOG = LogManager.getLogger("ParkingSpotDAO");

    private DataBaseConfig dataBaseConfig;

    public ParkingSpotDAO(DataBaseConfig dataBaseConfig){
        this.dataBaseConfig = dataBaseConfig;
    }

    public int getNextAvailableSlot(ParkingType parkingType){
        Connection con = null;
        int result=-1;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            ps.setString(1, parkingType.toString());
            rs = ps.executeQuery();

            if(rs.next()){
                result = rs.getInt(1);
            }
        }catch (Exception ex){
            LOG.error("Error fetching next available slot",ex);
        }finally {
            this.dataBaseConfig.closeResultSet(rs);
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    public boolean updateParking(ParkingSpot parkingSpot){
        //update the availability fo that parking slot
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();
            return (updateRowCount == 1);
        }catch (Exception ex){
            LOG.error("Error updating parking info",ex);
            return false;
        }finally {
            this.dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
    }

}
