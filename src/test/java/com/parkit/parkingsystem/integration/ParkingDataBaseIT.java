package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();


        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        final Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket);

        final boolean result = parkingSpotDAO.updateParking(ticket.getParkingSpot());

        assertTrue(result);
    }

    @Test
    public void testParkingLotExit(){
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(ticket.getInTime().toInstant().toEpochMilli() -(2*3600*1000)));

        //long ticketInTimePlus2hours = ticket.getInTime().toInstant().toEpochMilli() +(2*3600*1000);
        //ticket.setOutTime(new Date(ticketInTimePlus2hours));
        ticketDAO.updateTicket(ticket);

        parkingService.processExitingVehicle();

        ticket = ticketDAO.getTicket("ABCDEF");

        long inHourMilliToHour = (ticket.getInTime().toInstant().toEpochMilli() / 3600) ;
        long outHourMilliToHour = (ticket.getOutTime().toInstant().toEpochMilli() / 3600);

        //Duration should be 2 Hours
        float duration = (float)  (outHourMilliToHour - inHourMilliToHour) / 1000;

        double expectedPrice = duration * Fare.CAR_RATE_PER_HOUR; //CAR_RATE_PER_HOUR

        assertEquals(3.00, ticket.getPrice());
    }

}
