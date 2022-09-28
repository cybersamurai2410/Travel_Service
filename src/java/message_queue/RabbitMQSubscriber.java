package message_queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitMQSubscriber {
    
    public void Subscribe(String EXCHANGE_NAME, String QUEUE_NAME, String EXCHANGE_TYPE, String ROUTING_KEY) throws Exception
    {
        // Connect to the RabbitMQ server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("152.71.155.95"); 
        factory.setUsername("student"); 
        factory.setPassword("COMP30231");
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
        
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY); 
        
        System.out.println("[*] Waiting for " + ROUTING_KEY +  " messages.");
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("[x] Received '" + message + "'");
        };
        
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        
    }
}
