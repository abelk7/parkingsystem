package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class InputReaderUtil {

    private static Scanner scan = new Scanner(System.in, "UTF-8");
    private static final Logger LOG = LogManager.getLogger("InputReaderUtil");

    public int readSelection() {
        try {
            return Integer.parseInt(scan.nextLine());
        }catch(Exception e){
            LOG.error("Error while reading user input from Shell", e);
            LOG.info("Error reading input. Please enter valid number for proceeding further");
            return -1;
        }
    }

    public String readVehicleRegistrationNumber() throws ParkingSystemException {
        try {
            String vehicleRegNumber= scan.nextLine();
            if(vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0 ) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        }catch(Exception e) {
            LOG.error("Error while reading user input from Shell", e);
            LOG.info("Error reading input. Please enter a valid string for vehicle registration number");
            throw e;
        }
    }


}
