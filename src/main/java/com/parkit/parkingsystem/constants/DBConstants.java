package com.parkit.parkingsystem.constants;

public class DBConstants {

    private DBConstants(){
        //Empty constructor for static class
    }

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE is true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID IN (select ID from (select ID from ticket where VEHICLE_REG_NUMBER=? ) as t)";

    public static final String DELETE_TICKET = "delete from ticket where ID=?";
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t, parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is NULL";

    //Dedicated for test
    public static final String GET_TICKET_TEST = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t, parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=?";

    //Verify if is reccurent vehicle (by VEHICLE_REG_NUMBER)
    public static final String GET_RECURRENT_VEHICLE = "select count(*) as nb_ticket from ticket where ticket.VEHICLE_REG_NUMBER = ? and ticket.OUT_TIME is not NULL and datediff(IN_TIME,CURRENT_TIMESTAMP) <= ?";


     //check if vehicle is in the parking
    public static final String GET_TICKET_ALREADY_IN_PARKING_AND_NOT_EXIT = "select count(*) as nb_found from ticket where ticket.VEHICLE_REG_NUMBER=? and ticket.OUT_TIME is null";


}
