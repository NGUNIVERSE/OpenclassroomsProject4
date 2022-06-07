package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.parkit.parkingsystem.constants.ParkingType.BIKE;
import static com.parkit.parkingsystem.constants.ParkingType.CAR;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;



   @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    } 
    

    @Test
    public void processExitingVehicleTest(){
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");     
        when(ticketDAO.getTicketWithOutTimeNull("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);

        
        parkingService.processExitingVehicle();
        
        assertThat(ticket.getPrice()).isGreaterThan(0);
        ArgumentCaptor<ParkingSpot> parkingSpotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotDAO ,  Mockito.times(1)).updateParking(parkingSpotCaptor.capture());
        ParkingSpot updatedParkingSpot = parkingSpotCaptor.getValue();
        assertThat(updatedParkingSpot.isAvailable()).isTrue();
        assertThat(updatedParkingSpot.getId()).isEqualTo(1);
    }
    
    @Test
    public void processIncomingCarTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(parkingSpotDAO.getNextAvailableSlot(CAR)).thenReturn(1);

        parkingService.processIncomingVehicle();

        ArgumentCaptor<ParkingSpot> parkingSpotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotDAO).updateParking(parkingSpotCaptor.capture());
        ParkingSpot updatedParkingSpot = parkingSpotCaptor.getValue();
        assertThat(updatedParkingSpot.isAvailable()).isFalse();
        assertThat(updatedParkingSpot.getParkingType()).isEqualTo(CAR);
        assertThat(updatedParkingSpot.getId()).isEqualTo(1);

        ArgumentCaptor<Ticket> saveTicketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).saveTicket(saveTicketCaptor.capture());
        Ticket saveTicket = saveTicketCaptor.getValue();
        assertThat(saveTicket.getInTime()).isNotNull();
        assertThat(saveTicket.getVehicleRegNumber()).isEqualTo("ABCDEF");
        assertThat(saveTicket.getParkingSpot().getId()).isEqualTo(1);
        assertThat(saveTicket.getPrice()).isEqualTo(0.0);
        assertThat(saveTicket.getOutTime()).isNull();

    }

    @Test
    public void processIncomingBikeTest() {
        when(inputReaderUtil.readSelection()).thenReturn(2);

        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);

        parkingService.processIncomingVehicle();

        ArgumentCaptor<ParkingSpot> parkingSpotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotDAO).updateParking(parkingSpotCaptor.capture());
        ParkingSpot updatedParkingSpot = parkingSpotCaptor.getValue();
        assertThat(updatedParkingSpot.isAvailable()).isFalse();
        assertThat(updatedParkingSpot.getParkingType()).isEqualTo(ParkingType.BIKE);
        assertThat(updatedParkingSpot.getId()).isEqualTo(4);

        ArgumentCaptor<Ticket> saveTicketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).saveTicket(saveTicketCaptor.capture());
        Ticket saveTicket = saveTicketCaptor.getValue();
        assertThat(saveTicket.getInTime()).isNotNull();
        assertThat(saveTicket.getVehicleRegNumber()).isEqualTo("ABCDEF");
        assertThat(saveTicket.getParkingSpot().getId()).isEqualTo(4);
        assertThat(saveTicket.getPrice()).isEqualTo(0.0);
        assertThat(saveTicket.getOutTime()).isNull();

    }

    @Test
    public void processExitingCarTest() {
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setInTime(new Date(new Date().getTime() - 3600000));

        when(ticketDAO.getTicketWithOutTimeNull("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.isVehicleRecurrent("ABCDEF")).thenReturn(true);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDAO.updateParking(ticket.getParkingSpot())).thenReturn(true);

        parkingService.processExitingVehicle();

       
        
        ArgumentCaptor<ParkingSpot> parkingSpotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotDAO).updateParking(parkingSpotCaptor.capture());
        ParkingSpot updatedParkingSpot = parkingSpotCaptor.getValue();
        assertThat(updatedParkingSpot.isAvailable()).isTrue();

        ArgumentCaptor<Ticket> saveTicketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).updateTicket(saveTicketCaptor.capture());
        Ticket saveTicket = saveTicketCaptor.getValue();
        assertThat(saveTicket.getInTime()).isNotNull();
        assertThat(saveTicket.getVehicleRegNumber()).isEqualTo("ABCDEF");
        assertThat(saveTicket.getPrice()).isGreaterThanOrEqualTo(0.0);
        assertThat(saveTicket.getOutTime()).isNotNull().isCloseTo(new Date(), 1000);

    }

    @Test
    public void processExitingBikeTest() {
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(4, BIKE, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setInTime(new Date(new Date().getTime() - 3600000));

        when(ticketDAO.getTicketWithOutTimeNull("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.isVehicleRecurrent("ABCDEF")).thenReturn(true);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDAO.updateParking(ticket.getParkingSpot())).thenReturn(true);

        parkingService.processExitingVehicle();


        ArgumentCaptor<ParkingSpot> parkingSpotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
        verify(parkingSpotDAO).updateParking(parkingSpotCaptor.capture());
        ParkingSpot updatedParkingSpot = parkingSpotCaptor.getValue();
        assertThat(updatedParkingSpot.isAvailable()).isTrue();

        ArgumentCaptor<Ticket> saveTicketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).updateTicket(saveTicketCaptor.capture());
        Ticket saveTicket = saveTicketCaptor.getValue();
        assertThat(saveTicket.getInTime()).isNotNull();
        assertThat(saveTicket.getVehicleRegNumber()).isEqualTo("ABCDEF");
        assertThat(saveTicket.getPrice()).isGreaterThanOrEqualTo(0.0);
        assertThat(saveTicket.getOutTime()).isNotNull().isCloseTo(new Date(), 1000);

        }
    
    /***********************************************************************************************/
    @Test
    public void processIncomingCarWithAnExistingVehicleNumberTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1); 

        when(parkingSpotDAO.getNextAvailableSlot(CAR)).thenReturn(1);
        when(ticketDAO.isVehicleInTheParkingYet("ABCDEF")).thenReturn(true); 
        
        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, times(0)).updateParking(any());
        verify(ticketDAO, times(0)).saveTicket(any());


    }
    
    /*  @Test
    public void processExitingCarWithNoExistingVehicleNumberTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1); 

        when(parkingSpotDAO.getNextAvailableSlot(CAR)).thenReturn(1);
        when(ticketDAO.isVehicleInTheParkingYet("ABCDEF")).thenReturn(false); 
        
        parkingService.processExitingVehicle();

        verify(parkingSpotDAO, times(0)).updateParking(any());
        verify(ticketDAO, times(0)).saveTicket(any());


    } */
    
    /************************************************************************************************/

}
