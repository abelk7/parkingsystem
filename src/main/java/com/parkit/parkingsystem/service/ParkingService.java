package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.util.ParkingSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ParkingService {

    private static final Logger LOG = LogManager.getLogger("ParkingService");

    private static final Integer NB_JOURS_MINI = 5;

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    public void processIncomingVehicle() {
        try{
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0){
                String vehicleRegNumber = getVehichleRegNumber();

                if(ticketDAO.checkIfVehicleAlreadyInTheParking(vehicleRegNumber)){
                    LOG.info("\nThis Reg Number is currently in the parking lot.\n");
                    return;
                }

                //Verify if is regular vehicle
                if(isRegularVehicle(vehicleRegNumber)){
                    LOG.info("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5 percent discount");
                }

                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                Date inTime = new Date();
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0.0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);



                ticketDAO.saveTicket(ticket);
                LOG.info("Generated Ticket and saved in DB");
                LOG.info(String.format("Please park your vehicle in spot number : " + parkingSpot.getId()));
                LOG.info(String.format("Recorded in-time for vehicle number: "+vehicleRegNumber +" is : " + inTime));
            }
        }catch(Exception e){
            LOG.error("Unable to process incoming vehicle",e);
        }
    }

    private String getVehichleRegNumber() throws Exception {
        LOG.info("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;
        try{
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
                throw new ParkingSystemException("Error fetching parking number from DB. Parking slots might be full");
            }
        }catch(IllegalArgumentException ie){
            LOG.error("Error parsing user input for type of vehicle", ie);
        }catch(Exception e){
            LOG.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehichleType(){
        LOG.info("Please select vehicle type from menu");
        LOG.info("1 CAR");
        LOG.info("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                LOG.info("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    public void processExitingVehicle() {
        Ticket ticket = null;
        Date outTime = null;
        try{
            String vehicleRegNumber = getVehichleRegNumber();
            ticket = ticketDAO.getTicket(vehicleRegNumber);
            outTime = new Date();
            ticket.setOutTime(outTime);

            fareCalculatorService.calculateFare(ticket);

            //Verify if is regular vehicle
            if(isRegularVehicle(ticket.getVehicleRegNumber())){
                //Apply the discount 5%
                ticket.setPrice(ticket.getPrice() - ( ticket.getPrice() * 5.0 / 100.0));
                LOG.info("You have benefit from a 5 percent discount.");
            }

            if(ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);

                LOG.info("Please pay the parking fare: " + ticket.getPrice());
                LOG.info("Recorded out-time for vehicle number:  "+ ticket.getVehicleRegNumber() +" is : " + outTime );

            }else{
                LOG.info("Unable to update ticket information. Error occurred");
            }
        }catch(Exception e){
            LOG.error("Unable to process exiting vehicle",e);
        }
    }


    /**
     * Vérify if VEHICLE_REG_NUMBER is  regular
     *
     * @param vehicleRegNumber
     * @return return true if is reccurent else return false
     * @author Abel
     */
    public boolean isRegularVehicle(String vehicleRegNumber){
        Integer differentdaysFound1 =  ticketDAO.getReccurentTicket(vehicleRegNumber);

        return differentdaysFound1 >= NB_JOURS_MINI;
    }


}
