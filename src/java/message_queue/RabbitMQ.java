/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message_queue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author T0266882
 */

public class RabbitMQ {
    private String message;
    private static Connection connection = null;
    
    public static Connection ConnectRabbitMQ(){
        // Connect to the RabbitMQ server
        if(connection == null){
            try{
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("152.71.155.95"); // http://152.71.155.95:15672/
                factory.setUsername("student"); 
                factory.setPassword("COMP30231");
                connection = factory.newConnection();
            } catch(IOException | TimeoutException e){
                e.printStackTrace(); 
            }
        }
        
        return connection;
    }
    
    public void Publish(String EXCHANGE_NAME, String ROUTING_KEY, String message, String EXCHANGE_TYPE) throws IOException, TimeoutException
    {        
        try (Channel channel = ConnectRabbitMQ().createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
            
            channel.basicPublish(EXCHANGE_NAME, 
                    ROUTING_KEY, 
                    null,
                    message.getBytes(StandardCharsets.UTF_8));
            System.out.println("[x] Sent '" + ROUTING_KEY + ":" + message + "'");
        }
    }
    
    public String Subscribe(String EXCHANGE_NAME, String QUEUE_NAME, String EXCHANGE_TYPE, String ROUTING_KEY) throws Exception
    {
        
        // Connect to the RabbitMQ server
        Channel channel = ConnectRabbitMQ().createChannel();
        
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
        
        // Declare a subscriber-defined queue
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        
        // Link the queue to the exchange
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY); // The last parameter is the routing key usually used for direct or topic queues
        
        System.out.println("[*] Waiting for " + ROUTING_KEY +  " messages.");
        
        // This code block indicates a callback which is like an event triggered ONLY when a message is received
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            message = new String(delivery.getBody(), "UTF-8");
        };
        
        // Consume messages from the queue by using the callback
        String tag = channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        channel.basicCancel(tag);
        
        return message;
    }
}
