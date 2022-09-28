/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service_orchestrator;

import connection_manager.ConnectionManager;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import message_queue.RabbitMQ;

import message_queue.RabbitMQPublisher;
import message_queue.RabbitMQSubscriber;

import client.Trip;

/**
 * REST Web Service
 *
 * @author T0266882
 */

@Path("orchestrator")
public class Orchestrator {
    
    private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};
    
    // Exchanges
    private final static String TRAVEL_OFFERS = "travel_offers", TRAVEL_INTENT = "travel_intent", TRAVEL_TEST = "travel test";
    
    // Queues/Routing-keys
    private final static String Query = "query", Intent = "intent", Check_Intent = "check_intent", Trip_Proposal = "trip_proposal";
    
    RabbitMQPublisher publisher = new RabbitMQPublisher();
    RabbitMQSubscriber subscriber = new RabbitMQSubscriber();
    
    RabbitMQ mq = new RabbitMQ();
    ConnectionManager connect = new ConnectionManager();

    @Context
    private UriInfo context;

    public Orchestrator() {
    }
    
    // <--- External Sevices --->
    
    @GET
    @Path("/generateID")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public String generateID() {
        
        return "1";
    }
    
    @GET
    @Path("/getWeather")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // Retrieve weather forecast
    public String getWeather() {
        throw new UnsupportedOperationException();
    } 
    
    // <--- Message Types --->
    
    @GET
    @Path("/queryMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    // Retrieve information about upcoming trips.
    // Response should contain trip proposals and append weather forecast from external service.
    public void queryMessage() throws Exception {
        subscriber.Subscribe(TRAVEL_OFFERS, Query, EXCHANGE_TYPE.DIRECT.toString().toLowerCase(), Trip_Proposal);
    }
    
    @PUT
    @Path("/intentMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    // Notify a user who has published a trip proposal that another user is interested in the invite.
    /*
        Message includes:
        - user ID
        - ID of user that has submitted the proposal.
        - message ID
    */
    public void intentMessage(@QueryParam("id")String id) throws Exception{
        publisher.Publish(TRAVEL_INTENT, Intent+".travel", id, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
    }
    
    @GET
    @Path("/checkIntent")
    @Consumes(MediaType.APPLICATION_JSON)
    // Retrieve information about other users’ interest in the user’s trip proposal.
    public void checkIntent() throws Exception{
        subscriber.Subscribe(TRAVEL_INTENT, Check_Intent, EXCHANGE_TYPE.TOPIC.toString().toLowerCase(), Intent+".*");
    }
    
    @POST
    @Path("/submitProposal/{userID}")
    @Consumes(MediaType.APPLICATION_JSON)
    // Notify other users about a trip proposal.
    /*
        Message includes:
        - user ID (sender/reciever)
        - message ID
        - coordinates/name of place
        - proposed trip date (must be <= 14 days)
    */
    public void submitProposal(@PathParam("userID")String uid, @QueryParam("message")String msg) throws Exception{
        publisher.Publish(TRAVEL_OFFERS, Trip_Proposal, msg, EXCHANGE_TYPE.DIRECT.toString().toLowerCase());
        // fanout?
    }

    // TESTING REST SERVICES
    @POST
    @Path("/getService")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("x") int x, @QueryParam("y") int y) {
        //TODO return proper representation object
        return "" + (x + y);
    }
    @GET
    @Path("/getJson/{userID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@PathParam("userID")String uid, @QueryParam("content") String content) {
        System.out.println("hello");
        return content;
    }
    
    // MESSAGE TESTS
    @POST
    @Path("/postService")
    @Consumes(MediaType.APPLICATION_JSON)
    public void postMessage(String message) throws Exception {
        mq.Publish(TRAVEL_TEST, Trip_Proposal, message, EXCHANGE_TYPE.DIRECT.toString().toLowerCase());
    }
    @GET
    @Path("/getMessage")
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessage() throws Exception {
        //mq.Publish(TRAVEL_TEST, Trip_Proposal, msg, EXCHANGE_TYPE.DIRECT.toString().toLowerCase());
        String message = mq.Subscribe(TRAVEL_TEST, Query, EXCHANGE_TYPE.DIRECT.toString().toLowerCase(), Trip_Proposal);
        return message;
    }
    
    @POST
    @Path("/postTrip")
    @Consumes(MediaType.APPLICATION_JSON)
    public void postTrip(Trip trip) throws Exception {
        // convert trip to json
        // publish json
    }
}

/*
    http://localhost:8080/Travel_Service/webresources/orchestrator/<rest method>
 */

/*
    @Consumes -> void functions
    @Produces -> return type functions
    - can be used together

    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/users/{userid}") -> localhost:8080/UserManagement/rest/UserService/users/1 - https://docs.oracle.com/javaee/7/api/javax/ws/rs/PathParam.html
*/
