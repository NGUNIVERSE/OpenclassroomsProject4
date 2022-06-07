package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean isUserRecurrent){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
  
        long inHour = ticket.getInTime().getTime();   

        long outHour = ticket.getOutTime().getTime(); 

        //TODO: Some tests are failing here. Need to check if this logic is correct
        

        double soustraction = outHour - inHour;                          
          double duration =  ((((soustraction)/1000)/60)/60);
          
          if(duration < 0.5) // Less than 30 min free parking fare
          {
        	  duration = 0;
          }
          else if(isUserRecurrent == true) // 5% discount
          {
        	  duration = (duration) - (0.05*duration);
          } 
       
        switch (ticket.getParkingSpot().getParkingType()){     
            case CAR: {
                ticket.setPrice(Math.round(duration * Fare.CAR_RATE_PER_HOUR*100.0)/100.0);	// arrondir à deux chiffres après la virgule
                System.out.println(Math.round(duration * Fare.CAR_RATE_PER_HOUR*100.0/100.0));
                break;
            }
            case BIKE: {
                ticket.setPrice((double)Math.round(duration * Fare.BIKE_RATE_PER_HOUR*100.0)/100.0);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}