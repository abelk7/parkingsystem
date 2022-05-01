package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = (ticket.getInTime().toInstant().toEpochMilli() / 3600) ;
        long outHour = (ticket.getOutTime().toInstant().toEpochMilli() / 3600);


        //TODO: Some tests are failing here. Need to check if this logic is correct
        float duration = (float)  (outHour - inHour) / 1000;

        if(duration <= 0.5){ //if the duration is less than or equal to half an hour, it's free for ALL
            ticket.setPrice(0.0);
            return;
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}