package message_queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

public class RabbitMQPublisher {
    
    public void Publish(String EXCHANGE_NAME, String ROUTING_KEY, String message, String EXCHANGE_TYPE) throws Exception
    {
        // Connect locally?
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("152.71.155.95"); 
        factory.setUsername("student"); 
        factory.setPassword("COMP30231");
        
        try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
            
            channel.basicPublish(EXCHANGE_NAME, 
                    ROUTING_KEY, 
                    null,
                    message.getBytes(StandardCharsets.UTF_8));
            System.out.println("[x] Sent '" + ROUTING_KEY + ":" + message + "'");
        }
    }
}
