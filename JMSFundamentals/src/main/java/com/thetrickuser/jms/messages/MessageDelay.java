package com.thetrickuser.jms.messages;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageDelay {
    public static void main(String[] args) throws NamingException {
        // create an initial context by specifying it in jndi.properties file
        InitialContext initialContext = new InitialContext();

        // create a queue to for sending requests
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");

        // create a queue to receive replies
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {

            // create a producer to send messages
            JMSProducer requestProducer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage("Hello from producer");
            requestProducer.setDeliveryDelay(5000);
            requestProducer.send(requestQueue, message);

            JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
            TextMessage receivedMessage = (TextMessage) consumer.receive(2000);
            System.out.println(receivedMessage.getText());

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
