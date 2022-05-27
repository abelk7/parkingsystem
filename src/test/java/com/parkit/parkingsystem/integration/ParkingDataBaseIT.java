package com.parkit.parkingsystem.integration;

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
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.DecimalFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingDataBaseIT {

    private static final Logger LOG = LogManager.getLogger("ParkingDataBaseIT");

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
        ticketDAO = new TicketDAO(dataBaseTestConfig);

        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
    }

    @AfterAll
    public static void tearDown(){
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void testParkingACar(){
        final Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket);

        final boolean result = parkingSpotDAO.updateParking(ticket.getParkingSpot());

        assertTrue(result);
    }

    @Test
     void testParkingLotExit(){
        Ticket ticket;
        FareCalculatorService fareCalculatorService = new FareCalculatorService();

        ticket = ticketDAO.getTicket("ABCDEF");

       //get current TimeOut and add 2 Hours
       Date leftDate = new Date(new Date().toInstant().toEpochMilli() +(2*3600*1000));

       ticket.setOutTime(leftDate);

       fareCalculatorService.calculateFare(ticket);

        if(ticketDAO.updateTicket(ticket)){
            LOG.debug("Ticket have been updated!");
        }

        DecimalFormat df = new DecimalFormat("0.00");

        assertEquals(df.format(3.00), df.format(ticket.getPrice()));
    }

    @Test
    void testExitingACar() {
        Ticket ticket;

        try{
            Thread.sleep(2000);
        }catch (Exception e){
            LOG.error(e);
        }

        parkingService.processExitingVehicle();

        ticket = ticketDAO.getTicketTest("ABCDEF");

        DecimalFormat df = new DecimalFormat("0.00");

        assertEquals(df.format(0.0), df.format(ticket.getPrice()));
    }

}
