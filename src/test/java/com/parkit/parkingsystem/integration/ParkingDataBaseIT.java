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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
        ticketDAO = new TicketDAO(dataBaseTestConfig);

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
    void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        final Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket);

        final boolean result = parkingSpotDAO.updateParking(ticket.getParkingSpot());

        assertTrue(result);
    }

    @Test
     void testParkingLotExit(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket;
        FareCalculatorService fareCalculatorService = new FareCalculatorService();

        ticket = ticketDAO.getTicket("ABCDEF");

       //get current TimeOut and add 2 Hours
       Date leftDate = new Date(new Date().toInstant().toEpochMilli() +(2*3600*1000));

       ticket.setOutTime(leftDate);

       fareCalculatorService.calculateFare(ticket);

        if(ticketDAO.updateTicket(ticket)){
            System.out.println("Ticket have been updated!");
        }

        parkingService.processExitingVehicle();

        DecimalFormat df = new DecimalFormat("0.00");

        assertEquals(df.format(3.00), df.format(ticket.getPrice()));
    }
}
