package com.parkit.parkingsystem.model;

//import java.util.Calendar;
import java.util.Date;

//import com.parkit.parkingsystem.constants.ParkingType;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot == null ? null :  new ParkingSpot( parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());// == null ? null : (ParkingSpot)parkingSpot.clone();
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
    
        this.parkingSpot = parkingSpot == null ? null :  new ParkingSpot( parkingSpot.getId(), parkingSpot.getParkingType(), parkingSpot.isAvailable());
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
    	//inTime = new Date(inTime.getTime()); // pourquoi cela ne marche pas avec getOutTime() ????????,
       return this.inTime == null ? null : new Date(inTime.getTime()); 
       // return inTime;
    }

    public void setInTime(Date inTime) {
    	
        this.inTime = inTime == null ? null : new Date(inTime.getTime()); //pourquoi cette solution ne marche pas pour setOutTime ??????????????,
    }

    public Date getOutTime() {
   // 	outTime = new Date(outTime.getTime());
    	
    	return this.outTime == null ? null : new Date(outTime.getTime());
      //  return outTime;
    }

    public void setOutTime(Date outTime) {
    	
        this.outTime = outTime == null ? null : new Date(outTime.getTime());//(outTime ==null ? null : (Date)outTime.clone());//outTime; //new Date(outTime.getTime());
    }
}
