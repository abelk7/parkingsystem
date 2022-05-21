package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractiveShell {

    private InteractiveShell() {
        //Empty constructor
    }

    private static final Logger LOG = LogManager.getLogger("InteractiveShell");

    public static void loadInterface(){
        LOG.info("App initialized!!!");
        LOG.info("Welcome to Parking System!");

        boolean continueApp = true;
        InputReaderUtil inputReaderUtil = new InputReaderUtil();
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO(new DataBaseConfig());
        TicketDAO ticketDAO = new TicketDAO(new DataBaseConfig());
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        while(continueApp){
            loadMenu();
            int option = inputReaderUtil.readSelection();
            switch(option){
                case 1: {
                    parkingService.processIncomingVehicle();
                    break;
                }
                case 2: {
                    parkingService.processExitingVehicle();
                    break;
                }
                case 3: {
                    LOG.info("Exiting from the system!");
                    continueApp = false;
                    break;
                }
                default:
                    LOG.info("Unsupported option. Please enter a number corresponding to the provided menu");
            }
        }
    }

    private static void loadMenu(){
        LOG.info("Please select an option. Simply enter the number to choose an action");
        LOG.info("1 New Vehicle Entering - Allocate Parking Space");
        LOG.info("2 Vehicle Exiting - Generate Ticket Price");
        LOG.info("3 Shutdown System");
    }

}
