/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author T0266882
 */

public class Trip {
    
    private String userID;
    private String messageID;
    private String placeName;
    private String lattitude, longitude;
    private String date; // No more than 14 days.
    
    public Trip(){}
    
    //public static newTrip(String uID, String msgID)
    
    public void setUserID(String uID){
        this.userID = uID;
    }
    
    public void setMsgID(String msgID){
        this.messageID = msgID;
    }
    
    public void setCoordinates(String lat, String lon){
        this.lattitude = lat;
        this.longitude = lon;
    }
    
    public void setDate(String dt){
        this.date = dt;
    }
    
    public String getUserID(){
        return userID;
    }
    
    public String getMsgID(){
        return messageID;
    }
    
    public String getCoordinates(){
        String coordinates = lattitude + "," + longitude;
        return coordinates;
    }
    
    public String getDate(){
        return date;
    }
}
