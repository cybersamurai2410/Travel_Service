/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;

import connection_manager.ConnectionManager;
import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import message_queue.RabbitMQ;

/**
 *
 * @author T0266882
 */

public class UserClient {
    
    private final static String address = "http://localhost:8080/Travel_Service/webresources/orchestrator/";
    
    private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};
    private final static String TRAVEL_OFFERS = "travel_offers", TRAVEL_INTENT = "travel_intent", TRAVEL_TEST = "travel test";
    private final static String Query = "query", Intent = "intent", Check_Intent = "check_intent", Trip_Proposal = "trip_proposal";
    
    public static void main(String  args[]) throws IOException, TimeoutException{
        
        ConnectionManager connect = new ConnectionManager();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        
        Trip trip = new Trip();
        List<Trip> tripList = new ArrayList<Trip>();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        
        File tripData = new File("TripProposal.json");
        FileWriter storeTrip = new FileWriter("TripProposal.json");
        
        // Generate user ID
        String userID = connect.getService(address+"generateID");
        System.out.println(userID);
        
        /*
        Menu corresponds to: 
        1) Query message
        2) Intent message
        3) Check intent message
        4) Submit trip proposal
        */
        String menu = "[1] Retrieve upcoming trips "
                + "\n[2] Notify intent for trip " //store data
                + "\n[3] Check intent for trip "
                + "\n[4] Submit trip proposal " //store data
                + "\n[5] Exit\n\n"
                + "[6] Test service\n";
        
        String response, msg; 
        int option = 0;
        while(option != 5) 
        {
            System.out.println(menu + "\nSelect one of the following options: ");
            option = Integer.parseInt(br.readLine());
        
            switch(option)
            {
//                case 1:
//                    response = connect.serviceCall("http://localhost:8080/Travel_Service/webresources/orchestrator/queryMessage", "GET");
//                    trip = gson.fromJson(response, Trip.class);
//                    System.out.println(trip.getClass()); // check if date is less than 14 days
//                    
//                    // append weather forecast (set params)
//                    String weather = connect.serviceCall("http://localhost:8080/Travel_Service/webresources/orchestrator/getWeather?", "GET");
//                break;
//                case 2:
//                    msg = "test"; // class for trip interest?
//                    connect.serviceCall("http://localhost:8080/Travel_Service/webresources/orchestrator/intentMessage?message="+msg, "PUT");
//                break;
//                case 3:
//                    response = connect.serviceCall("http://localhost:8080/Travel_Service/webresources/orchestrator/checkIntent", "GET");
//                break;
//                case 4:
//                    //trip.setUserID(userID); 
//                    trip.setMsgID("proposal"); 
//                    trip.setCoordinates("1", "1"); // external service
//                    trip.setDate(formatter.format(date));
//                    
//                    msg = gson.toJson(trip);
//                    storeTrip.write(msg);
//                    storeTrip.close();
//                    connect.postService("http://localhost:8080/Travel_Service/webresources/orchestrator/submitProposal/"+userID+"?message="+URLEncoder.encode(msg));
//                    System.out.println("POST trip proposal...");
//                break;
                case 5:
                    System.out.println("Exiting service...");
                break;
                
                // TEST CASES
                case 6:
                    //trip.setUserID(userID); 
                    trip.setMsgID("1"); 
                    trip.setCoordinates("1", "1");
                    trip.setDate(formatter.format(date));
                    tripList.add(trip);
                    
                    msg =gson.toJson(trip);
                    response = connect.getService("http://localhost:8080/Travel_Service/webresources/orchestrator/getJson/3?content="+URLEncoder.encode(msg, "UTF-8"));
                    System.out.println("Answer: "+response);
                  
//                    trip = gson.fromJson(response, Trip.class);
//                    System.out.println(trip.getCoordinates());
                    
                    msg = "hello world";
//                    RabbitMQ mq = new RabbitMQ();
//                    mq.Publish(TRAVEL_TEST, Trip_Proposal, msg, EXCHANGE_TYPE.DIRECT.toString().toLowerCase());
                    //connect.postService("http://localhost:8080/Travel_Service/webresources/orchestrator/postService/", msg);
                    System.out.println("Submitted proposal...");
                    response = connect.getService("http://localhost:8080/Travel_Service/webresources/orchestrator/getMessage/");
                    System.out.println(response);
                    System.out.println("Recieved proposal...");
                    
                break;
                default:
                    System.out.println("[Invalid Entry] Please try again.\n");
            }
        }
        br.close();
        //System.exit(1);
    } 
}

/*
Requirements:
- Send message as JSON object.
- Exception handling to check if correct JSON format is recieved.
    return String.format("{\"name\":\"%s\",\"address\":\"%s\",\"phoneNumber\":\"%s\"}", name, address, phoneNumber);
*/
